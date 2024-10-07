package org.example.inminute_demo.domain;

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

    @Column(columnDefinition = "LONGTEXT")
    private String script;
    private String summary;

    public void update(String name, String script, String summary) {
        if (name != null) {
            this.name = name;
        }
        if (script != null) {
            this.script = script;
        }
        if (summary != null) {
            this.summary = summary;
        }
    }

    public void toScript(String script) {
        this.script = script;
    }
}
