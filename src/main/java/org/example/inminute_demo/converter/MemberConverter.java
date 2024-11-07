package org.example.inminute_demo.converter;

import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.dto.member.response.MemberResponse;
import org.example.inminute_demo.security.dto.LoginResponse;

public class MemberConverter {

    public static MemberResponse toMemberResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .build();
    }

    public static LoginResponse toLoginResponse(Member member) {
        return LoginResponse.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .role(member.getRole())
                .isFirst(member.getIsFirst())
                .build();
    }
}
