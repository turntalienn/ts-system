package com.turntalienn.mytrade.trading.domain.forex.portfolio;

import com.turntalienn.mytrade.trading.domain.forex.brokerintegration.BrokerIntegrationService;
import com.turntalienn.mytrade.trading.domain.forex.order.OrderDto;

import java.util.List;
import java.util.Map;


class PortfolioChecker {
    private final BrokerIntegrationService executionHandler;

    public PortfolioChecker(BrokerIntegrationService executionHandler) {
        this.executionHandler = executionHandler;
    }

    /**
     * Check if the local portfolio is in sync with the portfolio on the broker
     */
    public void process(List<PositionDto> localPositions) throws ReconciliationException {
        Map<String, FilledOrderDto> remotePositions = executionHandler.getPositions();

        if (localPositions.isEmpty() && remotePositions.isEmpty()) {
            return;
        }

        if (localPositions.size() != remotePositions.size()) {
            throw new ReconciliationException("Portfolio is not in sync", localPositions, remotePositions);
        }

        if (localPositions.size() == remotePositions.size()) {
            checkEveryPosition(localPositions, remotePositions);
        }
    }

    private void checkEveryPosition(
            List<PositionDto> localPositions,
            Map<String, FilledOrderDto> remotePositions
    ) throws ReconciliationException {
        for (PositionDto position : localPositions) {
            String symbol = position.symbol();
            if (!remotePositions.containsKey(symbol)) {
                throw new ReconciliationException("Position key mismatch", localPositions, remotePositions);
            }
            OrderDto.OrderAction orderAction = position.positionType().getOrderAction();
            if (!remotePositions.get(symbol).action().equals(orderAction)) {
                throw new ReconciliationException("Position action mismatch", localPositions, remotePositions);
            }
        }
    }
}
