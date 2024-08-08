package example.mapper;

import example.config.MapperConfig;
import example.dto.orderItem.OrderItemRespondDto;
import example.model.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.Set;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    Set<OrderItemRespondDto> toDto(Set<OrderItem> orderItems);

    @AfterMapping
    default void setBookId(@MappingTarget OrderItemRespondDto orderItemRespondDto, OrderItem orderItem) {
        Long id = orderItem.getBook().getId();
        orderItemRespondDto.setBookId(id);
    }
}
