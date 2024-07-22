package example.service.impl;

import example.dto.userDtos.UserRegistrationRequestDto;
import example.dto.userDtos.UserResponseDto;
import example.exception.RegistrationException;
import example.mapper.UserMapper;
import example.model.User;
import example.repository.user.UserRepository;
import example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("This email is already used. Can`t register user");
        }
        User user = userMapper.toModel(request);
        User savedUser = userRepository.save(user);
        return userMapper.toUserRespondDto(savedUser);
    }
}
