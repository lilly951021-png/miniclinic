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
        
        // 因為 appointment-result.html 上半部有用 ${appointment.apptId} 顯示剛剛成功的摘要
        // 從導覽列直接點進來時，我們塞一個預設的空物件進去，防止 Thymeleaf 解析錯誤爆炸。
        if (!allAppointments.isEmpty()) {
            // 如果資料庫本來就有資料，拿最新或第一筆塞進去填補欄位
            model.addAttribute("appointment", allAppointments.get(0));
        } else {
            // 如果是空資料庫，塞一個乾淨的空物件進去
            model.addAttribute("appointment", new Appointment());
        }
        
        // 3. 回傳你的歷史紀錄大表格網頁
        return "appointment-result";
    }
}