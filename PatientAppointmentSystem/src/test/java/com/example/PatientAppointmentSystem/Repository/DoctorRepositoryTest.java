package com.example.PatientAppointmentSystem.Repository;

import com.example.PatientAppointmentSystem.Entity.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void testFindByEmail_ShouldReturnDoctor_WhenEmailExists() {
        // Arrange
        Doctor doctor = new Doctor("Dr. John Smith", "john.smith@example.com", "securePass123", "Cardiology");
        doctorRepository.save(doctor);

        // Act
        Optional<Doctor> foundDoctor = doctorRepository.findByEmail("john.smith@example.com");

        // Assert
        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getName()).isEqualTo("Dr. John Smith");
        assertThat(foundDoctor.get().getEmail()).isEqualTo("john.smith@example.com");
        assertThat(foundDoctor.get().getPassword()).isEqualTo("securePass123");
        assertThat(foundDoctor.get().getSpecialization()).isEqualTo("Cardiology");
    }

    @Test
    void testFindByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // Act
        Optional<Doctor> foundDoctor = doctorRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(foundDoctor).isEmpty();
    }

    @Test
    void testExistsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Arrange
        Doctor doctor = new Doctor("Dr. Jane Doe", "jane.doe@example.com", "password456", "Pediatrics");
        doctorRepository.save(doctor);

        // Act
        boolean exists = doctorRepository.existsByEmail("jane.doe@example.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // Act
        boolean exists = doctorRepository.existsByEmail("noone@example.com");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void testSaveDoctor_ShouldPersistDoctorCorrectly() {
        // Arrange
        Doctor doctor = new Doctor("Dr. Alice Johnson", "alice.johnson@example.com", "alicePass789", "Neurology");

        // Act
        Doctor savedDoctor = doctorRepository.save(doctor);

        // Assert
        assertThat(savedDoctor.getId()).isNotNull();
        assertThat(savedDoctor.getName()).isEqualTo("Dr. Alice Johnson");
        assertThat(savedDoctor.getEmail()).isEqualTo("alice.johnson@example.com");
        assertThat(savedDoctor.getPassword()).isEqualTo("alicePass789");
        assertThat(savedDoctor.getSpecialization()).isEqualTo("Neurology");
    }

    @Test
    void testFindById_ShouldReturnDoctor_WhenIdExists() {
        // Arrange
        Doctor doctor = new Doctor("Dr. Chris Evans", "chris.evans@example.com", "evansPass321", "Orthopedics");
        Doctor savedDoctor = doctorRepository.save(doctor);

        // Act
        Optional<Doctor> foundDoctor = doctorRepository.findById(savedDoctor.getId());

        // Assert
        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getName()).isEqualTo("Dr. Chris Evans");
    }

    @Test
    void testFindById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        // Act
        Optional<Doctor> foundDoctor = doctorRepository.findById(999L);

        // Assert
        assertThat(foundDoctor).isEmpty();
    }
}
