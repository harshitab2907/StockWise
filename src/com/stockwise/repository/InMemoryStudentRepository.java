package com.stockwise.repository;

import com.stockwise.model.Student;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory thread-safe implementation of StudentRepository.
 * Assigned to: Janvi / Sidak
 */
public class InMemoryStudentRepository implements StudentRepository {
    private final Map<String, Student> studentStore = new LinkedHashMap<>();

    @Override
    public synchronized Student save(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Cannot save null student.");
        }
        studentStore.put(student.getStudentId(), student);
        return student;
    }

    @Override
    public synchronized Optional<Student> findById(String studentId) {
        if (studentId == null) return Optional.empty();
        return Optional.ofNullable(studentStore.get(studentId));
    }

    @Override
    public synchronized Optional<Student> findByRollNumber(String rollNumber) {
        if (rollNumber == null) return Optional.empty();
        String roll = rollNumber.trim().toLowerCase();
        return studentStore.values().stream()
                .filter(s -> s.getRollNumber().equalsIgnoreCase(roll))
                .findFirst();
    }

    @Override
    public synchronized List<Student> findAll() {
        return new ArrayList<>(studentStore.values());
    }

    @Override
    public synchronized List<Student> findByNameContaining(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String query = name.trim().toLowerCase();
        return studentStore.values().stream()
                .filter(s -> s.getStudentName().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized boolean deleteById(String studentId) {
        if (studentId == null) return false;
        return studentStore.remove(studentId) != null;
    }

    @Override
    public synchronized boolean existsById(String studentId) {
        if (studentId == null) return false;
        return studentStore.containsKey(studentId);
    }

    @Override
    public synchronized boolean existsByRollNumber(String rollNumber) {
        return findByRollNumber(rollNumber).isPresent();
    }

    @Override
    public synchronized int count() {
        return studentStore.size();
    }
}
