package example.controller;

import example.dto.user.UserLoginRequestDto;
import example.dto.user.UserLoginResponseDto;
import example.dto.user.UserRegistrationRequestDto;
import example.dto.user.UserResponseDto;
import example.exception.RegistrationException;
import example.security.AuthenticationService;
import example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "User Management", description = "Endpoints for managing Users registration")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Operation(summary = "Registration of a new user", description = "Registration of a new user")
    @PostMapping("/registration")
    public UserResponseDto registration(@RequestBody @Valid UserRegistrationRequestDto request) throws RegistrationException {
        return userService.register(request);
    }

    @Operation(summary = "User login", description = "Login to user`s account via username and password")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
       return authenticationService.login(request);
    }
}
