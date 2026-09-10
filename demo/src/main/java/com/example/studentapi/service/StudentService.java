package com.example.studentapi.service;

import com.example.studentapi.dto.StudentRequest;
import com.example.studentapi.exception.ResourceNotFoundException;
import com.example.studentapi.model.Student;
import com.example.studentapi.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private static final Logger logger =
            LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> getAllStudents() {

        logger.info("Fetching all students");

        return repository.findAll();
    }

    public Student getStudentById(Long id) {

        logger.info("Fetching student with id: {}", id);

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student with id " + id + " not found"
                        ));
    }

    public Student createStudent(StudentRequest request) {

        logger.info("Creating student with email: {}", request.getEmail());

        Student student = new Student();

        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());

        return repository.save(student);
    }

    public Student updateStudent(Long id, StudentRequest request) {

        logger.info("Updating student with id: {}", id);

        Student student = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student with id " + id + " not found"
                        ));

        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());

        return repository.save(student);
    }

    public void deleteStudent(Long id) {

        logger.info("Deleting student with id: {}", id);

        Student student = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student with id " + id + " not found"
                        ));

        repository.delete(student);
    }
}