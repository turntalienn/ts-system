package com.turntalienn.mytrade.trading.domain.forex.session;

import com.turntalienn.mytrade.trading.domain.forex.common.events.Event;
import com.turntalienn.mytrade.trading.domain.forex.common.ForexException;
import com.turntalienn.mytrade.trading.domain.forex.common.events.SessionFinishedEvent;
import com.turntalienn.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;
import com.turntalienn.mytrade.trading.domain.forex.orderbook.OrderBookService;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

class QueueConsumer extends Thread {
    private final BlockingQueue<Event> eventQueue;
    private final OrderBookService historyHandler;
    private final EventNotifier notifier;
    private final LocalDateTime endDate;

    private static Logger log = Logger.getLogger(QueueConsumer.class.getSimpleName());

    public QueueConsumer(
            BlockingQueue<Event> eventQueue,
            OrderBookService historyHandler,
            EventNotifier notifier,
            LocalDateTime endDate

    ) {
        super();
        this.eventQueue = eventQueue;
        this.historyHandler = historyHandler;
        this.notifier = notifier;
        this.endDate = endDate;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                Event event = eventQueue.take();
                var logMsg = String.format(
                        "%s - %s - %s",
                        event,
                        event.getTimestamp(),
                        event.getPrice().get("AUDUSD").close()
                );
                log.info(logMsg);
                historyHandler.startCycle(event.getTimestamp());

                notifier.notify(event);

                historyHandler.endCycle();
                if (event instanceof SessionFinishedEvent) {
                    return;
                }
            } catch (InterruptedException ex) {
                throw new ForexException(ex);
            }
        }
    }
}
