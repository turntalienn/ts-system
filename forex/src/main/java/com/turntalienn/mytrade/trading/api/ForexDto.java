package com.turntalienn.mytrade.trading.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ForexDto(
        String systemName,
        LocalDateTime startDay,
        LocalDateTime endDay,
        BigDecimal equity,
        SessionType sessionType,
        ExecutionType executionType
) {
}
