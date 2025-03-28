package com.example.PatientAppointmentSystem.Service;

import com.example.PatientAppointmentSystem.Entity.Doctor;
import com.example.PatientAppointmentSystem.Exception.ResourceAlreadyExistsException;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Exception.UnauthorizedException;
import com.example.PatientAppointmentSystem.Repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctor = new Doctor("Dr. John Smith", "john.smith@example.com", "securePass123", "Cardiology");
    }

    @Test
    void testRegisterDoctor_ShouldSaveDoctor_WhenEmailDoesNotExist() {
        // Arrange
        when(doctorRepository.existsByEmail(doctor.getEmail())).thenReturn(false);
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        // Act
        Doctor savedDoctor = doctorService.registerDoctor(doctor);

        // Assert
        assertThat(savedDoctor).isEqualTo(doctor);
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    void testRegisterDoctor_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(doctorRepository.existsByEmail(doctor.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> doctorService.registerDoctor(doctor))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Doctor with email john.smith@example.com already exists");

        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void testAuthenticateDoctor_ShouldReturnDoctor_WhenCredentialsAreCorrect() {
        // Arrange
        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.of(doctor));

        // Act
        Doctor authenticatedDoctor = doctorService.authenticateDoctor("john.smith@example.com", "securePass123");

        // Assert
        assertThat(authenticatedDoctor).isEqualTo(doctor);
    }

    @Test
    void testAuthenticateDoctor_ShouldThrowException_WhenEmailNotFound() {
        // Arrange
        when(doctorRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.authenticateDoctor("nonexistent@example.com", "securePass123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Doctor not found with email: nonexistent@example.com");
    }

    @Test
    void testAuthenticateDoctor_ShouldThrowException_WhenPasswordIsIncorrect() {
        // Arrange
        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.of(doctor));

        // Act & Assert
        assertThatThrownBy(() -> doctorService.authenticateDoctor("john.smith@example.com", "wrongPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid password");
    }

    @Test
    void testGetDoctorById_ShouldReturnDoctor_WhenIdExists() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        // Act
        Doctor foundDoctor = doctorService.getDoctorById(1L);

        // Assert
        assertThat(foundDoctor).isEqualTo(doctor);
    }

    @Test
    void testGetDoctorById_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.getDoctorById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Doctor not found with id: 99");
    }

    @Test
    void testUpdateDoctor_ShouldUpdateDoctor_WhenIdExists() {
        // Arrange
        Doctor updatedDoctor = new Doctor("Dr. John Updated", "updated@example.com", "newPass123", "Neurology");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);

        // Act
        Doctor savedDoctor = doctorService.updateDoctor(1L, updatedDoctor);

        // Assert
        assertThat(savedDoctor.getName()).isEqualTo("Dr. John Updated");
        assertThat(savedDoctor.getEmail()).isEqualTo("updated@example.com");
        assertThat(savedDoctor.getSpecialization()).isEqualTo("Neurology");
    }

    @Test
    void testUpdateDoctor_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        Doctor updatedDoctor = new Doctor("Dr. John Updated", "updated@example.com", "newPass123", "Neurology");
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.updateDoctor(99L, updatedDoctor))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Doctor not found with id: 99");
    }

    @Test
    void testDeleteDoctor_ShouldDeleteDoctor_WhenIdExists() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        doNothing().when(doctorRepository).delete(doctor);

        // Act
        doctorService.deleteDoctor(1L);

        // Assert
        verify(doctorRepository, times(1)).delete(doctor);
    }

    @Test
    void testDeleteDoctor_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.deleteDoctor(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Doctor not found with id: 99");
    }

    @Test
    void testGetAllDoctors_ShouldReturnListOfDoctors() {
        // Arrange
        List<Doctor> doctors = Arrays.asList(
                new Doctor("Dr. John Smith", "john.smith@example.com", "securePass123", "Cardiology"),
                new Doctor("Dr. Jane Doe", "jane.doe@example.com", "password456", "Pediatrics")
        );
        when(doctorRepository.findAll()).thenReturn(doctors);

        // Act
        List<Doctor> result = doctorService.getAllDoctors();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Dr. John Smith");
        assertThat(result.get(1).getName()).isEqualTo("Dr. Jane Doe");
    }

    @Test
    void testGetAllDoctors_ShouldReturnEmptyList_WhenNoDoctorsExist() {
        // Arrange
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Doctor> result = doctorService.getAllDoctors();

        // Assert
        assertThat(result).isEmpty();
    }
}
