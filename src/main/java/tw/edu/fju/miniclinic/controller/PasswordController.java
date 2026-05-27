package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PasswordForm;

@Controller
@RequestMapping("/doctor/change-password")
public class PasswordController {

    @Autowired
    private DoctorRepository doctorRepo;

    // GET /password：顯示修改密碼表單
    @GetMapping
    public String showPasswordForm(HttpSession session, Model model) {
        String doctorName = (String) session.getAttribute("loggedInDoctorName");
        model.addAttribute("loggedInDoctorName", doctorName);
        model.addAttribute("passwordForm", new PasswordForm());
        return "password";
    }

    // POST /password：處理密碼修改邏輯
    @PostMapping
    public String changePassword(
            @ModelAttribute("passwordForm") PasswordForm form,
            HttpSession session,
            Model model) {
        
        String doctorId = (String) session.getAttribute("loggedInDoctorId");
        String doctorName = (String) session.getAttribute("loggedInDoctorName");
        model.addAttribute("loggedInDoctorName", doctorName);

        // 【安全防線】防止 Session 在未登入或重啟時傳入 null 導致伺服器 500 崩潰
        if (doctorId == null) {
            return "redirect:/login";
        }

        // 1. 以 Session 中的 loggedInDoctorId 查詢醫師
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) {
            return "redirect:/login";
        }

        // 2. 驗證舊密碼是否正確 (BCrypt 比對) - 精準對接原版變數名 errorMessage
        if (!BCrypt.checkpw(form.getOldPassword(), doctor.getPasswordHash())) {
            model.addAttribute("loggedInDoctorName", doctorName);
            model.addAttribute("errorMessage", "舊密碼錯誤");
            return "password";
        }

        // 3. 驗證新密碼與確認密碼是否一致 - 精準對接原版變數名 errorMessage
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
           model.addAttribute("loggedInDoctorName", doctorName);
            model.addAttribute("errorMessage", "兩次密碼不相符");
            return "password";
        }

        // 4. 驗證新密碼長度是否少於 8 碼 - 精準對接原版變數名 errorMessage
        if (form.getNewPassword() == null || form.getNewPassword().length() < 8) {
            model.addAttribute("loggedInDoctorName", doctorName);
            model.addAttribute("errorMessage", "密碼至少需要 8 個字元");
            return "password";
        }

        // 5. 全部驗證通過：將新密碼進行雜湊更新
        String hashedNewPassword = BCrypt.hashpw(form.getNewPassword(), BCrypt.gensalt());
        doctor.setPasswordHash(hashedNewPassword);
        doctorRepo.save(doctor);

        model.addAttribute("loggedInDoctorName", doctor.getName()); 
        model.addAttribute("successMessage", "密碼修改成功！");
        
        // 清空輸入框，讓畫面變乾淨
        model.addAttribute("passwordForm", new PasswordForm()); 
        
        return "password"; 
    } 
}