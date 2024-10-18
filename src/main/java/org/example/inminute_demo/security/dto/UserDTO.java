package org.example.inminute_demo.security.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String role;
    private String name;
    private String username;

    private Boolean isFirst;
    private String nickname;
}
