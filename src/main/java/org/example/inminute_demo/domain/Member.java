package org.example.inminute_demo.domain;

import jakarta.persistence.*;
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

    @Column(nullable = false, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    public void updateIsFirst() { this.isFirst = false; }
    public void updateNickname(String nickname) { this.nickname = nickname; }
    public void updateEmail(String email) { this.email = email; }
    public void updateName(String name) { this.name = name; }


}
