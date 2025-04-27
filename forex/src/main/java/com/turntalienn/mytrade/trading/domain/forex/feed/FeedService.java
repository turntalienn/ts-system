package com.turntalienn.mytrade.trading.domain.forex.feed;

import com.turntalienn.mytrade.feed.api.PriceDto;
import com.turntalienn.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FeedService {

    List<SignalDto> getSignal(String systemName, final LocalDateTime currentTime);

    Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime time);
}
