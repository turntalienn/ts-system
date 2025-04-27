package com.turntalienn.mytrade.trading.domain.forex.common.events;

import com.turntalienn.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public class SessionFinishedEvent implements Event {

    private final LocalDateTime timestamp;
    private final Map<String, PriceDto> price;

    public SessionFinishedEvent(LocalDateTime timestamp, Map<String, PriceDto> price) {
        this.timestamp = timestamp;
        this.price = price;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public Map<String, PriceDto> getPrice() {
        return price;
    }
}
