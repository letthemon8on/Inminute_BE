package org.example.inminute_demo.converter;

import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.dto.member.response.MemberResponse;
import org.example.inminute_demo.security.dto.OAuth2Response;
import org.example.inminute_demo.security.dto.UserDTO;

public class MemberConverter {

    public static MemberResponse toMemberResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .build();
    }

    public static Member toMember(String username, OAuth2Response oAuth2Response) {
        return Member.builder()
                .username(username)
                .email(oAuth2Response.getEmail())
                .name(oAuth2Response.getName())
                .role("ROLE_USER")
                .isFirst(true)
                .build();
    }

    public static UserDTO toUserDTO(Member member) {
        return UserDTO.builder()
                .username(member.getUsername())
                .name(member.getName())
                .role(member.getRole())
                .isFirst(member.getIsFirst())
                .build();
    }
}
