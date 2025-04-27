package com.turntalienn.mytrade.trading.domain.forex.portfolio;

import com.turntalienn.mytrade.feed.api.PriceDto;
import com.turntalienn.mytrade.trading.domain.forex.common.TradingParams;
import com.turntalienn.mytrade.trading.domain.forex.feed.PriceBuilder;
import com.turntalienn.mytrade.trading.domain.forex.common.events.PriceChangedEvent;
import com.turntalienn.mytrade.trading.domain.forex.order.StopOrderBuilder;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto;
import com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto.StopOrderType;
import com.turntalienn.mytrade.trading.domain.forex.common.events.EndedTradingDayEvent;
import static com.turntalienn.mytrade.trading.domain.forex.order.OrderDto.OrderAction.BUY;
import static com.turntalienn.mytrade.trading.domain.forex.order.OrderDto.OrderAction.SELL;
import static com.turntalienn.mytrade.trading.domain.forex.portfolio.PositionDto.ExitReason.END_OF_DAY;
import static com.turntalienn.mytrade.trading.domain.forex.portfolio.PositionDto.ExitReason.RECONCILIATION_FAILED;
import static com.turntalienn.mytrade.trading.domain.forex.portfolio.PositionDto.PositionStatus.CLOSED;
import static com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto.StopOrderStatus.CREATED;
import static com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto.StopOrderStatus.FILLED;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import static java.math.BigDecimal.ONE;
import static java.util.Collections.emptyList;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioDomainShould {


    @Test
    public void update_portfolio_balance() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var position = new PositionBuilder().build();

        portfolio.addNewPosition(position.positionType(), position.filledOrder());

        var newPrice = BigDecimal.valueOf(2);
        var priceMap = new PriceBuilder().withPrice("AUDUSD", newPrice).builderMap();

        portfolio.updatePositionsPrices(priceMap);

        var modelPosition = portfolio.getPosition(position.identifier());
        assertEquals(newPrice, modelPosition.currentPrice());
    }

    @Test
    public void create_stop_loss_order() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var position = new PositionBuilder().build();
        portfolio.addNewPosition(position.positionType(), position.filledOrder());
        var stopLossOrder = new StopOrderDto(StopOrderType.STOP_LOSS, 1, CREATED, BUY, ONE, ONE, "EURUSD", 100, "id");
        var takeProfitOrder = new StopOrderDto(StopOrderType.TAKE_PROFIT, 2, FILLED, SELL, ONE, ONE, "EURUSD", 100, "id");

        var stopOrders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        stopOrders.put(StopOrderType.STOP_LOSS, stopLossOrder);

        when(builder.riskManagementService.createStopOrders(any(), any())).thenReturn(stopOrders);
        when(builder.brokerIntegrationService.placeStopOrder(any()))
                .thenReturn(new StopOrderDto(FILLED, stopLossOrder))
                .thenReturn(takeProfitOrder);
        TradingParams.take_profit_stop_enabled = false;

        portfolio.createStopOrder(new PriceChangedEvent(null, new PriceBuilder().builderMap()));

        assert_createStopOrder_withoutTakeProfitStopOrder(builder, portfolio);
    }

    private void assert_createStopOrder_withoutTakeProfitStopOrder(PortfolioHandlerBuilder builder, PortfolioService model) {
        ArgumentCaptor<StopOrderDto> placeStopOrderArgCapture = ArgumentCaptor.forClass(StopOrderDto.class);
        ArgumentCaptor<PositionDto> createStopOrdersArgCaptor = ArgumentCaptor.forClass(PositionDto.class);

        verify(builder.riskManagementService, times(1)).createStopOrders(createStopOrdersArgCaptor.capture(), any());
        verify(builder.brokerIntegrationService, times(1)).placeStopOrder(placeStopOrderArgCapture.capture());

        var createStopOrdersArgCaptured = placeStopOrderArgCapture.getValue();
        var placeStopOrderArgCaptured = createStopOrdersArgCaptor.getValue();

        assertEquals(StopOrderType.STOP_LOSS, createStopOrdersArgCaptured.type());
        assertEquals(model.getPosition("AUDUSD"), placeStopOrderArgCaptured);
    }

    @Test
    public void create_stop_loss_order_and_take_profit_order() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();

        var position = new PositionBuilder().build();
        portfolio.addNewPosition(position.positionType(), position.filledOrder());
        var stopLossOrder = new StopOrderDto(StopOrderType.STOP_LOSS, 1, CREATED, BUY, ONE, ONE, "EURUSD", 100, "id");
        var takeProfitOrder = new StopOrderDto(StopOrderType.TAKE_PROFIT, 2, FILLED, SELL, ONE, ONE, "EURUSD", 100, "id");

        var stopOrders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        stopOrders.put(StopOrderType.STOP_LOSS, stopLossOrder);
        stopOrders.put(StopOrderType.TAKE_PROFIT, takeProfitOrder);

        when(builder.riskManagementService.createStopOrders(any(), any())).thenReturn(stopOrders);
        when(builder.brokerIntegrationService.placeStopOrder(any()))
                .thenReturn(new StopOrderDto(FILLED, stopLossOrder))
                .thenReturn(takeProfitOrder);

        portfolio.createStopOrder(new PriceChangedEvent(null, null));
        assert_createStopOrder_withTakeProfitStopOrder(builder, portfolio);
    }

    private void assert_createStopOrder_withTakeProfitStopOrder(PortfolioHandlerBuilder builder, PortfolioService model) {
        ArgumentCaptor<StopOrderDto> placeStopOrderArgCapture = ArgumentCaptor.forClass(StopOrderDto.class);
        ArgumentCaptor<PositionDto> createStopOrdersArgCaptor = ArgumentCaptor.forClass(PositionDto.class);

        verify(builder.riskManagementService, times(1)).createStopOrders(createStopOrdersArgCaptor.capture(), any());
        verify(builder.brokerIntegrationService, times(2)).placeStopOrder(placeStopOrderArgCapture.capture());

        var createStopOrdersArgCaptured = placeStopOrderArgCapture.getAllValues();
        var placeStopOrderArgCaptured = createStopOrdersArgCaptor.getValue();

        assertEquals(StopOrderType.STOP_LOSS, createStopOrdersArgCaptured.get(0).type());
        assertEquals(StopOrderType.TAKE_PROFIT, createStopOrdersArgCaptured.get(1).type());
        assertEquals(model.getPosition("AUDUSD"), placeStopOrderArgCaptured);
    }

    @Test
    public void handle_stop_orders_when_price_changes() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();

        var position = new PositionBuilder().build();
        portfolio.addNewPosition(position.positionType(), position.filledOrder());
        var stopOrderDtoMap = new HashMap<Integer, StopOrderDto>();
        stopOrderDtoMap.put(0, new StopOrderBuilder().withStatus(FILLED).build());

        var stopOrders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        stopOrders.put(StopOrderType.STOP_LOSS, stopOrderDtoMap.get(0));

        when(builder.brokerIntegrationService.getStopLossOrders()).thenReturn(stopOrderDtoMap);
        when(builder.riskManagementService.createStopOrders(any(), any())).thenReturn(stopOrders);
        when(builder.brokerIntegrationService.placeStopOrder(any())).thenReturn(
                new StopOrderDto(FILLED, stopOrders.get(StopOrderType.STOP_LOSS))
        );

        var event = new PriceChangedEvent(null, new PriceBuilder().builderMap());
        portfolio.createStopOrder(event);
        portfolio.handleStopOrder(event);

        verify(builder.eventNotifier, times(1)).notify(any());
    }

    @Test
    public void process_exits_when_price_change() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();

        var cancelledPosition = new PositionBuilder()
                .withPositionStatus(CLOSED)
                .build();

        when(builder.riskManagementService.getExitPositions(any(), any()))
                .thenReturn(Arrays.asList(cancelledPosition));


        var position = new PositionBuilder().build();
        portfolio.addNewPosition(position.positionType(), position.filledOrder());
        var event = new PriceChangedEvent(null, null);

        portfolio.checkExits(event, emptyList());

        verify(builder.riskManagementService, times(1)).getExitPositions(any(), any());
        verify(builder.orderService, times(1)).createOrderFromClosedPosition(any(), any());
        verify(builder.eventNotifier, times(1)).notify(any());
    }

    @Test
    public void close_all_positions() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var position = new PositionBuilder()
                .withPositionStatus(PositionDto.PositionStatus.FILLED)
                .build();


        portfolio.addNewPosition(position.positionType(), position.filledOrder());
        assertEquals(1, portfolio.size());

        var priceMap = new HashMap<String, PriceDto>();
        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, ONE, ONE, ONE, ONE, "AUDUSD"));
        var event = new EndedTradingDayEvent(LocalDateTime.MIN, priceMap);

        var closedPositions = portfolio.closeAllPositions(END_OF_DAY, event);

        assertEquals(CLOSED, closedPositions.get(0).status());
        verify(builder.orderService, times(1)).createOrderFromClosedPosition(any(), any());
        verify(builder.eventNotifier, times(1)).notify(any());
    }

    @Test
    public void close_a_position() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var position = new PositionBuilder()
                .withPositionStatus(PositionDto.PositionStatus.FILLED)
                .build();


        portfolio.addNewPosition(position.positionType(), position.filledOrder());
        assertEquals(1, portfolio.size());

        var priceMap = new HashMap<String, PriceDto>();
        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, ONE, ONE, ONE, ONE, "AUDUSD"));
        portfolio.closePosition(position.identifier(), PositionDto.ExitReason.COUNTER_SIGNAL);

        assertEquals(0, portfolio.getPositions().size());
    }

    @Test
    public void add_new_position_to_portfolio() {
        var portfolio = new PortfolioHandlerBuilder().build();

        var position = new PositionBuilder().build();
        portfolio.addNewPosition(position.positionType(), position.filledOrder());
        assertEquals(1, portfolio.size());
    }

    @Test
    public void process_reconciliation_with_broker_fail() {
        var builder = new PortfolioHandlerBuilder();
        var priceMap = new PriceBuilder().builderMap();
        var event = new PriceChangedEvent(LocalDateTime.now(), priceMap);
        var portfolio = builder.build();

        PositionDto position = new PositionBuilder().build();
        portfolio.addNewPosition(position.positionType(), position.filledOrder());

        var filledOrderDtoMap = new HashMap<String, FilledOrderDto>();
        when(builder.brokerIntegrationService.getPositions()).thenReturn(filledOrderDtoMap);

        portfolio.processReconciliation(event);

        assertEquals(0, portfolio.size());
        assertEquals(RECONCILIATION_FAILED, portfolio.getPosition(position.identifier()).exitReason());
    }

    @Test
    public void process_reconciliation_with_broker_success() {
        var builder = new PortfolioHandlerBuilder();
        var priceMap = new PriceBuilder().builderMap();
        var event = new PriceChangedEvent(LocalDateTime.now(), priceMap);
        var portfolio = builder.build();

        PositionDto position = new PositionBuilder().build();
        portfolio.addNewPosition(position.positionType(), position.filledOrder());

        var filledOrderDtoMap = new HashMap<String, FilledOrderDto>();
        var filledOrderBuilder = new FilledOrderBuilder();
        filledOrderBuilder.withSymbol(position.symbol());
        filledOrderDtoMap.put(position.symbol(), filledOrderBuilder.build());
        when(builder.brokerIntegrationService.getPositions()).thenReturn(filledOrderDtoMap);

        portfolio.processReconciliation(event);

        assertEquals(1, portfolio.size());
        assertNull(portfolio.getPosition(position.identifier()).exitReason());
    }


    @Test
    public void change_position_quantity() {
        //TODO
    }

    @Test
    public void handle_order_filled_event() {
        //TODO
    }

    @Test
    public void handle_stop_order_filled_event() {
        //TODO
    }
}