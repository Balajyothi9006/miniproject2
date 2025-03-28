package com.example.PatientAppointmentSystem.Controller;


import com.example.PatientAppointmentSystem.Entity.Appointment;
import com.example.PatientAppointmentSystem.Entity.Doctor;
import com.example.PatientAppointmentSystem.Exception.ResourceNotFoundException;
import com.example.PatientAppointmentSystem.Repository.DoctorRepository;
import com.example.PatientAppointmentSystem.Service.AppointmentService;
import com.example.PatientAppointmentSystem.Service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DoctorRepository doctorRepository;

    // Show appointment booking form
    @GetMapping("/bookform")
    public String showBookingForm(Model model) {
        List<Doctor> doctors = doctorService.getAllDoctors();
        // Add the list of doctors to the model
        model.addAttribute("doctors", doctors);
        model.addAttribute("appointment", new Appointment());
        return "book-appointment";
    }

    // Handle appointment booking
    @PostMapping("/booking")
    public String bookAppointment(@ModelAttribute("appointment") Appointment appointment,
                                  BindingResult result,
                                  @RequestParam("appointmentDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
                                  Model model) {
        appointment.setAppointmentDateTime(appointmentDateTime);

        try {
            appointmentService.bookAppointment(appointment);
            return "redirect:/appointments";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "book-appointment";
        }
    }

    // List all appointments
    @GetMapping
    public String listAppointments(Model model) {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        model.addAttribute("appointments", appointments);
        return "appointments";
    }

    // Show appointment details
    @GetMapping("/{id}")
    public String showAppointmentDetails(@PathVariable Long id, Model model) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            return "appointment-details";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/appointments";
        }
    }

    // Show form to update an appointment
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            return "edit-appointment";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/appointments";
        }
    }

    // Handle appointment update
    @PostMapping("/edit/{id}")
    public String updateAppointment(@PathVariable Long id, @ModelAttribute Appointment appointmentDetails, Model model) {
        try {
            appointmentService.updateAppointment(id, appointmentDetails);
            return "redirect:/appointments";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "edit-appointment";
        }
    }

    // Delete an appointment
    @GetMapping("/delete/{id}")
    public String deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return "redirect:/appointments";
    }
}