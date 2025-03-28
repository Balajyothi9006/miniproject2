package com.example.PatientAppointmentSystem.Service;


import com.example.PatientAppointmentSystem.Entity.Medication;
import com.example.PatientAppointmentSystem.Entity.Patient;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Repository.MedicationRepository;
import com.example.PatientAppointmentSystem.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicationService {

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private PatientRepository patientRepository;

    /*
     * Add a new medication.

     */
    public Medication addMedication(Medication medication) {
        Patient patient = patientRepository.findById(medication.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + medication.getPatient().getId()));
        medication.setPatient(patient);
        return medicationRepository.save(medication);
    }

    /*
     * Get a medication by ID.

     */
    public Medication getMedicationById(Long id) {
        return medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));
    }

    /*
     * Get all medications.

     */
    public List<Medication> getAllMedications() {
        return medicationRepository.findAll();
    }

    /*
     * Get medications by patient ID.


     */
    public Medication updateMedication(Long id, Medication medicationDetails) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));

        Patient patient = patientRepository.findById(medicationDetails.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + medicationDetails.getPatient().getId()));

        medication.setName(medicationDetails.getName());
        medication.setDosage(medicationDetails.getDosage());
        medication.setInstructions(medicationDetails.getInstructions());
        medication.setPatient(patient);

        return medicationRepository.save(medication);
    }

    /*
     * Delete a medication by ID.

     */
    public void deleteMedication(Long id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));
        medicationRepository.delete(medication);
    }
}
