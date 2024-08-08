package example.mapper;

import example.config.MapperConfig;
import example.dto.order.OrderRequestDto;
import example.dto.order.OrderRespondDto;
import example.model.Order;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    Order toEntity(OrderRequestDto requestDto);

    OrderRespondDto toDto(Order order);
}
