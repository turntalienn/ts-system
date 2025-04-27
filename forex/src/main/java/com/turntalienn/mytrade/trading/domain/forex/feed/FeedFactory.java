package com.turntalienn.mytrade.trading.domain.forex.feed;

import com.turntalienn.mytrade.feed.api.FeedModule;

public class FeedFactory {

    public static FeedService create(final FeedModule feedModule) {
        return new TradingFeed(feedModule);
    }
}
