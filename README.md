## Disclaimer
educational purpose only


## Features
* Persistence: Persistence is achieved through the H2
* Backtesting: Run a simulation of your buy/sell strategy.
* Stop order/ Limit order management
* Broker reconciliation
* Multi position for currency pair
* Daily summary of profit/loss: Provide a daily summary of your profit/loss.
* Performance status report: Provide a performance status of your current trades.

# Tech details
* Java 17
* Event driven architecture
* Maven multi-modules
* Java modularization
* Packaging by domain
* Domain driven design (DDD)
* BDD with cucumber
* Multiple design patterns (Strategy, factory, builder, Observer...)
* Cross-cutting concern

![Alt text](assets/trading-system.png?raw=true "Trading system")

## How to run
```
mvn package && \
java -jar runner/target/runner-1.0-SNAPSHOT.jar
``` 

### Using Docker

```
docker build . -t trading-engine
docker run --rm -p 8080:8080 trading-engine
```

