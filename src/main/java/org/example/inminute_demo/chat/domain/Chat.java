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
@Table(name = "chat")
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String username;

    private String uuid;

    @Enumerated(STRING)
    private MessageType type;

    @Column(columnDefinition = "TEXT")
    private String content;

    public void updateContent(String content) {
        if (content != null) {
            this.content = content;
        }
    }

    public void updateType(MessageType type) {
        if (type != null) {
            this.type = type;
        }
    }
}