package com.example.PatientAppointmentSystem.Repository;

import com.example.PatientAppointmentSystem.Entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository  extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    boolean existsByEmail(String email);

    Doctor findById(Doctor doctor);
}
