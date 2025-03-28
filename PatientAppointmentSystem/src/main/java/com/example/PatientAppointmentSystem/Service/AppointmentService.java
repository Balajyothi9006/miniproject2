package com.example.PatientAppointmentSystem.Service;


import com.example.PatientAppointmentSystem.Entity.Appointment;
import com.example.PatientAppointmentSystem.Entity.Doctor;
import com.example.PatientAppointmentSystem.Entity.Patient;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Repository.AppointmentRepository;
import com.example.PatientAppointmentSystem.Repository.DoctorRepository;
import com.example.PatientAppointmentSystem.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;


    public Appointment bookAppointment(Appointment appointment) {


        return appointmentRepository.save(appointment);
    }


    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }


    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    /*
      Get appointments by doctor ID.

     */
    public Optional<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findById(doctorId);
    }

    /*
     * Get appointments by patient ID.

     */
    public Optional<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findById(patientId);
    }

    /*
     * Update an appointment.

     */
    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        Doctor doctor = doctorRepository.findById(appointmentDetails.getDoctor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + appointmentDetails.getDoctor().getId()));
        Patient patient = patientRepository.findById(appointmentDetails.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + appointmentDetails.getPatient().getId()));

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDateTime(appointmentDetails.getAppointmentDateTime());

        return appointmentRepository.save(appointment);
    }

    /*
     * Delete an appointment by ID.

     */
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        appointmentRepository.delete(appointment);
    }
}
