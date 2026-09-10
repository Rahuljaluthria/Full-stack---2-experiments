package com.example.studentapi.controller;

import com.example.studentapi.dto.StudentRequest;
import com.example.studentapi.model.Student;
import com.example.studentapi.response.ApiResponse;
import com.example.studentapi.service.StudentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Students fetched successfully",
                        service.getAllStudents()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> getStudentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Student fetched successfully",
                        service.getStudentById(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Student>> createStudent(
            @Valid @RequestBody StudentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Student created successfully",
                                service.createStudent(request)
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Student updated successfully",
                        service.updateStudent(id, request)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable Long id) {

        service.deleteStudent(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Student deleted successfully",
                        null
                )
        );
    }
}