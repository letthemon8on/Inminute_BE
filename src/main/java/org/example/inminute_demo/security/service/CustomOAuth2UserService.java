package org.example.inminute_demo.security.service;

import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.converter.MemberConverter;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.security.dto.*;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // naver, google 구분하기 위해 registrationId 가져옴
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        System.out.println("userRequest clientRegistration : " + userRequest.getClientRegistration());
        // token을 통해 응답받은 회원정보
        System.out.println("oAuth2User : " + oAuth2User);

        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값 생성
        String username = oAuth2Response.getProvider()+oAuth2Response.getProviderId();
        // 회원 조회
        Optional<Member> existData = memberRepository.findByUsername(username);

        // 한 번도 로그인 한 적 없는 경우 -> 유저 정보 save
        if (existData.isEmpty()) {

            Member member = MemberConverter.toMember(username, oAuth2Response);

            memberRepository.save(member);

            UserDTO userDTO = MemberConverter.toUserDTO(member);

            return new CustomOAuth2User(userDTO);
        }
        // 한 번이라도 로그인 하여 유저 정보가 존재하는 경우 -> 유저 정보 update
        else {

            Member member = existData.get();
            member.updateEmail(oAuth2Response.getEmail());
            member.updateName(oAuth2Response.getName());

            memberRepository.save(member);

            UserDTO userDTO = MemberConverter.toUserDTO(member);

            return new CustomOAuth2User(userDTO);
        }
    }
}
