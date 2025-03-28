package com.example.PatientAppointmentSystem.Service;




import com.example.PatientAppointmentSystem.Entity.Patient;
import com.example.PatientAppointmentSystem.Exception.ResourceAlreadyExistsException;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Exception.UnauthorizedException;
import com.example.PatientAppointmentSystem.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    /*
     * Register a new patient.

     */
    public Patient registerPatient(Patient patient) {
        // Check if a patient with the same email already exists
        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new ResourceAlreadyExistsException("Patient with email " + patient.getEmail() + " already exists");
        }
        return patientRepository.save(patient);
    }

    /*
     * Authenticate a patient.
     */
    public Patient authenticatePatient(String email, String password) {
        Optional<Patient> patientOptional = patientRepository.findByEmail(email);
        if (patientOptional.isEmpty()) {
            throw new ResourceNotFoundException("Patient not found with email: " + email);
        }
        Patient patient = patientOptional.get();
        if (!patient.getPassword().equals(password)) {
            try {
                throw new UnauthorizedException("Invalid password");
            } catch (UnauthorizedException e) {
                throw new RuntimeException(e);
            }
        }
        return patient;
    }

    /*
     * Get a patient by ID.

     */
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    /*
     * Update a patient's profile.

     */
    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        patient.setName(patientDetails.getName());
        patient.setEmail(patientDetails.getEmail());
        patient.setPassword(patientDetails.getPassword());
        patient.setPhoneNumber(patientDetails.getPhoneNumber());

        return patientRepository.save(patient);
    }


    /*
     * Delete a patient by ID.

     */
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);
    }
}
