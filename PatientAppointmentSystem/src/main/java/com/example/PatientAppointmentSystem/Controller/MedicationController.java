package com.example.PatientAppointmentSystem.Controller;


import com.example.PatientAppointmentSystem.Entity.Medication;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/medications")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    // Show form to add a new medication
    @GetMapping("/add")
    public String showAddMedicationForm(Model model) {
        model.addAttribute("medication", new Medication());
        return "add-medication";
    }

    // Handle adding a new medication
    @PostMapping("/add")
    public String addMedication(@ModelAttribute Medication medication, Model model) {
        try {
            medicationService.addMedication(medication);
            return "redirect:/medications";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "add-medication";
        }
    }

    // List all medications
    @GetMapping
    public String listMedications(Model model) {
        List<Medication> medications = medicationService.getAllMedications();
        model.addAttribute("medications", medications);
        return "medications";
    }

    // Show medication details
    @GetMapping("/{id}")
    public String showMedicationDetails(@PathVariable Long id, Model model) {
        try {
            Medication medication = medicationService.getMedicationById(id);
            model.addAttribute("medication", medication);
            return "medication-details";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/medications";
        }
    }

    // Show form to update a medication
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        try {
            Medication medication = medicationService.getMedicationById(id);
            model.addAttribute("medication", medication);
            return "edit-medication";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/medications";
        }
    }

    // Handle medication update
    @PostMapping("/edit/{id}")
    public String updateMedication(@PathVariable Long id, @ModelAttribute Medication medicationDetails, Model model) {
        try {
            medicationService.updateMedication(id, medicationDetails);
            return "redirect:/medications";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "edit-medication";
        }
    }

    // Delete a medication
    @GetMapping("/delete/{id}")
    public String deleteMedication(@PathVariable Long id) {
        medicationService.deleteMedication(id);
        return "redirect:/medications";
    }
}