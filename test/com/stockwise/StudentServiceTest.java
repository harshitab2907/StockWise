package com.stockwise;

import com.stockwise.exception.DuplicateResourceException;
import com.stockwise.exception.ValidationException;
import com.stockwise.model.DemandRecord;
import com.stockwise.model.Student;
import com.stockwise.repository.InMemoryDemandRepository;
import com.stockwise.repository.InMemoryStudentRepository;
import com.stockwise.service.StudentService;

import java.util.List;

import static com.stockwise.TestAssertions.*;

/**
 * Unit tests verifying Module B: Student Management.
 */
public class StudentServiceTest {

    private InMemoryStudentRepository studentRepo;
    private InMemoryDemandRepository demandRepo;
    private StudentService service;

    public void setUp() {
        studentRepo = new InMemoryStudentRepository();
        demandRepo = new InMemoryDemandRepository();
        service = new StudentService(studentRepo, demandRepo);
    }

    public void testAddStudentSuccessfully() {
        setUp();
        Student s = service.addStudent("STU-101", "Alice Smith", "CS-2024-01", "Computer Science", "alice@college.edu", "9876543210");
        assertNotNull(s, "Added student should not be null");
        assertEquals("STU-101", s.getStudentId(), "Student ID matches");
        assertEquals("CS-2024-01", s.getRollNumber(), "Roll number matches");
        assertEquals(1, studentRepo.count(), "Repository should contain 1 student");
    }

    public void testDuplicateRollNumberRejection() {
        setUp();
        service.addStudent("STU-1", "Bob", "ROLL-100", "CS", "bob@college.edu", "9876543210");
        assertThrows(DuplicateResourceException.class, () -> {
            service.addStudent("STU-2", "Charlie", "ROLL-100", "IT", "charlie@college.edu", "9876543211");
        }, "Adding a student with duplicate roll number must throw DuplicateResourceException");
    }

    public void testDuplicateStudentIdRejection() {
        setUp();
        service.addStudent("STU-SAME", "Student 1", "ROLL-1", "CS", "s1@college.edu", "9876543210");
        assertThrows(DuplicateResourceException.class, () -> {
            service.addStudent("STU-SAME", "Student 2", "ROLL-2", "CS", "s2@college.edu", "9876543211");
        }, "Adding a student with duplicate ID must throw DuplicateResourceException");
    }

    public void testSearchStudents() {
        setUp();
        service.addStudent("STU-1", "John Doe", "R1", "CS", "john@college.edu", "9876543210");
        service.addStudent("STU-2", "Jane Doe", "R2", "IT", "jane@college.edu", "9876543211");
        service.addStudent("STU-3", "Alan Turing", "R3", "Math", "alan@college.edu", "9876543212");

        List<Student> doeList = service.searchByName("Doe");
        assertEquals(2, doeList.size(), "Should find 2 students with 'Doe'");

        Student byRoll = service.getStudentByRollNumber("R3");
        assertEquals("Alan Turing", byRoll.getStudentName(), "Should find student by roll number");
    }

    public void testUpdateStudent() {
        setUp();
        service.addStudent("STU-UPD", "Old Name", "R-UPD", "CS", "old@college.edu", "9876543210");
        Student updated = service.updateStudent("STU-UPD", "Updated Name", "AI Dept", "new@college.edu", "9998887776");

        assertEquals("Updated Name", updated.getStudentName(), "Name updated");
        assertEquals("AI Dept", updated.getDepartment(), "Dept updated");
        assertEquals("new@college.edu", updated.getEmail(), "Email updated");
    }

    public void testSafeDeletionPreventedWithDemands() {
        setUp();
        service.addStudent("STU-ACTIVE", "Active Student", "R-ACT", "CS", "active@college.edu", "9876543210");
        demandRepo.save(new DemandRecord("DMD-ACT", "STU-ACTIVE", "ITM-1", 1));

        assertThrows(ValidationException.class, () -> {
            service.deleteStudent("STU-ACTIVE");
        }, "Deleting a student with dependent demands must be rejected");
    }

    public void testInvalidEmailAndPhoneValidation() {
        setUp();
        assertThrows(ValidationException.class, () -> {
            service.addStudent("STU-BAD-EMAIL", "Bad Email", "R-BE", "CS", "not-an-email", "9876543210");
        }, "Invalid email format must throw ValidationException");

        assertThrows(ValidationException.class, () -> {
            service.addStudent("STU-BAD-PHONE", "Bad Phone", "R-BP", "CS", "good@college.edu", "123");
        }, "Too short phone number must throw ValidationException");
    }
}
