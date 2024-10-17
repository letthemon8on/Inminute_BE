package org.example.inminute_demo.dto.member.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequest {

    private String nickname;
    private String redirectUrl;
}
