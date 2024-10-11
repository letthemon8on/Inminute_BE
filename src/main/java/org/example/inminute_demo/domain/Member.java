package org.example.inminute_demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String name;

    private String email;

    private String role;

    private String nickname;

    private Boolean isFirst;

    public void updateIsFirst() { this.isFirst = false; }
    public void updateNickname(String nickname) { this.nickname = nickname; }
    public void updateEmail(String email) { this.email = email; }
    public void updateName(String name) { this.name = name; }


}
