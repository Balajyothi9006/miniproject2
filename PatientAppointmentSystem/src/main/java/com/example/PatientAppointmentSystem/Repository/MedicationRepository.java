package com.example.PatientAppointmentSystem.Repository;

import com.example.PatientAppointmentSystem.Entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
}
