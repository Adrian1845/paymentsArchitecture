package com.afernber.project.service.kafka.factory;

import com.afernber.project.service.kafka.strategies.KafkaStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class KafkaStrategyFactory {

    private final Map<String, KafkaStrategy> strategyMap = new HashMap<>();

    public KafkaStrategyFactory(List<KafkaStrategy> strategies) {
        for (KafkaStrategy strategy : strategies) {
            for (String type : strategy.getSupportedTypes()) {
                strategyMap.put(type, strategy);
            }
        }
    }

    public KafkaStrategy getStrategy(String eventType) {
        return Optional.ofNullable(strategyMap.get(eventType))
                .orElseThrow(() -> new RuntimeException("No strategy for: " + eventType));
    }
}
