package com.example.PatientAppointmentSystem.Controller;

import com.example.PatientAppointmentSystem.Entity.Doctor;
import com.example.PatientAppointmentSystem.Exception.ResourceAlreadyExistsException;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DoctorControllerTest {

    @Mock
    private DoctorService doctorService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private DoctorController doctorController;

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");
        doctor.setEmail("smith@example.com");
        doctor.setPassword("password123");
        doctor.setSpecialization("Cardiology");
    }

    @Test
    void testShowRegistrationForm_ShouldReturnDoctorRegistrationPage() {
        // Act
        String viewName = doctorController.showRegistrationForm(model);

        // Assert
        assertThat(viewName).isEqualTo("doctor-registration");
        verify(model, times(1)).addAttribute(eq("doctor"), any(Doctor.class));
    }

    @Test
    void testRegisterDoctor_ShouldRedirectToLogin_WhenSuccess() {
        // Act
        String viewName = doctorController.registerDoctor(doctor, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/doctors/doctor-login");
        verify(doctorService, times(1)).registerDoctor(doctor);
    }

    @Test
    void testRegisterDoctor_ShouldReturnRegistrationPage_WhenDoctorExists() {
        // Arrange
        doThrow(new ResourceAlreadyExistsException("Doctor already exists")).when(doctorService).registerDoctor(doctor);

        // Act
        String viewName = doctorController.registerDoctor(doctor, model);

        // Assert
        assertThat(viewName).isEqualTo("doctor-registration");
        verify(model, times(1)).addAttribute("error", "Doctor already exists");
    }

    @Test
    void testShowLoginForm_ShouldReturnDoctorLoginPage() {
        // Act
        String viewName = doctorController.showLoginForm();

        // Assert
        assertThat(viewName).isEqualTo("doctor-login");
    }

    @Test
    void testLoginDoctor_ShouldRedirectToDashboard_WhenSuccess() {
        // Arrange
        when(doctorService.authenticateDoctor("smith@example.com", "password123")).thenReturn(doctor);

        // Act
        String viewName = doctorController.loginDoctor("smith@example.com", "password123", session, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/doctors/doctor-dashboard");
        verify(session, times(1)).setAttribute("doctor", doctor);
    }

    @Test
    void testLoginDoctor_ShouldReturnLoginPage_WhenDoctorNotFound() {
        // Arrange
        when(doctorService.authenticateDoctor("smith@example.com", "wrongPass"))
                .thenThrow(new ResourceNotFoundException("Doctor not found"));

        // Act
        String viewName = doctorController.loginDoctor("smith@example.com", "wrongPass", session, model);

        // Assert
        assertThat(viewName).isEqualTo("doctor-login");
        verify(model, times(1)).addAttribute("error", "Doctor not found");
    }



    @Test
    void testShowDashboard_ShouldReturnDashboard_WhenDoctorInSession() {
        // Arrange
        when(session.getAttribute("doctor")).thenReturn(doctor);

        // Act
        String viewName = doctorController.showDashboard(session, model);

        // Assert
        assertThat(viewName).isEqualTo("doctor-dashboard");
        verify(model, times(1)).addAttribute("doctor", doctor);
    }

    @Test
    void testShowProfile_ShouldRedirectToLogin_WhenNoSessionDoctor() {
        // Arrange
        when(session.getAttribute("doctor")).thenReturn(null);

        // Act
        String viewName = doctorController.showProfile(session, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/doctors/doctor-login");
    }

    @Test
    void testShowProfile_ShouldReturnDoctorProfile_WhenDoctorInSession() {
        // Arrange
        when(session.getAttribute("doctor")).thenReturn(doctor);

        // Act
        String viewName = doctorController.showProfile(session, model);

        // Assert
        assertThat(viewName).isEqualTo("doctor-profile");
        verify(model, times(1)).addAttribute("doctor", doctor);
    }


    @Test
    void testUpdateProfile_ShouldRedirectToProfile_WhenSuccess() {
        // Arrange
        when(session.getAttribute("doctor")).thenReturn(doctor);
        when(doctorService.updateDoctor(1L, doctor)).thenReturn(doctor);

        // Act
        String viewName = doctorController.updateProfile(doctor, session, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/doctors/profile");
        verify(session, times(1)).setAttribute("doctor", doctor);
    }

    @Test
    void testUpdateProfile_ShouldReturnProfilePage_WhenUpdateFails() {
        // Arrange
        when(session.getAttribute("doctor")).thenReturn(doctor);
        when(doctorService.updateDoctor(1L, doctor)).thenThrow(new ResourceNotFoundException("Doctor not found"));

        // Act
        String viewName = doctorController.updateProfile(doctor, session, model);

        // Assert
        assertThat(viewName).isEqualTo("doctor-profile");
        verify(model, times(1)).addAttribute("error", "Doctor not found");
    }

    @Test
    void testLogoutDoctor_ShouldInvalidateSessionAndRedirectToLogin() {
        // Act
        String viewName = doctorController.logoutDoctor(session);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/doctors/doctor-login");
        verify(session, times(1)).invalidate();
    }
}
