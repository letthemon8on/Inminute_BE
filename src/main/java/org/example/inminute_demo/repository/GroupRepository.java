package org.example.inminute_demo.repository;

import org.example.inminute_demo.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
