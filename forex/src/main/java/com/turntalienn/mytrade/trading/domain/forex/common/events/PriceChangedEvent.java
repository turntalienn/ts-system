package com.turntalienn.mytrade.trading.domain.forex.common.events;

import com.turntalienn.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public class PriceChangedEvent implements Event {
    private final LocalDateTime time;
    private final Map<String, PriceDto> priceSymbolMapped;

    public PriceChangedEvent(LocalDateTime time, Map<String, PriceDto> priceSymbolMapped) {
        this.time = time;
        this.priceSymbolMapped = priceSymbolMapped;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return time;
    }

    @Override
    public Map<String, PriceDto> getPrice() {
        return priceSymbolMapped;
    }
}
