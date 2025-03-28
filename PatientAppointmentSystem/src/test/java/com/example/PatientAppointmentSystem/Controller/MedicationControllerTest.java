package com.example.PatientAppointmentSystem.Controller;

import com.example.PatientAppointmentSystem.Entity.Medication;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Service.MedicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MedicationControllerTest {

    @Mock
    private MedicationService medicationService;

    @Mock
    private Model model;

    @InjectMocks
    private MedicationController medicationController;

    private Medication medication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        medication = new Medication();
        medication.setId(1L);
        medication.setName("Paracetamol");
        medication.setDosage("500mg");
        medication.setInstructions("Take one tablet after meal.");
    }

    @Test
    void testShowAddMedicationForm_ShouldReturnAddMedicationPage() {
        // Act
        String viewName = medicationController.showAddMedicationForm(model);

        // Assert
        assertThat(viewName).isEqualTo("add-medication");
        verify(model, times(1)).addAttribute(eq("medication"), any(Medication.class));
    }

    @Test
    void testAddMedication_ShouldRedirectToMedications_WhenSuccess() {
        // Act
        String viewName = medicationController.addMedication(medication, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/medications");
        verify(medicationService, times(1)).addMedication(medication);
    }

    @Test
    void testAddMedication_ShouldReturnAddMedicationPage_WhenPatientNotFound() {
        // Arrange
        doThrow(new ResourceNotFoundException("Patient not found")).when(medicationService).addMedication(medication);

        // Act
        String viewName = medicationController.addMedication(medication, model);

        // Assert
        assertThat(viewName).isEqualTo("add-medication");
        verify(model, times(1)).addAttribute("error", "Patient not found");
    }

    @Test
    void testListMedications_ShouldReturnMedicationsPage() {
        // Arrange
        List<Medication> medications = List.of(medication);
        when(medicationService.getAllMedications()).thenReturn(medications);

        // Act
        String viewName = medicationController.listMedications(model);

        // Assert
        assertThat(viewName).isEqualTo("medications");
        verify(model, times(1)).addAttribute("medications", medications);
    }

    @Test
    void testShowMedicationDetails_ShouldReturnMedicationDetails_WhenMedicationExists() {
        // Arrange
        when(medicationService.getMedicationById(1L)).thenReturn(medication);

        // Act
        String viewName = medicationController.showMedicationDetails(1L, model);

        // Assert
        assertThat(viewName).isEqualTo("medication-details");
        verify(model, times(1)).addAttribute("medication", medication);
    }


    @Test
    void testShowUpdateForm_ShouldRedirectToMedications_WhenMedicationNotFound() {
        // Arrange
        when(medicationService.getMedicationById(99L)).thenThrow(new ResourceNotFoundException("Medication not found"));

        // Act
        String viewName = medicationController.showUpdateForm(99L, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/medications");
        verify(model, times(1)).addAttribute("error", "Medication not found");
    }

    @Test
    void testUpdateMedication_ShouldRedirectToMedications_WhenSuccess() {
        // Act
        String viewName = medicationController.updateMedication(1L, medication, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/medications");
        verify(medicationService, times(1)).updateMedication(1L, medication);
    }

    @Test
    void testUpdateMedication_ShouldReturnEditMedicationPage_WhenMedicationNotFound() {
        // Arrange
        doThrow(new ResourceNotFoundException("Medication not found")).when(medicationService).updateMedication(1L, medication);

        // Act
        String viewName = medicationController.updateMedication(1L, medication, model);

        // Assert
        assertThat(viewName).isEqualTo("edit-medication");
        verify(model, times(1)).addAttribute("error", "Medication not found");
    }

    @Test
    void testDeleteMedication_ShouldRedirectToMedications_WhenSuccess() {
        // Act
        String viewName = medicationController.deleteMedication(1L);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/medications");
        verify(medicationService, times(1)).deleteMedication(1L);
    }
}
