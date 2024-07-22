package example.service;

import example.dto.userDtos.UserRegistrationRequestDto;
import example.dto.userDtos.UserResponseDto;
import example.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}
