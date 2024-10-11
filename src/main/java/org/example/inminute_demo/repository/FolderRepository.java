package org.example.inminute_demo.repository;

import org.example.inminute_demo.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findAllByMember_Id(Long memberId);
}
