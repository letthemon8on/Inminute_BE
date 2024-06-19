package org.example.inminute_demo.api.repository;

import org.example.inminute_demo.api.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findAllByUserEntity_Username(String username);
}
