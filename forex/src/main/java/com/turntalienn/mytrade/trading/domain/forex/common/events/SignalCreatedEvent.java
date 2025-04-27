package com.turntalienn.mytrade.trading.domain.forex.common.events;


import com.turntalienn.mytrade.feed.api.PriceDto;
import com.turntalienn.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.Map;

public class SignalCreatedEvent extends AbstractEvent {


    private final SignalDto signal;

    public SignalCreatedEvent(LocalDateTime timestamp, Map<String, PriceDto> price, SignalDto signalDto) {
        super(timestamp, price);
        this.signal = signalDto;
    }

    public SignalDto getSignal() {
        return signal;
    }
}
