package example.mapper;

import example.config.MapperConfig;
import example.dto.user.UserRegistrationRequestDto;
import example.dto.user.UserResponseDto;
import example.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserRespondDto(User user);

    User toModel(UserRegistrationRequestDto userRegistrationRequestDto);
}
