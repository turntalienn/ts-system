package com.turntalienn.mytrade.trading.domain.forex.feed.pricefeed;

import com.turntalienn.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * Price feed stream API for the Forex system
 */
public interface PriceStream {

    void start(LocalDateTime start, LocalDateTime end);

    Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime current);
}
