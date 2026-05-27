package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;
import java.util.List;

@Controller
public class HomeController {

    // 注入這三個 Repository，讓 Controller 可以跟資料庫拿資料
    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/")  // get 請求連接根目錄
    public String home() {
        return "home";  // 對應 templates/home.html
    }

    // 新增：統計頁面路徑
    @GetMapping("/stats")
    public String getStats(Model model) {
        // 1. 使用 Repository 內建的 count() 方法取得總數
        model.addAttribute("doctorCount", doctorRepo.count());
        model.addAttribute("patientCount", patientRepo.count());
        model.addAttribute("appointmentCount", appointmentRepo.count());

        // 2. 使用你在 AppointmentRepository 裡面寫好的 @Query 方法
        List<Object[]> deptStats = appointmentRepo.countAppointmentsByDepartment();
        model.addAttribute("deptStats", deptStats);

        return "status";  // 這會去尋找 templates/stats.html
    }
}