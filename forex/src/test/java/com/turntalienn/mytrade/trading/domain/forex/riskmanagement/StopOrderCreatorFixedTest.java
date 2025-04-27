package com.turntalienn.mytrade.trading.domain.forex.riskmanagement;

import com.turntalienn.mytrade.feed.api.PriceDto;
import com.turntalienn.mytrade.trading.domain.forex.common.events.PriceChangedEvent;
import com.turntalienn.mytrade.trading.domain.forex.order.OrderDto;
import com.turntalienn.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.turntalienn.mytrade.trading.domain.forex.portfolio.PositionBuilder;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderConfigDto;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderCreator;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderCreationFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class StopOrderCreatorFixedTest extends TestCase {

    private StopOrderCreator obj;

    @Before
    public void setUp() throws Exception {
        this.obj = StopOrderCreationFactory.factory(new StopOrderConfigDto(.1, .2, .2, .2));
    }

    @Test
    public void getHardStopLoss_WhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(PositionDto.PositionType.LONG);
        PositionDto position = positionBuilder.build();
        obj.createContext(PositionDto.PositionType.LONG);
        StopOrderDto hardStopLoss = obj.getHardStopLoss(position);

        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.HARD_STOP, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.SELL, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(0.904), hardStopLoss.price());
    }

    @Test
    public void getHardStopLoss_WhenShortPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(PositionDto.PositionType.SHORT);
        positionBuilder.withPrice(BigDecimal.valueOf(1.004));
        PositionDto position = positionBuilder.build();
        obj.createContext(PositionDto.PositionType.SHORT);

        StopOrderDto hardStopLoss = obj.getHardStopLoss(position);
        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.HARD_STOP, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.BUY, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(1.104), hardStopLoss.price());
    }

    @Test
    public void getTakeProfit_WhenShortPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(PositionDto.PositionType.SHORT);
        PositionDto position = positionBuilder.build();
        obj.createContext(PositionDto.PositionType.SHORT);

        StopOrderDto hardStopLoss = obj.getProfitStopOrder(position);
        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.TAKE_PROFIT, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.BUY, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(0.804), hardStopLoss.price());
    }


    @Test
    public void getTakeProfit_WhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(PositionDto.PositionType.LONG);
        positionBuilder.withPrice(BigDecimal.valueOf(1.004));
        PositionDto position = positionBuilder.build();

        obj.createContext(PositionDto.PositionType.LONG);

        StopOrderDto hardStopLoss = obj.getProfitStopOrder(position);
        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.TAKE_PROFIT, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.SELL, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(1.204), hardStopLoss.price());
    }


    @Test
    public void getEntryStopLoss_WhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(PositionDto.PositionType.LONG);
        positionBuilder.withPrice(BigDecimal.valueOf(1.004));
        PositionDto position = positionBuilder.build();

        obj.createContext(PositionDto.PositionType.LONG);

        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.305);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        StopOrderDto hardStopLoss = optional.get();
        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.ENTRY_STOP, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.SELL, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(1.004), hardStopLoss.price());
    }

    @Test
    public void getEntryStopLoss_WhenLongPositionAndPriceNotReachedEntryDistance() {
        PositionDto position = new PositionDto(
                PositionDto.PositionType.LONG,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.004),
                LocalDateTime.now(), "AUDUSD",
                null,
                null,
                PositionDto.PositionStatus.FILLED,
                null,
                null,
                null
        );
        obj.createContext(PositionDto.PositionType.LONG);
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.105);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        assertFalse(optional.isPresent());
    }


    @Test
    public void getEntryStopLossWhenShortPosition() {
        PositionDto position = new PositionDto(
                PositionDto.PositionType.SHORT,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.004),
                LocalDateTime.now(), "AUDUSD",
                null,
                null,
                PositionDto.PositionStatus.FILLED,
                null,
                null,
                null
        );
        obj.createContext(PositionDto.PositionType.SHORT);
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(0.803);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        StopOrderDto hardStopLoss = optional.get();
        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.ENTRY_STOP, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.BUY, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(1.004), hardStopLoss.price());
    }

    @Test
    public void getEntryStopLossWhenShortPositionAndPriceNotReachedEntryDistance() {
        PositionDto position = new PositionDto(
                PositionDto.PositionType.SHORT,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.004),
                LocalDateTime.now(), "AUDUSD",
                null,
                null,
                PositionDto.PositionStatus.FILLED,
                null,
                null,
                null
        );
        obj.createContext(PositionDto.PositionType.SHORT);
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.105);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        assertFalse(optional.isPresent());
    }

}