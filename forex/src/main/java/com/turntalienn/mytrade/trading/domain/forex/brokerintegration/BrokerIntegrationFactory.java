package com.turntalienn.mytrade.trading.domain.forex.brokerintegration;

import com.turntalienn.mytrade.trading.api.ExecutionType;
import com.turntalienn.mytrade.trading.domain.forex.common.TradingParams;

public class BrokerIntegrationFactory {

    public static BrokerIntegrationService factory(ExecutionType executionType){
        if (executionType == ExecutionType.BROKER) {
            return new InteractiveBrokerOrderExecution(
                    TradingParams.brokerHost,
                    TradingParams.brokerPort,
                    TradingParams.brokerClientId
            );
        }
        return new SimulatedOrderExecution();
    }
}
