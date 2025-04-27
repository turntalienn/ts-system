package com.turntalienn.mytrade.trading.domain.forex.feed.pricefeed;

import com.turntalienn.mytrade.feed.api.PriceDto;
import com.turntalienn.mytrade.trading.domain.forex.feed.FeedService;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * PriceFeedHandler is responsible for providing ticker prices
 */
class PriceFeedHandler {

    private FeedService feedModule;

    public PriceFeedHandler(FeedService feedModule) {
        this.feedModule = feedModule;
    }

    public Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime time) {
        return feedModule.getPriceSymbolMapped(time);
    }
}
