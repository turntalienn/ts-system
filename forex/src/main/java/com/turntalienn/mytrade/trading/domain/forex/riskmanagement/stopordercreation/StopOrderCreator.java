package com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation;

import com.turntalienn.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.turntalienn.mytrade.trading.domain.forex.common.events.Event;

import java.util.Optional;

public interface StopOrderCreator {

    void createContext(PositionDto.PositionType type);

    StopOrderDto getHardStopLoss(PositionDto position);

    StopOrderDto getProfitStopOrder(PositionDto position);

    Optional<StopOrderDto> getEntryStopOrder(PositionDto position, Event event);

    Optional<StopOrderDto> getTrailingStopOrder(PositionDto position, Event event);
}
