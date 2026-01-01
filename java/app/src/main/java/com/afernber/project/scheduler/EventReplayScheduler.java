package com.afernber.project.scheduler;

import com.afernber.project.service.FailedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventReplayScheduler {

    private final FailedEventService failedEventService;

    /**
     * Runs every 30 minutes to check for failed events and attempt a replay.
     * Cron format: second, minute, hour, day, month, day of week
     */
    @Scheduled(cron = "${app.scheduling.replay-cron}")
    public void scheduleReplay() {
        log.info("Scheduler triggered: Checking for PENDING_REPLAY events");
        failedEventService.replayAllEvents();
    }
}