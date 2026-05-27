package tw.edu.fju.miniclinic.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentForm;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

@Controller
public class AppointmentApiController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    // === 頁面導向端點 ===

    @GetMapping("/appointment/new")
    public String newAppointmentForm(Model model) {
        model.addAttribute("form", new AppointmentForm());
        model.addAttribute("doctors", doctorRepo.findAll());
        return "appointment-new";
    }

   @PostMapping("/appointment/new")
    public String submitAppointment(
            @Valid @ModelAttribute("form") AppointmentForm form,   // 明確指定名稱為 "form"
            BindingResult result,                                   //緊接在 @Valid 參數之後
            Model model) {

        // 驗證失敗時，回到表單頁並補上需要的 Model 資料，避免前端破圖
        if (result.hasErrors()) {
            model.addAttribute("form", form);
            model.addAttribute("doctors", doctorRepo.findAll());
            return "appointment-new";
        }

        // --- 以下是你原本寫好的商務邏輯與資料庫比對 ---
        Patient patient = patientRepo.findById(form.getChartNo()).orElse(null);
        Doctor doctor = doctorRepo.findById(form.getDoctorId()).orElse(null);

        if (patient == null || doctor == null) {
            model.addAttribute("error", "查無此病歷號或醫師，請確認後重試");
            model.addAttribute("form", form);
            model.addAttribute("doctors", doctorRepo.findAll());
            return "appointment-new";
        }

        // 一切驗證通過，建立並存入掛號資料
        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setApptDate(LocalDate.parse(form.getApptDate()));
        appt.setTimeSlot(form.getTimeSlot());
        appt.setStatus("BOOKED");

        Appointment saved = appointmentRepo.save(appt);
        model.addAttribute("appointment", saved);
        return "appointment-result";
    }
    // === REST API 端點 (回傳 JSON) ===

    // 1. 回傳總掛號數
    @GetMapping("/api/appointments/count")
    @ResponseBody
    public Map<String, Long> getAppointmentCount() {
        return Collections.singletonMap("count", appointmentRepo.count());
    }

    // 2. 條件篩選掛號列表 (處理 date 與 doctorId 選填)
    @GetMapping("/api/appointments")
    @ResponseBody
    public List<Appointment> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {

        // 情況 A：依日期篩選
        if (date != null) {
            return appointmentRepo.findByApptDate(LocalDate.parse(date));
        }

        // 情況 B：依醫師 ID 篩選
        if (doctorId != null) {
            Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
            if (doctor != null) {
                return appointmentRepo.findByDoctor(doctor);
            }
            return Collections.emptyList();
        }

        // 情況 C：都沒傳，回傳全部紀錄
        return appointmentRepo.findAll();
    }

    @PostMapping("/api/appointments")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Map<String, String> request) {
        String chartNo = request.get("chartNo");
        String doctorId = request.get("doctorId");
        LocalDate apptDate = LocalDate.parse(request.get("apptDate"));
        String timeSlot = request.get("timeSlot");

        Patient patient = patientRepo.findById(chartNo).orElse(null);
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (patient == null || doctor == null) {
            return ResponseEntity.badRequest().build();
        }

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setApptDate(apptDate);
        appt.setTimeSlot(timeSlot);
        appt.setStatus("BOOKED");

        Appointment saved = appointmentRepo.save(appt);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/api/appointments/{apptId}/status")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long apptId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");

        Appointment appt = appointmentRepo.findById(apptId).orElse(null);
        if (appt == null) {
            return ResponseEntity.notFound().build();
        }

        // 只能修改自己的掛號
        if (!appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) {
            return ResponseEntity.status(403).build();
        }

        String newStatus = payload.get("status");
        if (!List.of("BOOKED", "COMPLETED", "CANCELLED").contains(newStatus)) {
            return ResponseEntity.badRequest().build();
        }

        appt.setStatus(newStatus);
        return ResponseEntity.ok(appointmentRepo.save(appt));
    }
}