package com.example.PatientAppointmentSystem.Controller;



import com.example.PatientAppointmentSystem.Entity.Patient;
import com.example.PatientAppointmentSystem.Exception.ResourceAlreadyExistsException;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // Show registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "register";
    }

    // Handle patient registration
    @PostMapping("/register")
    public String registerPatient(@ModelAttribute Patient patient, Model model) {
        try {
            patientService.registerPatient(patient);
            return "redirect:/patients/login";
        } catch (ResourceAlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    // Show login form
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // Handle patient login
    @PostMapping("/login")
    public String loginPatient(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        try {
            Patient patient = patientService.authenticatePatient(email, password);
            session.setAttribute("patient", patient);
            return "redirect:/patients/dashboard";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    // Show patient dashboard
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("patient");
        if (patient == null) {
            return "redirect:/patients/login";
        }
        model.addAttribute("patient", patient);
        return "dashboard";
    }

    // Show patient profile
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("patient");
        if (patient == null) {
            return "redirect:/patients/login";
        }
        model.addAttribute("patient", patient);
        return "patient-profile";
    }

    // Update patient profile

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute Patient patientDetails, HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("patient");
        if (patient == null) {
            return "redirect:/patients/login";
        }
        try {
            Patient updatedPatient = patientService.updatePatient(patient.getId(), patientDetails);
            session.setAttribute("patient", updatedPatient);
            return "redirect:/patients/profile";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "profile";
        }
    }

    // Logout patient
    @GetMapping("/logout")
    public String logoutPatient(HttpSession session) {
        session.invalidate();
        return "redirect:/patients/login";
    }
}
