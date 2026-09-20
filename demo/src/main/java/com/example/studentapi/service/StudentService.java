package com.example.studentapi.service;

import com.example.studentapi.dto.StudentRequest;
import com.example.studentapi.exception.ResourceNotFoundException;
import com.example.studentapi.model.Student;
import com.example.studentapi.repository.StudentRepository;
import com.example.studentapi.response.PageResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;



@Service
public class StudentService {

    private static final Logger logger =
            LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

   @Cacheable(
            value = "students",
            key = "#page + '-' + #size + '-' + #sortBy + '-' + #direction"
    )
    public PageResponse<Student> getAllStudents(
            int page,
            int size,
            String sortBy,
            String direction) {

        logger.info(
                "CACHE MISS - Fetching students from database: page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction
        );

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Student> studentPage = repository.findAll(pageable);

        return new PageResponse<>(
                studentPage.getContent(),
                studentPage.getNumber(),
                studentPage.getSize(),
                studentPage.getTotalElements(),
                studentPage.getTotalPages(),
                studentPage.isLast()
        );
    }


    // CACHE STUDENT BY ID
    @Cacheable(
            value = "studentById",
            key = "#id"
    )
    public Student getStudentById(Long id) {

        logger.info(
                "CACHE MISS - Fetching student with id: {}",
                id
        );

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student with id " + id + " not found"
                        ));
    }


    // CREATE STUDENT
    @CacheEvict(
            value = "students",
            allEntries = true
    )
    public Student createStudent(StudentRequest request) {

        logger.info(
                "Creating student with email: {}",
                request.getEmail()
        );

        Student student = new Student();

        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());

        return repository.save(student);
    }


    // UPDATE STUDENT
    @CacheEvict(
            value = {"students", "studentById"},
            allEntries = true
    )
    public Student updateStudent(
            Long id,
            StudentRequest request) {

        logger.info(
                "Updating student with id: {}",
                id
        );

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


    // DELETE STUDENT
    @CacheEvict(
            value = {"students", "studentById"},
            allEntries = true
    )
    public void deleteStudent(Long id) {

        logger.info(
                "Deleting student with id: {}",
                id
        );

        Student student = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student with id " + id + " not found"
                        ));

        repository.delete(student);
    }
}