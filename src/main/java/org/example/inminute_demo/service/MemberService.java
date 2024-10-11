package org.example.inminute_demo.service;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member loadMemberByCustomOAuth2User(CustomOAuth2User customOAuth2User) {

        String username = customOAuth2User.getUsername();

        return memberRepository.findByUsername(username);
    }
}
