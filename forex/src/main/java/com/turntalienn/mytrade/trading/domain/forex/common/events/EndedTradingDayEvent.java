package com.turntalienn.mytrade.trading.domain.forex.common.events;

import com.turntalienn.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public class EndedTradingDayEvent extends AbstractEvent {

    public EndedTradingDayEvent(LocalDateTime time, Map<String, PriceDto> price) {
        super(time, price);
    }

}
