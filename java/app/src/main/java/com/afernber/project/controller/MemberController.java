package com.afernber.project.controller;

import com.afernber.project.domain.dto.MemberDTO;
import com.afernber.project.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

    private MemberService service;

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMember(id));
    }

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getMembers() {
        return ResponseEntity.ok(service.getMembers());
    }

    @PostMapping
    public ResponseEntity<Void> createMember(@RequestBody MemberDTO member) {
        service.createMember(member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO) {
        return ResponseEntity.ok(service.updateMember(id, memberDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        service.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
