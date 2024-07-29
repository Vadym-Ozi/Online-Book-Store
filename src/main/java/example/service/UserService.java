package example.service;

import example.dto.user.UserRegistrationRequestDto;
import example.dto.user.UserResponseDto;
import example.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}
