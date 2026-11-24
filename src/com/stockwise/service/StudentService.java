package com.stockwise.service;

import com.stockwise.exception.DuplicateResourceException;
import com.stockwise.exception.ResourceNotFoundException;
import com.stockwise.exception.ValidationException;
import com.stockwise.model.Student;
import com.stockwise.repository.DemandRepository;
import com.stockwise.repository.StudentRepository;
import com.stockwise.util.IDGenerator;
import com.stockwise.util.ValidationUtils;

import java.util.List;

/**
 * Service managing student registration, profile updates, and query operations.
 * Assigned to: Sedhat (Student Management)
 */
public class StudentService {

    private final StudentRepository studentRepository;
    private final DemandRepository demandRepository;

    public StudentService(StudentRepository studentRepository, DemandRepository demandRepository) {
        this.studentRepository = studentRepository;
        this.demandRepository = demandRepository;
    }

    public synchronized Student addStudent(String studentId, String studentName, String rollNumber,
                                           String department, String email, String phoneNumber) {
        String finalId = (studentId == null || studentId.trim().isEmpty())
                ? IDGenerator.generateStudentId()
                : studentId.trim();

        if (studentRepository.existsById(finalId)) {
            throw new DuplicateResourceException("A student with ID '" + finalId + "' already exists.");
        }

        if (studentRepository.existsByRollNumber(rollNumber)) {
            throw new DuplicateResourceException("A student with Roll Number '" + rollNumber.trim() + "' already exists.");
        }

        Student student = new Student(finalId, studentName, rollNumber, department, email, phoneNumber);
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(String studentId) {
        ValidationUtils.requireNonEmpty(studentId, "Student ID");
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID '" + studentId + "' not found."));
    }

    public Student getStudentByRollNumber(String rollNumber) {
        ValidationUtils.requireNonEmpty(rollNumber, "Roll Number");
        return studentRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Student with Roll Number '" + rollNumber + "' not found."));
    }

    public List<Student> searchByName(String name) {
        return studentRepository.findByNameContaining(name);
    }

    public synchronized Student updateStudent(String studentId, String newName, String newDept,
                                              String newEmail, String newPhone) {
        Student student = getStudentById(studentId);

        if (newName != null && !newName.trim().isEmpty()) {
            student.setStudentName(newName);
        }
        if (newDept != null && !newDept.trim().isEmpty()) {
            student.setDepartment(newDept);
        }
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            student.setEmail(newEmail);
        }
        if (newPhone != null && !newPhone.trim().isEmpty()) {
            student.setPhoneNumber(newPhone);
        }

        return studentRepository.save(student);
    }

    public synchronized boolean deleteStudent(String studentId) {
        Student student = getStudentById(studentId);

        // Academic business rule: verify no dependent demands exist
        int demandCount = demandRepository.findByStudentId(studentId).size();
        if (demandCount > 0) {
            throw new ValidationException("Cannot delete student '" + student.getStudentName() + "' (" + studentId +
                    "): " + demandCount + " associated demand requests exist.");
        }

        return studentRepository.deleteById(studentId);
    }

    public int getTotalStudentCount() {
        return studentRepository.count();
    }
}
