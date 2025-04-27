package com.turntalienn.mytrade.trading.domain.forex.feed.signalfeed;

import com.turntalienn.mytrade.trading.domain.forex.feed.FeedService;

public class SignalFeedFactory {

    public static SignalFeedHandler create(FeedService feed) {
        return new SignalFeedHandler(feed);
    }
}
