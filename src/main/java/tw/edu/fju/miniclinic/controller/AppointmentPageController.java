package tw.edu.fju.miniclinic.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;

@Controller
public class AppointmentPageController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        // 1. 撈出所有的掛號紀錄，供給下方的「全院掛號紀錄清單」大表格使用
        List<Appointment> allAppointments = appointmentRepo.findAll();
        model.addAttribute("appointments", allAppointments);
        
        // ✨【核心修改點】：因為是直接從導覽列點進來的，我們設定 showSummary 為 false！
        // 告訴前端：不要顯示上半部那個討人厭的「掛號成功摘要」！
        model.addAttribute("showSummary", false);
        
        // 防空針防爆處理
        if (!allAppointments.isEmpty()) {
            model.addAttribute("appointment", allAppointments.get(0));
        } else {
            model.addAttribute("appointment", new Appointment());
        }
        
        return "appointments";
    }
}