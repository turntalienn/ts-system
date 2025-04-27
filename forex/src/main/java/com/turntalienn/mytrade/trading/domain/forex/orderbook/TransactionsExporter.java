package com.turntalienn.mytrade.trading.domain.forex.orderbook;

import com.turntalienn.mytrade.common.file.WriteFileHelper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class TransactionsExporter {

    public void exportCsv(List<CycleHistoryDto> transactions, String filepath) throws IOException {
        Path path = Paths.get(filepath);
        if (!Files.exists(path)){
            File file = new File(filepath);
            file.createNewFile();
        }
        WriteFileHelper.write(filepath, getHeader() + "\n");
        for (CycleHistoryDto item : transactions) {
            for (Map.Entry<String, TransactionDto> trans : item.getTransactions().entrySet()) {
                List<String> line = Arrays.asList(
                        trans.getValue().getIdentifier(),
                        trans.getValue().getPosition() != null ? toString(trans.getValue().getPosition().initPrice()) : "",
                        trans.getValue().getPosition() != null ? toString(trans.getValue().getPosition().currentPrice()) : "",
                        trans.getValue().getPosition() != null ? toString(trans.getValue().getPosition().quantity()) : "",
                        trans.getValue().getPosition() != null ? toString(trans.getValue().getPosition().initPrice().multiply(BigDecimal.valueOf(trans.getValue().getPosition().quantity()))) : "",
                        trans.getValue().getPosition() != null ? toString(trans.getValue().getPosition().currentPrice().multiply(BigDecimal.valueOf(trans.getValue().getPosition().quantity()))) : "",
                        trans.getValue().getPosition() != null ? toString(trans.getValue().getPosition().currentPrice().subtract(trans.getValue().getPosition().initPrice())) : "",
                        trans.getValue().getFilledOrder() != null ? toString(trans.getValue().getFilledOrder().action()) : "",
                        trans.getValue().getPosition() != null ? toString(trans.getValue().getPosition().timestamp()) : "",
                        //trans.getValue().getPosition() != null ? trans.getValue().getPosition().getPlacedStopLoss().getPrice().toString(): "",
                        //trans.getValue().getPosition() != null ? trans.getValue().getPosition().getTakeProfitOrder().getPrice().toString(): "",
                        trans.getValue().getPosition() != null ? toString(trans.getValue().getPosition().exitReason()) : "",
                        trans.getValue().getState() != null ? toString(trans.getValue().getState()) : "",
                        ""
                );
                WriteFileHelper.append(filepath, String.join(",", line) + "\n");
            }
        }
    }

    private String toString(Object ob) {
        if (ob != null) {
            return ob.toString();
        }
        return "";
    }

    private List<String> getHeader() {
        List<String> line = Arrays.asList(
                "Identifier",
                "Init price",
                "End price",
                "Quantidade",
                "Init amount",
                "End amount",
                "Result",
                "Action",
                "Timestamp",
                "Exit reason",
                "State",
                ""
        );
        return line;
    }
}

