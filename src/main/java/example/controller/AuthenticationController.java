package example.controller;

import example.dto.user.UserRegistrationRequestDto;
import example.dto.user.UserResponseDto;
import example.exception.RegistrationException;
import example.service.UserService;
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
    private final UserService userService;

    @PostMapping("/registration")
    public UserResponseDto registration(@RequestBody @Valid UserRegistrationRequestDto request) throws RegistrationException {
        return userService.register(request);
    }
}
