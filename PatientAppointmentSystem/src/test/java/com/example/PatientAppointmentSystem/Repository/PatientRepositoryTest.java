package com.example.PatientAppointmentSystem.Repository;

import com.example.PatientAppointmentSystem.Entity.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void testFindByEmail_ShouldReturnPatient_WhenEmailExists() {
        // Arrange
        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setEmail("john.doe@example.com");
        patient.setPassword("securePassword123");
        patient.setPhoneNumber("1234567890");
        patientRepository.save(patient);

        // Act
        Optional<Patient> foundPatient = patientRepository.findByEmail("john.doe@example.com");

        // Assert
        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get().getName()).isEqualTo("John Doe");
        assertThat(foundPatient.get().getPassword()).isEqualTo("securePassword123");
        assertThat(foundPatient.get().getPhoneNumber()).isEqualTo("1234567890");
    }



    @Test
    void testExistsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Arrange
        Patient patient = new Patient();
        patient.setName("Jane Doe");
        patient.setEmail("jane.doe@example.com");
        patient.setPassword("password456");
        patient.setPhoneNumber("9876543210");
        patientRepository.save(patient);

        // Act
        boolean exists = patientRepository.existsByEmail("jane.doe@example.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // Act
        boolean exists = patientRepository.existsByEmail("noone@example.com");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void testSavePatient_ShouldPersistPatientCorrectly() {
        // Arrange
        Patient patient = new Patient();
        patient.setName("Alice Johnson");
        patient.setEmail("alice.johnson@example.com");
        patient.setPassword("alicePass789");
        patient.setPhoneNumber("1112223333");

        // Act
        Patient savedPatient = patientRepository.save(patient);

        // Assert
        assertThat(savedPatient.getId()).isNotNull();
        assertThat(savedPatient.getName()).isEqualTo("Alice Johnson");
        assertThat(savedPatient.getEmail()).isEqualTo("alice.johnson@example.com");
        assertThat(savedPatient.getPassword()).isEqualTo("alicePass789");
        assertThat(savedPatient.getPhoneNumber()).isEqualTo("1112223333");
    }
}
