package com.stockwise.repository;

import com.stockwise.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Contract for student persistence operations.
 * Assigned to: Janvi / Sidak
 */
public interface StudentRepository {
    Student save(Student student);
    Optional<Student> findById(String studentId);
    Optional<Student> findByRollNumber(String rollNumber);
    List<Student> findAll();
    List<Student> findByNameContaining(String name);
    boolean deleteById(String studentId);
    boolean existsById(String studentId);
    boolean existsByRollNumber(String rollNumber);
    int count();
}
