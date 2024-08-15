package example.mapper;

import example.config.MapperConfig;
import example.dto.cartItem.CartItemDto;
import example.dto.cartItem.RequestCartItemDto;
import example.dto.cartItem.RespondCartItemDto;
import example.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.Set;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {

    Set<RespondCartItemDto> toDto(Set<CartItem> cartItem);

    CartItem toEntity(RequestCartItemDto requestCartItemDto);

    @AfterMapping
    default void setBookId(@MappingTarget CartItemDto cartItemRespondDto, CartItem cartItem) {
        Long id = cartItem.getBook().getId();
        cartItemRespondDto.setBookId(id);
    }
    @AfterMapping
    default void setBookInfo(@MappingTarget RespondCartItemDto respondCartItemDto, CartItem cartItem) {
        String bookTitle = cartItem.getBook().getTitle();
        Long id = cartItem.getBook().getId();
        respondCartItemDto.setBookId(id);
        respondCartItemDto.setBookTitle(bookTitle);
    }
}
