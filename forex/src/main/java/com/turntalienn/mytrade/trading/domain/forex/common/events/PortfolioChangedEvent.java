package com.turntalienn.mytrade.trading.domain.forex.common.events;

import com.turntalienn.mytrade.feed.api.PriceDto;
import com.turntalienn.mytrade.trading.domain.forex.portfolio.PositionDto;

import java.time.LocalDateTime;
import java.util.Map;

public class PortfolioChangedEvent extends AbstractEvent {
    private final PositionDto position;

    public PortfolioChangedEvent(
            LocalDateTime timestamp,
            Map<String, PriceDto> price,
            PositionDto position
    ) {
        super(timestamp, price);
        this.position = position;
    }

    public PositionDto getPosition() {
        return position;
    }
}
