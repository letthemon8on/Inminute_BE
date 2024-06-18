package org.example.inminute_demo.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant { // 참여자 추가/삭제 로직 구현 필요

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    private String name;
}
