package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;
import java.util.List;

@RestController // 關鍵字：告訴 Spring 我要直接回傳資料，不找美編 (Thymeleaf)
public class PatientApiController {

    // 建立倉庫實例 (通常實務上會用注入的，現在先 new 出來)
    @Autowired
    private PatientRepository patientRepo; 
    @GetMapping("/api/patients") // 定義網址路徑
    public List<Patient> getPatientsJson() {
        // 直接 return Java 的 List 物件
        // Spring 看到後會啟動「自動序列化」，把它翻譯成 JSON
        return patientRepo.findAll();
    }
}