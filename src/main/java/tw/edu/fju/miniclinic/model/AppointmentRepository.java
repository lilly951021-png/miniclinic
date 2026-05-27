package tw.edu.fju.miniclinic.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 記得要加這一行
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByApptDate(LocalDate apptDate);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPatient(Patient patient);
    long countByApptDateBetween(LocalDate from, LocalDate to);
    List<Appointment> findByDoctorAndApptDate(Doctor doctor, LocalDate apptDate);  // 新加入
    // 新增：依照醫師的科別(specialization)分組統計掛號數
  @Query("SELECT a.doctor.department, COUNT(a) FROM Appointment a GROUP BY a.doctor.department")
List<Object[]> countAppointmentsByDepartment();
}