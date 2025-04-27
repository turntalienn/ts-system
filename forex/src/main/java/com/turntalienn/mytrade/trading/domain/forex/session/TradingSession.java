package com.turntalienn.mytrade.trading.domain.forex.session;

import com.turntalienn.mytrade.trading.api.ExecutionType;
import com.turntalienn.mytrade.trading.api.SessionType;
import com.turntalienn.mytrade.trading.domain.forex.brokerintegration.BrokerIntegrationService;
import com.turntalienn.mytrade.trading.domain.forex.brokerintegration.BrokerIntegrationFactory;
import com.turntalienn.mytrade.trading.domain.forex.common.TradingParams;
import com.turntalienn.mytrade.trading.domain.forex.common.events.EndedTradingDayEvent;
import com.turntalienn.mytrade.trading.domain.forex.common.events.Event;
import com.turntalienn.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;
import com.turntalienn.mytrade.trading.domain.forex.feed.FeedService;
import com.turntalienn.mytrade.trading.domain.forex.feed.pricefeed.PriceStream;
import com.turntalienn.mytrade.trading.domain.forex.feed.pricefeed.PriceStreamFactory;
import com.turntalienn.mytrade.trading.domain.forex.feed.signalfeed.SignalFeedFactory;
import com.turntalienn.mytrade.trading.domain.forex.feed.signalfeed.SignalFeedHandler;
import com.turntalienn.mytrade.trading.domain.forex.order.OrderFactory;
import com.turntalienn.mytrade.trading.domain.forex.order.OrderService;
import com.turntalienn.mytrade.trading.domain.forex.orderbook.OrderBookFactory;
import com.turntalienn.mytrade.trading.domain.forex.orderbook.OrderBookService;
import com.turntalienn.mytrade.trading.domain.forex.orderbook.CycleHistoryDto;
import com.turntalienn.mytrade.trading.domain.forex.portfolio.PortfolioFactory;
import com.turntalienn.mytrade.trading.domain.forex.portfolio.PortfolioService;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.RiskManagementFactory;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.RiskManagementService;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderConfigDto;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderCreationFactory;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderCreator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class TradingSession {

    protected final BigDecimal equity;
    protected final LocalDateTime startDate;
    protected final LocalDateTime endDate;
    protected final SessionType sessionType;
    protected final String systemName;
    protected final ExecutionType executionType;

    protected SignalFeedHandler signalFeedHandler;
    private final FeedService feedModule;
    protected BrokerIntegrationService executionHandler;
    protected OrderService orderService;
    protected OrderBookService historyHandler;
    protected PriceStream priceStream;
    protected PortfolioService portfolioService;
    protected boolean processedEndDay;
    protected RiskManagementService riskManagementService;
    protected final BlockingQueue<Event> eventQueue;
    protected EventNotifier eventNotifier;

    private static Logger log = Logger.getLogger(PortfolioService.class.getSimpleName());

    public TradingSession(
            BigDecimal equity,
            LocalDateTime startDate,
            LocalDateTime endDate,
            SessionType sessionType,
            String systemName,
            ExecutionType executionType,
            FeedService feedModule
    ) {
        this.equity = equity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sessionType = sessionType;
        this.systemName = systemName;
        this.executionType = executionType;
        this.eventQueue = new LinkedBlockingDeque<>();
        this.feedModule = feedModule;
        this.configSession();
    }

    private void configSession() {
        StopOrderConfigDto stopOrderDto = new StopOrderConfigDto(
                TradingParams.hard_stop_loss_distance,
                TradingParams.take_profit_distance_fixed,
                TradingParams.entry_stop_loss_distance_fixed,
                TradingParams.trailing_stop_loss_distance
        );
        StopOrderCreator stopOrderCreator = StopOrderCreationFactory.factory(stopOrderDto);
        this.signalFeedHandler = SignalFeedFactory.create(feedModule);
        this.executionHandler = BrokerIntegrationFactory.factory(this.executionType);
        this.historyHandler = OrderBookFactory.create();
        this.riskManagementService = RiskManagementFactory.create(stopOrderCreator);
        this.orderService = OrderFactory.create(this.riskManagementService);
        this.eventNotifier = new EventNotifier();
        this.portfolioService = PortfolioFactory.create(
                this.orderService,
                this.executionHandler,
                this.riskManagementService,
                eventNotifier
        );
        this.eventNotifier = setListeners();
        this.priceStream = PriceStreamFactory.create(this.sessionType, eventQueue, this.feedModule);
    }

    private EventNotifier setListeners() {
        var eventListeners = OrderFactory.createListeners(
                orderService,
                riskManagementService,
                executionHandler,
                eventNotifier,
                portfolioService
        );
        eventListeners.addAll(PortfolioFactory.createListeners(portfolioService, eventNotifier));

        eventListeners.add(new PriceChangedListener(
                executionHandler,
                portfolioService,
                signalFeedHandler,
                orderService,
                eventNotifier
        ));
        eventListeners.addAll(OrderBookFactory.createListeners(historyHandler));
        eventListeners.forEach(eventNotifier::attach);
        return eventNotifier;
    }


    public void start() {
        this.runSession();
    }

    public void shutdown() {
        log.warning("Shutting down the application");
        var current = LocalDateTime.now();
        var event = new EndedTradingDayEvent(
                current,
                priceStream.getPriceSymbolMapped(current)
        );
        eventNotifier.notify(event);
    }

    public List<CycleHistoryDto> getHistory() {
        return this.historyHandler.getTransactions();
    }

    protected void runSession() {
        printSessionStartMsg();
        this.executionHandler.closeAllPositions();
        this.executionHandler.cancelOpenLimitOrders();
        startEventProcessor();
        priceStream.start(startDate, endDate);
    }

    private void printSessionStartMsg() {
        if (this.sessionType == SessionType.BACK_TEST) {
            System.out.println(String.format("Running Backtest from %s to %s", this.startDate, this.endDate));
        } else {
            System.out.println(String.format("Running Real-time Session until %s", this.endDate));
        }
    }

    private void startEventProcessor() {
        QueueConsumer queueConsumer = new QueueConsumer(
                eventQueue,
                historyHandler,
                eventNotifier,
                endDate
        );
        queueConsumer.start();
    }

}



