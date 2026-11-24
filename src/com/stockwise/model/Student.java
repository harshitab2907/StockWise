package com.stockwise.model;

import com.stockwise.exception.ValidationException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a student registered in the college inventory system.
 * Assigned to: Sidak (Student Management)
 */
public class Student {
    private String studentId;
    private String studentName;
    private String rollNumber;
    private String department;
    private String email;
    private String phoneNumber;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s()]{7,20}$");

    public Student(String studentId, String studentName, String rollNumber,
                   String department, String email, String phoneNumber) {
        validateStudentId(studentId);
        validateStudentName(studentName);
        validateRollNumber(rollNumber);
        validateEmail(email);
        validatePhoneNumber(phoneNumber);

        this.studentId = studentId.trim();
        this.studentName = studentName.trim();
        this.rollNumber = rollNumber.trim();
        this.department = (department == null || department.trim().isEmpty()) ? "General" : department.trim();
        this.email = email.trim();
        this.phoneNumber = phoneNumber.trim();
    }

    public static void validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("Student ID cannot be empty.");
        }
    }

    public static void validateStudentName(String studentName) {
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new ValidationException("Student name cannot be empty.");
        }
    }

    public static void validateRollNumber(String rollNumber) {
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new ValidationException("Roll number cannot be empty.");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address cannot be empty.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format: '" + email + "'. Expected format: user@domain.com");
        }
    }

    public static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new ValidationException("Phone number cannot be empty.");
        }
        if (!PHONE_PATTERN.matcher(phoneNumber.trim()).matches()) {
            throw new ValidationException("Invalid phone number format: '" + phoneNumber + "'. Must be 7-20 digits.");
        }
    }

    // Getters and Setters

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        validateStudentId(studentId);
        this.studentId = studentId.trim();
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        validateStudentName(studentName);
        this.studentName = studentName.trim();
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        validateRollNumber(rollNumber);
        this.rollNumber = rollNumber.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = (department == null || department.trim().isEmpty()) ? "General" : department.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        this.phoneNumber = phoneNumber.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId) || Objects.equals(rollNumber, student.rollNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", rollNumber='" + rollNumber + '\'' +
                ", department='" + department + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
