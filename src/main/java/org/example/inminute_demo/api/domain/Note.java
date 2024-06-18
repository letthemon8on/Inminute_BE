package org.example.inminute_demo.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Note extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    private String name;
    private String script;
    private String summary;

    // 추후에 spring security 적용해서 session 유저 가져와 구현 필요
    @ElementCollection
    private List<String> participants;
}
