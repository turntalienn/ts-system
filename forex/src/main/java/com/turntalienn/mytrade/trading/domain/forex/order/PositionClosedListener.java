package com.turntalienn.mytrade.trading.domain.forex.order;

import com.turntalienn.mytrade.trading.domain.forex.common.events.Event;
import com.turntalienn.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.turntalienn.mytrade.trading.domain.forex.common.events.PositionClosedEvent;


class PositionClosedListener implements Observer {

    private final OrderService orderService;

    public PositionClosedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void update(final Event e) {
        if (!(e instanceof PositionClosedEvent event)) {
            return;
        }
        this.orderService.persist(event.getOrder());
    }

}
