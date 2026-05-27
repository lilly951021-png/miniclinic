package tw.edu.fju.miniclinic.controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import tw.edu.fju.miniclinic.model.PatientRepository;

    @Controller
public class PatientPageController {
   @Autowired
private PatientRepository patientRepo;

    @GetMapping("/patients")
    public String showPatientsPage(Model model) {
        // 1. 把資料塞進 Model 提籃
        model.addAttribute("patients", patientRepo.findAll());
        // 2. 回傳字串 = 模板名稱 (找出 patients.html)
        return "patients";
    }
}

