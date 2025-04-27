package com.turntalienn.mytrade.trading.domain.forex.common.events;

import com.turntalienn.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public class AbstractEvent implements Event {

    protected final LocalDateTime timestamp;
    private final Map<String, PriceDto> priceDtoMap;

    public AbstractEvent( LocalDateTime timestamp, Map<String, PriceDto> priceDtoMap) {
        this.timestamp = timestamp;
        this.priceDtoMap = priceDtoMap;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public Map<String, PriceDto> getPrice() {
        return priceDtoMap;
    }
}
