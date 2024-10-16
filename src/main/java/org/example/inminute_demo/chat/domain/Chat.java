package org.example.inminute_demo.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.inminute_demo.domain.BaseEntity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.Assert.notNull;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long memberId;

    private Long noteId;

    @Enumerated(STRING)
    private MessageType type;

    @Column(columnDefinition = "TEXT")
    private String content;
}