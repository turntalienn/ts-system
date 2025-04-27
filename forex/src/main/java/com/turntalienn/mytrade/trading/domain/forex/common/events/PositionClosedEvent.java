package com.turntalienn.mytrade.trading.domain.forex.common.events;

import com.turntalienn.mytrade.feed.api.PriceDto;
import com.turntalienn.mytrade.trading.domain.forex.order.OrderDto;

import java.time.LocalDateTime;
import java.util.Map;

public class PositionClosedEvent extends AbstractEvent {
    private OrderDto order;

    public PositionClosedEvent(LocalDateTime timestamp, Map<String, PriceDto> price, OrderDto order) {
        super(timestamp, price);
        this.order = order;
    }

    public OrderDto getOrder() {
        return order;
    }
}
