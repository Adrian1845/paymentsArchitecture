package com.afernber.project.controller;

import com.afernber.project.domain.response.ActionResponse;
import com.afernber.project.service.FailedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventReplayController {

    private final FailedEventService failedEventService;

    @PostMapping("/replay/{id}")
    public ResponseEntity<ActionResponse> replayEvent(@PathVariable Long id) {
        failedEventService.replayEvent(id);
        return ResponseEntity.ok(new ActionResponse(
                "Replay triggered for event " + id,
                LocalDateTime.now()
        ));
    }

    @PostMapping("/replay/all")
    public ResponseEntity<ActionResponse> replayAll() {
        failedEventService.replayAllEvents();
        return ResponseEntity.ok(new ActionResponse(
                "All events are triggered for replay",
                LocalDateTime.now()
        ));
    }
}
