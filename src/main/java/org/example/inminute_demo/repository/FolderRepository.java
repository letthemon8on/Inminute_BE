package org.example.inminute_demo.repository;

import org.example.inminute_demo.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
}
