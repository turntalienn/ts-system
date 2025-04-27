package com.turntalienn.mytrade.trading.api;

import com.turntalienn.mytrade.feed.api.FeedBuilder;
import com.turntalienn.mytrade.trading.domain.forex.common.ForexException;
import com.turntalienn.mytrade.trading.domain.forex.feed.FeedFactory;
import com.turntalienn.mytrade.trading.domain.forex.orderbook.CycleHistoryDto;
import com.turntalienn.mytrade.trading.domain.forex.session.TradingSession;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import static java.time.LocalDate.of;


public class ForexEngine {

    private final TradingSession tradingSession;

    public ForexEngine(final TradingSession tradingSession){
        this.tradingSession = tradingSession;
    }

    public static void main(String[] args) {
        var date = of(2018, 9, 10);

        var systemName = "signal_test";

        LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(date.plusDays(10), LocalTime.MIN);

        var feed = new FeedBuilder()
                .withStartTime(start)
                .withEndTime(end)
                .withSignalName(systemName)
                //                .withConnection(getConnection())
                .build();

        var engine =new ForexBuilder()
                .withSystemName(systemName)
                .withStartTime(start)
                .withEndTime(end)
                .withEquity(BigDecimal.valueOf(100000L))
                .withSessionType(SessionType.BACK_TEST)
                .withExecutionType(ExecutionType.SIMULATED)
                .withFeed(FeedFactory.create(feed))
                .build();
        engine.start();
        System.out.println("finished session");
    }

    private static com.turntalienn.mytrade.trading.api.CycleHistoryDto mapHistory(CycleHistoryDto c) {
        List<TransactionDto> transactions = c.getTransactions()
                .entrySet()
                .stream()
                .map(e -> new TransactionDto(e.getValue()))
                .collect(Collectors.toList());
        return new com.turntalienn.mytrade.trading.api.CycleHistoryDto(c.getTime(), transactions);
    }

    public void start() {
        tradingSession.start();
    }

    public List<com.turntalienn.mytrade.trading.api.CycleHistoryDto> getHistory() {
        List<com.turntalienn.mytrade.trading.api.CycleHistoryDto> collect = tradingSession.getHistory()
                .stream()
                .filter(i -> i.getTransactions().size() > 0)
                .map(ForexEngine::mapHistory)
                .collect(Collectors.toList());
        return collect;
    }


    private static Connection getConnection() throws ForexException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            throw new ForexException(ex);
        }
        String url = "jdbc:h2:mem:";

        Connection con;
        try {
            con = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new ForexException(ex);
        }
        return con;
    }


}
