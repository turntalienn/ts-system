package com.turntalienn.mytrade.trading.domain.forex.riskmanagement.stopordercreation;

public class StopOrderCreationFactory {

    public static StopOrderCreator factory(
            StopOrderConfigDto stopOrderDto
    ) {
        return new StopOrderCreatorFixed(stopOrderDto);
    }
}
