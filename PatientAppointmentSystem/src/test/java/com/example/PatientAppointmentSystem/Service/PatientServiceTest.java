package com.example.PatientAppointmentSystem.Service;

import com.example.PatientAppointmentSystem.Entity.Patient;
import com.example.PatientAppointmentSystem.Exception.ResourceAlreadyExistsException;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Exception.UnauthorizedException;
import com.example.PatientAppointmentSystem.Repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient = new Patient("John Doe", "john.doe@example.com", "securePass123", "123-456-7890");
    }

    @Test
    void testRegisterPatient_ShouldSavePatient_WhenEmailDoesNotExist() {
        // Arrange
        when(patientRepository.existsByEmail(patient.getEmail())).thenReturn(false);
        when(patientRepository.save(patient)).thenReturn(patient);

        // Act
        Patient savedPatient = patientService.registerPatient(patient);

        // Assert
        assertThat(savedPatient).isEqualTo(patient);
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void testRegisterPatient_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(patientRepository.existsByEmail(patient.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> patientService.registerPatient(patient))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Patient with email john.doe@example.com already exists");

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void testAuthenticatePatient_ShouldReturnPatient_WhenCredentialsAreCorrect() {
        // Arrange
        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.of(patient));

        // Act
        Patient authenticatedPatient = patientService.authenticatePatient("john.doe@example.com", "securePass123");

        // Assert
        assertThat(authenticatedPatient).isEqualTo(patient);
    }

    @Test
    void testAuthenticatePatient_ShouldThrowException_WhenEmailNotFound() {
        // Arrange
        when(patientRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.authenticatePatient("nonexistent@example.com", "securePass123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found with email: nonexistent@example.com");
    }

    @Test
    void testAuthenticatePatient_ShouldThrowException_WhenPasswordIsIncorrect() {
        // Arrange
        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.of(patient));

        // Act & Assert
        assertThatThrownBy(() -> patientService.authenticatePatient("john.doe@example.com", "wrongPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid password");
    }

    @Test
    void testGetPatientById_ShouldReturnPatient_WhenIdExists() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        Patient foundPatient = patientService.getPatientById(1L);

        // Assert
        assertThat(foundPatient).isEqualTo(patient);
    }

    @Test
    void testGetPatientById_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.getPatientById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found with id: 99");
    }

    @Test
    void testUpdatePatient_ShouldUpdatePatient_WhenIdExists() {
        // Arrange
        Patient updatedPatient = new Patient("John Updated", "updated@example.com", "newPass123", "987-654-3210");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        // Act
        Patient savedPatient = patientService.updatePatient(1L, updatedPatient);

        // Assert
        assertThat(savedPatient.getName()).isEqualTo("John Updated");
        assertThat(savedPatient.getEmail()).isEqualTo("updated@example.com");
        assertThat(savedPatient.getPhoneNumber()).isEqualTo("987-654-3210");
    }

    @Test
    void testUpdatePatient_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        Patient updatedPatient = new Patient("John Updated", "updated@example.com", "newPass123", "987-654-3210");
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.updatePatient(99L, updatedPatient))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found with id: 99");
    }

    @Test
    void testDeletePatient_ShouldDeletePatient_WhenIdExists() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        doNothing().when(patientRepository).delete(patient);

        // Act
        patientService.deletePatient(1L);

        // Assert
        verify(patientRepository, times(1)).delete(patient);
    }

    @Test
    void testDeletePatient_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.deletePatient(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found with id: 99");
    }
}
