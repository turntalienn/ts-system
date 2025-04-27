package com.turntalienn.mytrade.trading.domain.forex.common.events;

import com.turntalienn.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public interface Event {

    LocalDateTime getTimestamp();

    Map<String, PriceDto> getPrice();

}
