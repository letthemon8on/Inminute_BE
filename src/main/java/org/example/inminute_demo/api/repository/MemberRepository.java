package org.example.inminute_demo.api.repository;

import org.example.inminute_demo.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
