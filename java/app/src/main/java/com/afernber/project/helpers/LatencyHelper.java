package com.afernber.project.helpers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LatencyHelper {

    /*** Make the thread sleep for 1s to empower redis implementation
     *
     */
    public static void simulateLatency() {
        try {
            log.warn("Simulating slow database fetch... (1 second delay)");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Latency simulation interrupted", e);
        }
    }

}
