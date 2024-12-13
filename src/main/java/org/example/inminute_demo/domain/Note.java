package org.example.inminute_demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "note")
public class Note extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteJoinMember> noteJoinMembers = new ArrayList<>();

    private String name;

    @Column(columnDefinition = "LONGTEXT")
    private String script;
    private String summary;

    @Column(nullable = false, unique = true)
    private String uuid; // 링크 공유를 위해 생성

    private Boolean isStart; // 회의 시작/종료 여부

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
    }

    public void updateName(String name) { this.name = name; }
    public void updateScript(String script) { this.script = script; }
    public void updateSummary(String summary) { this.summary = summary; }
    public void updateIsStart(Boolean isStart) { this.isStart = isStart; }

    public void deleteFolder() { this.folder = null; }

    public void toScript(String script) {
        this.script = script;
    }
}
