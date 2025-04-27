package com.turntalienn.mytrade.trading.domain.forex.portfolio;

import com.turntalienn.mytrade.trading.domain.forex.common.MultiPositionHandler;
import com.turntalienn.mytrade.trading.domain.forex.common.events.Event;
import com.turntalienn.mytrade.trading.domain.forex.common.events.PortfolioChangedEvent;
import com.turntalienn.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;
import com.turntalienn.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderFilledEvent;
import static com.turntalienn.mytrade.trading.domain.forex.portfolio.PositionDto.ExitReason;

class StopOrderFilledListener implements Observer {

    private final PortfolioService portfolioService;
    private final EventNotifier eventNotifier;

    public StopOrderFilledListener(
            PortfolioService portfolioService,
            EventNotifier eventNotifier
    ) {
        this.portfolioService = portfolioService;
        this.eventNotifier = eventNotifier;
    }

    @Override
    public void update(final Event e) {
        if (!(e instanceof StopOrderFilledEvent event)) {
            return;
        }
        StopOrderDto stopOrder = event.getStopOrder();
        PositionDto ps = MultiPositionHandler.getPositionByStopOrder(stopOrder);

        portfolioService.closePosition(ps.identifier(), ExitReason.STOP_ORDER_FILLED);
        portfolioService.processReconciliation(e);
        eventNotifier.notify(new PortfolioChangedEvent(
                e.getTimestamp(),
                e.getPrice(),
                ps
        ));
    }
}
