package com.afernber.project.controller;

import com.afernber.project.service.FailedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventReplayController {

    private final FailedEventService failedEventService;

    @PostMapping("/replay/{id}")
    public ResponseEntity<String> replayEvent(@PathVariable Long id) {
        failedEventService.replayEvent(id);
        return ResponseEntity.ok("Replay triggered for event " + id);
    }

    @PostMapping("/replay/all")
    public ResponseEntity<String> replayAll() {
        failedEventService.replayAllEvents();
        return ResponseEntity.ok("All events are triggered for replay");
    }
}
