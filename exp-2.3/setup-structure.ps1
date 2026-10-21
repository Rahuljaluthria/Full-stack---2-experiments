
$base = "src/main/java/com/example/studentapi"

$directories = @(
    "controller",
    "service",
    "security",
    "config",
    "model",
    "repository",
    "dto",
    "crypto",
    "exception",
    "response"
)

$files = @{
    "controller/AuthController.java" = "AuthController"
    "controller/StudentController.java" = "StudentController"
    "service/AuthService.java" = "AuthService"
    "service/StudentService.java" = "StudentService"
    "security/JwtService.java" = "JwtService"
    "security/JwtAuthenticationFilter.java" = "JwtAuthenticationFilter"
    "security/CustomUserDetailsService.java" = "CustomUserDetailsService"
    "config/SecurityConfig.java" = "SecurityConfig"
    "config/CorsConfig.java" = "CorsConfig"
    "model/User.java" = "User"
    "model/Role.java" = "Role"
    "model/Student.java" = "Student"
    "model/RefreshToken.java" = "RefreshToken"
    "repository/UserRepository.java" = "UserRepository"
    "repository/StudentRepository.java" = "StudentRepository"
    "repository/RefreshTokenRepository.java" = "RefreshTokenRepository"
    "dto/RegisterRequest.java" = "RegisterRequest"
    "dto/LoginRequest.java" = "LoginRequest"
    "dto/AuthResponse.java" = "AuthResponse"
    "dto/RefreshRequest.java" = "RefreshRequest"
    "crypto/EncryptionService.java" = "EncryptionService"
    "exception/GlobalExceptionHandler.java" = "GlobalExceptionHandler"
    "exception/ResourceNotFoundException.java" = "ResourceNotFoundException"
    "response/ApiResponse.java" = "ApiResponse"
}

foreach ($dir in $directories) {
    $path = Join-Path $base $dir
    New-Item -ItemType Directory -Path $path -Force | Out-Null
}

foreach ($entry in $files.GetEnumerator()) {
    $path = Join-Path $base $entry.Key

    if (-not (Test-Path $path)) {
        $className = $entry.Value
        $packageName = "com.example.studentapi." +
            ($entry.Key -replace '/', '.' -replace '\.java$', '' -replace '\.[^.]+$', '')

        if ($className -eq "Role") {
            $content = @"
package com.example.studentapi.model;

public enum Role {
    ADMIN,
    USER
}
"@
        }
        elseif ($className -eq "ResourceNotFoundException") {
            $content = @"
package com.example.studentapi.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
"@
        }
        else {
            $content = @"
package $packageName;

// TODO: Implement $className
"@
        }

        Set-Content -Path $path -Value $content -Encoding UTF8
        Write-Host "Created: $path"
    }
    else {
        Write-Host "Skipped existing file: $path"
    }
}

Write-Host "`nProject structure setup completed."
Write-Host "Existing files were preserved."
