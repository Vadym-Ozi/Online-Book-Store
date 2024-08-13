package example.mapper;

import example.config.MapperConfig;
import example.dto.shoppingCart.ShoppingRespondCartDto;
import example.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class , uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    ShoppingRespondCartDto toRespondDto(ShoppingCart shoppingCart);

    @AfterMapping
    default void setUsersId(@MappingTarget ShoppingRespondCartDto shoppingRespondCartDto, ShoppingCart shoppingCart) {
        Long usersId = shoppingCart.getUser().getId();
        shoppingRespondCartDto.setUserId(usersId);
    }
}
