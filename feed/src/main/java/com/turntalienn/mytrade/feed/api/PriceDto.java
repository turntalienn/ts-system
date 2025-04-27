package com.turntalienn.mytrade.feed.api;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

public record PriceDto(
        LocalDateTime timestamp,
        BigDecimal open,
        BigDecimal close,
        BigDecimal high,
        BigDecimal low,
        String symbol
) {
}
