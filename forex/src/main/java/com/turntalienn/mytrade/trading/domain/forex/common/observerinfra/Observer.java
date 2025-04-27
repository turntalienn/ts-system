package com.turntalienn.mytrade.trading.domain.forex.common.observerinfra;

import com.turntalienn.mytrade.trading.domain.forex.common.events.Event;

public interface Observer
{
    void update(Event e);
}