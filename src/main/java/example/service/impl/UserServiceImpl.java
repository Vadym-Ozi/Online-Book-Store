package example.service.impl;

import example.dto.user.UserRegistrationRequestDto;
import example.dto.user.UserResponseDto;
import example.exception.RegistrationException;
import example.mapper.UserMapper;
import example.model.User;
import example.repository.user.UserRepository;
import example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegistrationException("This email is already used: " + request.getEmail());
        }

        User user = userMapper.toModel(request);
        user.setPassword(encoder.encode(request.getPassword()));
        return userMapper.toUserRespondDto(userRepository.save(user));
    }
}
