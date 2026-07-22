package com.munevver.order_service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxCleanupScheduler {

    private final OutboxRepository outboxRepository;

    // Her gün sabaha karşı 4:00'te çalışır (0 0 4 * * *)
    @Scheduled(cron = "0 0 4 * * *")
    public void cleanOldOutboxRecords() {
        log.info("Outbox cleanup job started.");
        
        // 24 saatten eski kayıtları temizle
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        int deletedCount = outboxRepository.deleteByCreatedAtBefore(cutoff);
        
        log.info("Outbox cleanup job finished. Deleted {} old outbox records.", deletedCount);
    }
}
