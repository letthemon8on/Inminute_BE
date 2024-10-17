package org.example.inminute_demo.service;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.converter.MemberConverter;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.dto.member.request.MemberRequest;
import org.example.inminute_demo.dto.member.response.MemberResponse;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.example.inminute_demo.security.dto.LoginResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member loadMemberByCustomOAuth2User(CustomOAuth2User customOAuth2User) {

        String username = customOAuth2User.getUsername();

        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public void addDetail(CustomOAuth2User customOAuth2User, MemberRequest memberRequest) {

        Member member = memberRepository.findByUsername(customOAuth2User.getUsername())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        if (!member.getIsFirst()) {
            throw new GeneralException(ErrorStatus.NOT_FIRST_LOGIN);
        }

        if (memberRequest.getNickname() != null) {
            member.updateNickname(memberRequest.getNickname());
        }

        member.updateIsFirst();
        memberRepository.save(member);
    }

    public LoginResponse getMemberDetail(CustomOAuth2User customOAuth2User) {

        Member member = memberRepository.findByUsername(customOAuth2User.getUsername())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        return MemberConverter.toLoginResponse(member);
    }
}
