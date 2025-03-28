package com.example.PatientAppointmentSystem.Repository;

import com.example.PatientAppointmentSystem.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository  extends JpaRepository<Appointment, Long> {

}
