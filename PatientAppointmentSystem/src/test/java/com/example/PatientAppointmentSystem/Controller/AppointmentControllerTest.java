package com.example.PatientAppointmentSystem.Controller;

import com.example.PatientAppointmentSystem.Entity.Appointment;
import com.example.PatientAppointmentSystem.Entity.Doctor;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Service.AppointmentService;
import com.example.PatientAppointmentSystem.Service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AppointmentController appointmentController;

    private Appointment appointment;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doctor = new Doctor("Dr. Smith", "smith@example.com", "password123", "Cardiology");
        doctor.setId(1L);

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    void testShowBookingForm_ShouldReturnBookAppointmentPage() {
        // Arrange
        List<Doctor> doctors = List.of(doctor);
        when(doctorService.getAllDoctors()).thenReturn(doctors);

        // Act
        String viewName = appointmentController.showBookingForm(model);

        // Assert
        assertThat(viewName).isEqualTo("book-appointment");
        verify(model, times(1)).addAttribute("doctors", doctors);
        verify(model, times(1)).addAttribute(eq("appointment"), any(Appointment.class));
    }

    @Test
    void testBookAppointment_ShouldRedirectToAppointments_WhenSuccess() {
        // Act
        String viewName = appointmentController.bookAppointment(appointment, bindingResult, LocalDateTime.now().plusDays(1), model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/appointments");
        verify(appointmentService, times(1)).bookAppointment(appointment);
    }

    @Test
    void testBookAppointment_ShouldReturnBookAppointmentPage_WhenExceptionThrown() {
        // Arrange
        doThrow(new ResourceNotFoundException("Doctor not found")).when(appointmentService).bookAppointment(appointment);

        // Act
        String viewName = appointmentController.bookAppointment(appointment, bindingResult, LocalDateTime.now().plusDays(1), model);

        // Assert
        assertThat(viewName).isEqualTo("book-appointment");
        verify(model, times(1)).addAttribute("error", "Doctor not found");
    }

    @Test
    void testListAppointments_ShouldReturnAppointmentsPage() {
        // Arrange
        List<Appointment> appointments = List.of(appointment);
        when(appointmentService.getAllAppointments()).thenReturn(appointments);

        // Act
        String viewName = appointmentController.listAppointments(model);

        // Assert
        assertThat(viewName).isEqualTo("appointments");
        verify(model, times(1)).addAttribute("appointments", appointments);
    }

    @Test
    void testShowAppointmentDetails_ShouldReturnAppointmentDetails_WhenAppointmentExists() {
        // Arrange
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointment);

        // Act
        String viewName = appointmentController.showAppointmentDetails(1L, model);

        // Assert
        assertThat(viewName).isEqualTo("appointment-details");
        verify(model, times(1)).addAttribute("appointment", appointment);
    }



    @Test
    void testShowUpdateForm_ShouldReturnEditAppointmentPage_WhenAppointmentExists() {
        // Arrange
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointment);

        // Act
        String viewName = appointmentController.showUpdateForm(1L, model);

        // Assert
        assertThat(viewName).isEqualTo("edit-appointment");
        verify(model, times(1)).addAttribute("appointment", appointment);
    }



    @Test
    void testUpdateAppointment_ShouldRedirectToAppointments_WhenSuccess() {
        // Act
        String viewName = appointmentController.updateAppointment(1L, appointment, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/appointments");
        verify(appointmentService, times(1)).updateAppointment(1L, appointment);
    }

    @Test
    void testUpdateAppointment_ShouldReturnEditAppointmentPage_WhenAppointmentNotFound() {
        // Arrange
        doThrow(new ResourceNotFoundException("Appointment not found")).when(appointmentService).updateAppointment(1L, appointment);

        // Act
        String viewName = appointmentController.updateAppointment(1L, appointment, model);

        // Assert
        assertThat(viewName).isEqualTo("edit-appointment");
        verify(model, times(1)).addAttribute("error", "Appointment not found");
    }

    @Test
    void testDeleteAppointment_ShouldRedirectToAppointments_WhenSuccess() {
        // Act
        String viewName = appointmentController.deleteAppointment(1L);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/appointments");
        verify(appointmentService, times(1)).deleteAppointment(1L);
    }
}
