package org.example.inminute_demo.api.repository;

import org.example.inminute_demo.api.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    Folder findByName(String name);
}
