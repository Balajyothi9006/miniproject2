package com.example.PatientAppointmentSystem.Service;

import com.example.PatientAppointmentSystem.Entity.Medication;
import com.example.PatientAppointmentSystem.Entity.Patient;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Repository.MedicationRepository;
import com.example.PatientAppointmentSystem.Repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MedicationServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private MedicationService medicationService;

    private Medication medication;
    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        patient.setEmail("john.doe@example.com");
        patient.setPassword("securePass123");
        patient.setPhoneNumber("123-456-7890");

        medication = new Medication();
        medication.setId(1L);
        medication.setName("Paracetamol");
        medication.setDosage("500mg");
        medication.setInstructions("Take twice daily");
        medication.setPatient(patient);
    }

    @Test
    void testAddMedication_ShouldSaveAndReturnMedication_WhenPatientExists() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicationRepository.save(medication)).thenReturn(medication);

        // Act
        Medication savedMedication = medicationService.addMedication(medication);

        // Assert
        assertThat(savedMedication).isEqualTo(medication);
        verify(medicationRepository, times(1)).save(medication);
    }


    @Test
    void testGetMedicationById_ShouldReturnMedication_WhenIdExists() {
        // Arrange
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        // Act
        Medication foundMedication = medicationService.getMedicationById(1L);

        // Assert
        assertThat(foundMedication).isEqualTo(medication);
    }



    @Test
    void testGetAllMedications_ShouldReturnAllMedications() {
        // Arrange
        List<Medication> medicationList = List.of(medication);
        when(medicationRepository.findAll()).thenReturn(medicationList);

        // Act
        List<Medication> result = medicationService.getAllMedications();

        // Assert
        assertThat(result).isEqualTo(medicationList);
    }

    @Test
    void testUpdateMedication_ShouldUpdateAndReturnMedication_WhenIdExists() {
        // Arrange
        Medication updatedMedication = new Medication();
        updatedMedication.setName("Ibuprofen");
        updatedMedication.setDosage("200mg");
        updatedMedication.setInstructions("Take once daily");
        updatedMedication.setPatient(patient);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicationRepository.save(medication)).thenReturn(medication);

        // Act
        Medication result = medicationService.updateMedication(1L, updatedMedication);

        // Assert
        assertThat(result.getName()).isEqualTo(updatedMedication.getName());
        assertThat(result.getDosage()).isEqualTo(updatedMedication.getDosage());
        assertThat(result.getInstructions()).isEqualTo(updatedMedication.getInstructions());
    }


    @Test
    void testUpdateMedication_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        medication.getPatient().setId(99L);

        // Act & Assert
        assertThatThrownBy(() -> medicationService.updateMedication(1L, medication))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found with id: 99");
    }

    @Test
    void testDeleteMedication_ShouldDeleteMedication_WhenIdExists() {
        // Arrange
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        doNothing().when(medicationRepository).delete(medication);

        // Act
        medicationService.deleteMedication(1L);

        // Assert
        verify(medicationRepository, times(1)).delete(medication);
    }

    @Test
    void testDeleteMedication_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        when(medicationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> medicationService.deleteMedication(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Medication not found with id: 99");
    }
}
