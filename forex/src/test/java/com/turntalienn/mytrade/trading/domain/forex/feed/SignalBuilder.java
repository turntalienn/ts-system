package com.turntalienn.mytrade.trading.domain.forex.feed;

import com.turntalienn.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SignalBuilder {
    List<SignalDto> signals = new ArrayList<>();

    public SignalBuilder addSignal(LocalDateTime time, String action){
        signals.add(new SignalDto(
                time, action,"AUDUSD", "test"
        ));
        return this;
    }

    public List<SignalDto> buildList(){
        return signals;
    }

    public SignalDto build(){
        return signals.get(0);
    }
}
