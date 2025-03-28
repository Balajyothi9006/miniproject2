package com.example.PatientAppointmentSystem.Controller;




import com.example.PatientAppointmentSystem.Entity.Doctor;
import com.example.PatientAppointmentSystem.Exception.ResourceAlreadyExistsException;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Exception.UnauthorizedException;
import com.example.PatientAppointmentSystem.Service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    // Show registration form
    @GetMapping("/doctor-registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "doctor-registration";
    }

    // Handle doctor registration
    @PostMapping("/doctor-registration")
    public String registerDoctor(@ModelAttribute Doctor doctor, Model model) {
        try {
            doctorService.registerDoctor(doctor);
            return "redirect:/doctors/doctor-login";
        } catch (ResourceAlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "doctor-registration";
        }
    }

    // Show login form
    @GetMapping("/doctor-login")
    public String showLoginForm() {
        return "doctor-login";
    }

    // Handle doctor login
    @PostMapping("/doctor-login")
    public String loginDoctor(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        try {
            Doctor doctor = doctorService.authenticateDoctor(email, password);
            session.setAttribute("doctor", doctor);
            return "redirect:/doctors/doctor-dashboard";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "doctor-login";
        }
    }

    // Show doctor dashboard
    @GetMapping("/doctor-dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Doctor doctor = (Doctor) session.getAttribute("doctor");
        if (doctor == null) {
            return "redirect:/doctors/doctor-login";
        }
        model.addAttribute("doctor", doctor);
        return "doctor-dashboard";
    }

    // Show doctor profile
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Doctor doctor = (Doctor) session.getAttribute("doctor");
        if (doctor == null) {
            return "redirect:/doctors/doctor-login";
        }
        model.addAttribute("doctor", doctor);
        return "doctor-profile";
    }

    // Update doctor profile
    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute Doctor doctorDetails, HttpSession session, Model model) {
        Doctor doctor = (Doctor) session.getAttribute("doctor");
        if (doctor == null) {
            return "redirect:/doctors/doctor-login";
        }
        try {
            Doctor updatedDoctor = doctorService.updateDoctor(doctor.getId(), doctorDetails);
            session.setAttribute("doctor", updatedDoctor);
            return "redirect:/doctors/profile";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "doctor-profile";
        }
    }

    // Logout doctor
    @GetMapping("/logout")
    public String logoutDoctor(HttpSession session) {
        session.invalidate();
        return "redirect:/doctors/doctor-login";
    }
}
