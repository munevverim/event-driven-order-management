package com.munevver.inventory_service.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConfig {

    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        // Hatalı mesajları "topic-adı.DLQ" kuyruğuna gönderen recoverer tanımlıyoruz
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (consumerRecord, exception) -> {
                    log.error("Failed to process message in topic: {}, partition: {}, offset: {}. Sending to DLQ.",
                            consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), exception);
                    return new TopicPartition(consumerRecord.topic() + ".DLQ", consumerRecord.partition());
                });

        // 3 kez tekrar dene, her deneme arasında 2 saniye bekle
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 3L));

        // JSON formatlama gibi kalıcı hataları doğrudan tekrar denemeden DLQ'ya yönlendir
        errorHandler.addNotRetryableExceptions(com.fasterxml.jackson.core.JsonProcessingException.class);

        return errorHandler;
    }
}
