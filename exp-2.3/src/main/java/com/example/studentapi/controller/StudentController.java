package com.example.studentapi.controller;

import com.example.studentapi.dto.StudentRequest;
import com.example.studentapi.model.Student;
import com.example.studentapi.response.ApiResponse;
import com.example.studentapi.response.PageResponse;
import com.example.studentapi.service.StudentService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<Student>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Students fetched successfully",
                        service.getAllStudents(page, size, sortBy, direction)
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Student>> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Student fetched successfully",
                        service.getStudentById(id)
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Student>> createStudent(@Valid @RequestBody StudentRequest request) {
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
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
