package com.logic.project.service;

import com.logic.project.domain.Member;
import com.logic.project.dto.MemberResponse;
import com.logic.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // 로그인한 회원 정보 조회
    public MemberResponse getMember(String username) {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        return MemberResponse.from(member);
    }

    // 전체 회원 조회 - 관리자용
    public List<MemberResponse> getAllMembers() {

        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    // 회원 ID로 조회 - 관리자용
    public MemberResponse getMemberById(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        return MemberResponse.from(member);
    }
}