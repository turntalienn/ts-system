package com.turntalienn.mytrade.trading.domain.forex.common.events;

import com.turntalienn.mytrade.feed.api.PriceDto;
import com.turntalienn.mytrade.trading.domain.forex.portfolio.FilledOrderDto;

import java.time.LocalDateTime;
import java.util.Map;

public class OrderFilledEvent extends AbstractEvent {
    private final FilledOrderDto filledOrder;

    public OrderFilledEvent(LocalDateTime time, Map<String, PriceDto> price, FilledOrderDto filledOrder) {
        super(time, price);
        this.filledOrder = filledOrder;
    }

    public FilledOrderDto getFilledOrder() {
        return filledOrder;
    }
}
