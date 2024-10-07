package org.example.inminute_demo.security.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {

        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getName() {

        Map<String, Object> properties = (Map<String, Object>) attribute.get("properties");

        // properties 맵에서 nickname 추출
        String nickname = (String) properties.get("nickname");
        return nickname;
    }
}
