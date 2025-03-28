package com.example.PatientAppointmentSystem.Service;


import com.example.PatientAppointmentSystem.Entity.Doctor;
import com.example.PatientAppointmentSystem.Exception.ResourceAlreadyExistsException;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Exception.UnauthorizedException;
import com.example.PatientAppointmentSystem.Repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    /*
     * Register a new doctor.

     */
    public Doctor registerDoctor(Doctor doctor) {
        if (doctorRepository.existsByEmail(doctor.getEmail())) {
            throw new ResourceAlreadyExistsException("Doctor with email " + doctor.getEmail() + " already exists");
        }
        return doctorRepository.save(doctor);
    }

    /*
     * Authenticate a doctor.

     */
    public Doctor authenticateDoctor(String email, String password) {
        Optional<Doctor> doctorOptional = doctorRepository.findByEmail(email);
        if (doctorOptional.isEmpty()) {
            throw new ResourceNotFoundException("Doctor not found with email: " + email);
        }
        Doctor doctor = doctorOptional.get();
        if (!doctor.getPassword().equals(password)) {
            try {
                throw new UnauthorizedException("Invalid password");
            } catch (UnauthorizedException e) {
                throw new RuntimeException(e);
            }
        }
        return doctor;
    }

    /*
     * Get a doctor by ID.

     */
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    /*
     * Update a doctor's profile.

     */
    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        doctor.setName(doctorDetails.getName());
        doctor.setEmail(doctorDetails.getEmail());
        doctor.setPassword(doctorDetails.getPassword());
        doctor.setSpecialization(doctorDetails.getSpecialization());

        return doctorRepository.save(doctor);
    }

    /*
     * Delete a doctor by ID.

     */
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        doctorRepository.delete(doctor);
    }


    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
}
