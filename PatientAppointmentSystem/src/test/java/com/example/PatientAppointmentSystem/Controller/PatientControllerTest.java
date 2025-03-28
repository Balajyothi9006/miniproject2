package com.example.PatientAppointmentSystem.Controller;

import com.example.PatientAppointmentSystem.Entity.Patient;
import com.example.PatientAppointmentSystem.Exception.ResourceAlreadyExistsException;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private PatientController patientController;

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
    }

    @Test
    void testShowRegistrationForm_ShouldReturnRegisterPage() {
        // Act
        String viewName = patientController.showRegistrationForm(model);

        // Assert
        assertThat(viewName).isEqualTo("register");
        verify(model, times(1)).addAttribute(eq("patient"), any(Patient.class));
    }

    @Test
    void testRegisterPatient_ShouldRedirectToLogin_WhenSuccess() {
        // Act
        String viewName = patientController.registerPatient(patient, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/patients/login");
        verify(patientService, times(1)).registerPatient(patient);
    }

    @Test
    void testRegisterPatient_ShouldReturnRegisterPage_WhenPatientExists() {
        // Arrange
        doThrow(new ResourceAlreadyExistsException("Patient already exists")).when(patientService).registerPatient(patient);

        // Act
        String viewName = patientController.registerPatient(patient, model);

        // Assert
        assertThat(viewName).isEqualTo("register");
        verify(model, times(1)).addAttribute("error", "Patient already exists");
    }

    @Test
    void testShowLoginForm_ShouldReturnLoginPage() {
        // Act
        String viewName = patientController.showLoginForm();

        // Assert
        assertThat(viewName).isEqualTo("login");
    }

    @Test
    void testLoginPatient_ShouldRedirectToDashboard_WhenSuccess() {
        // Arrange
        when(patientService.authenticatePatient("john.doe@example.com", "securePass123")).thenReturn(patient);

        // Act
        String viewName = patientController.loginPatient("john.doe@example.com", "securePass123", session, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/patients/dashboard");
        verify(session, times(1)).setAttribute("patient", patient);
    }

    @Test
    void testLoginPatient_ShouldReturnLoginPage_WhenPatientNotFound() {
        // Arrange
        when(patientService.authenticatePatient("john.doe@example.com", "wrongPass"))
                .thenThrow(new ResourceNotFoundException("Patient not found"));

        // Act
        String viewName = patientController.loginPatient("john.doe@example.com", "wrongPass", session, model);

        // Assert
        assertThat(viewName).isEqualTo("login");
        verify(model, times(1)).addAttribute("error", "Patient not found");
    }


    @Test
    void testShowDashboard_ShouldReturnDashboard_WhenPatientInSession() {
        // Arrange
        when(session.getAttribute("patient")).thenReturn(patient);

        // Act
        String viewName = patientController.showDashboard(session, model);

        // Assert
        assertThat(viewName).isEqualTo("dashboard");
        verify(model, times(1)).addAttribute("patient", patient);
    }



    @Test
    void testShowProfile_ShouldReturnProfile_WhenPatientInSession() {
        // Arrange
        when(session.getAttribute("patient")).thenReturn(patient);

        // Act
        String viewName = patientController.showProfile(session, model);

        // Assert
        assertThat(viewName).isEqualTo("profile");
        verify(model, times(1)).addAttribute("patient", patient);
    }

    @Test
    void testUpdateProfile_ShouldRedirectToLogin_WhenNoSessionPatient() {
        // Arrange
        when(session.getAttribute("patient")).thenReturn(null);

        // Act
        String viewName = patientController.updateProfile(patient, session, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/patients/login");
    }

    @Test
    void testUpdateProfile_ShouldRedirectToProfile_WhenSuccess() {
        // Arrange
        when(session.getAttribute("patient")).thenReturn(patient);
        when(patientService.updatePatient(1L, patient)).thenReturn(patient);

        // Act
        String viewName = patientController.updateProfile(patient, session, model);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/patients/profile");
        verify(session, times(1)).setAttribute("patient", patient);
    }

    @Test
    void testUpdateProfile_ShouldReturnProfilePage_WhenUpdateFails() {
        // Arrange
        when(session.getAttribute("patient")).thenReturn(patient);
        when(patientService.updatePatient(1L, patient)).thenThrow(new ResourceNotFoundException("Patient not found"));

        // Act
        String viewName = patientController.updateProfile(patient, session, model);

        // Assert
        assertThat(viewName).isEqualTo("profile");
        verify(model, times(1)).addAttribute("error", "Patient not found");
    }

    @Test
    void testLogoutPatient_ShouldInvalidateSessionAndRedirectToLogin() {
        // Act
        String viewName = patientController.logoutPatient(session);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/patients/login");
        verify(session, times(1)).invalidate();
    }

    @Test
    void testIndex_ShouldReturnIndexPage() {
        // Act
        String viewName = patientController.index();

        // Assert
        assertThat(viewName).isEqualTo("index");
    }
}
