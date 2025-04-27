package com.turntalienn.mytrade.trading.domain.forex.orderbook;

import com.turntalienn.mytrade.trading.domain.forex.common.events.Event;
import com.turntalienn.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.turntalienn.mytrade.trading.domain.forex.common.events.PortfolioChangedEvent;

class HistoryPortfolioChangedListener implements Observer {

    private final OrderBookService bookHandler;

    public HistoryPortfolioChangedListener(OrderBookService bookHandler) {
        this.bookHandler = bookHandler;
    }

    @Override
    public void update(final Event e) {
        if (!(e instanceof PortfolioChangedEvent event)) {
            return;
        }
        bookHandler.addPosition(event.getPosition());
    }
}
