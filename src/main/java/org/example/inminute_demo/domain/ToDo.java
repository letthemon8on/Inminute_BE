package org.example.inminute_demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "to_do")
public class ToDo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;
    private String username;
    private String nickname;

    private String content;

    private Boolean isDone;

    public void updateContent(String content) { this.content = content; }
    public void updateIsDone(Boolean isDone) { this.isDone = isDone; }
}
