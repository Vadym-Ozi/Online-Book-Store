package example.mapper;

import example.config.MapperConfig;
import example.dto.order.*;
import example.model.Order;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    Order toEntity(OrderRequestDto requestDto);

    OrderDto toDto(Order order);

    UpdateOrderResponseDto toUpdateRespondDto(@MappingTarget UpdateOrderResponseDto responseDto,
                                              OrderUpdateRequestDto orderRequestDto);

    OrderRespondDto toRespondDto(Order order);

    @AfterMapping
    default void setUserIds(@MappingTarget OrderDto orderDto, Order order) {
        orderDto.setUserId(order.getUser().getId());
    }
}
