package com.turntalienn.mytrade.trading.domain.forex.orderbook;

import com.turntalienn.mytrade.trading.domain.forex.order.OrderDto;
import com.turntalienn.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.turntalienn.mytrade.trading.domain.forex.portfolio.PositionDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CycleHistoryDto {

    private final LocalDateTime time;

    private Map<String, TransactionDto> transactions = new HashMap<>();

    public CycleHistoryDto(LocalDateTime time) {
        this.time = time;
    }

    public void setState(TransactionDto.TransactionState state, String identifier) {
        getTransaction(identifier).setState(state);
    }

    private TransactionDto getTransaction(String identifier) {
        if (transactions.containsKey(identifier)) {
            return transactions.get(identifier);
        }
        TransactionDto transactionDto = new TransactionDto(this.time, identifier);
        transactions.put(identifier, transactionDto);
        return transactions.get(identifier);

    }

    public void addPosition(PositionDto ps) {
        getTransaction(ps.identifier()).setPosition(ps);
    }

    public void addOrderFilled(FilledOrderDto order) {
        getTransaction(order.identifier()).setFilledOrder(order);
    }

    public void addOrder(OrderDto order) {
        getTransaction(order.identifier()).setOrder(order);
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Map<String, TransactionDto> getTransactions() {
        return transactions;
    }
}
