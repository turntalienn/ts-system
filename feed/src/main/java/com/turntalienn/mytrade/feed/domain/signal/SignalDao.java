package com.turntalienn.mytrade.feed.domain.signal;

import com.turntalienn.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SignalDao {

    List<SignalDto> getSignal(String systemName, LocalDateTime currentTime);

}
