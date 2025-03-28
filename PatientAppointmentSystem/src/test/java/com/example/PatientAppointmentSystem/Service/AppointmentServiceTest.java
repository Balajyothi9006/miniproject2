package com.example.PatientAppointmentSystem.Service;

import com.example.PatientAppointmentSystem.Entity.Appointment;
import com.example.PatientAppointmentSystem.Entity.Doctor;
import com.example.PatientAppointmentSystem.Entity.Patient;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Repository.AppointmentRepository;
import com.example.PatientAppointmentSystem.Repository.DoctorRepository;
import com.example.PatientAppointmentSystem.Repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment appointment;
    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctor = new Doctor("Dr. Smith", "smith@example.com", "password123", "Cardiology");
        doctor.setId(1L);

        patient = new Patient("John Doe", "john.doe@example.com", "securePass123", "123-456-7890");
        patient.setId(1L);

        appointment = new Appointment(1L, LocalDateTime.now().plusDays(1),patient, doctor );
    }

    @Test
    void testBookAppointment_ShouldSaveAndReturnAppointment() {
        // Arrange
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        // Act
        Appointment savedAppointment = appointmentService.bookAppointment(appointment);

        // Assert
        assertThat(savedAppointment).isEqualTo(appointment);
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void testGetAppointmentById_ShouldReturnAppointment_WhenIdExists() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        // Act
        Appointment foundAppointment = appointmentService.getAppointmentById(1L);

        // Assert
        assertThat(foundAppointment).isEqualTo(appointment);
    }

    @Test
    void testGetAppointmentById_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> appointmentService.getAppointmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Appointment not found with id: 99");
    }

    @Test
    void testGetAllAppointments_ShouldReturnAllAppointments() {
        // Arrange
        List<Appointment> appointmentList = List.of(appointment);
        when(appointmentRepository.findAll()).thenReturn(appointmentList);

        // Act
        List<Appointment> result = appointmentService.getAllAppointments();

        // Assert
        assertThat(result).isEqualTo(appointmentList);
    }

    @Test
    void testGetAppointmentsByDoctorId_ShouldReturnAppointment_WhenDoctorIdExists() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        // Act
        Optional<Appointment> result = appointmentService.getAppointmentsByDoctorId(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(appointment);
    }

    @Test
    void testGetAppointmentsByDoctorId_ShouldReturnEmpty_WhenDoctorIdDoesNotExist() {
        // Arrange
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Appointment> result = appointmentService.getAppointmentsByDoctorId(99L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAppointmentsByPatientId_ShouldReturnAppointment_WhenPatientIdExists() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        // Act
        Optional<Appointment> result = appointmentService.getAppointmentsByPatientId(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(appointment);
    }

    @Test
    void testGetAppointmentsByPatientId_ShouldReturnEmpty_WhenPatientIdDoesNotExist() {
        // Arrange
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Appointment> result = appointmentService.getAppointmentsByPatientId(99L);

        // Assert
        assertThat(result).isEmpty();
    }




    @Test
    void testUpdateAppointment_ShouldThrowException_WhenDoctorNotFound() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        appointment.getDoctor().setId(99L);

        // Act & Assert
        assertThatThrownBy(() -> appointmentService.updateAppointment(1L, appointment))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Doctor not found with id: 99");
    }

    @Test
    void testUpdateAppointment_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        appointment.getPatient().setId(99L);

        // Act & Assert
        assertThatThrownBy(() -> appointmentService.updateAppointment(1L, appointment))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found with id: 99");
    }

    @Test
    void testDeleteAppointment_ShouldDeleteAppointment_WhenIdExists() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        doNothing().when(appointmentRepository).delete(appointment);

        // Act
        appointmentService.deleteAppointment(1L);

        // Assert
        verify(appointmentRepository, times(1)).delete(appointment);
    }


}

