package com.afernber.project.service;

import com.afernber.project.domain.dto.MemberDTO;

import java.util.List;

public interface MemberService {
    MemberDTO getMember(Long id);

    List<MemberDTO> getMembers();

    void createMember(MemberDTO member);

    MemberDTO updateMember(Long id, MemberDTO memberDTO);

    void deleteMember(Long id);
}
