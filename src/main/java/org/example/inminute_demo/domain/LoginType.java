package org.example.inminute_demo.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    KAKAO("카카오"), GOOGLE("구글");

    private final String loginType;
}
