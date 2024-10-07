package org.example.inminute_demo.repository;

import org.example.inminute_demo.domain.ZoomMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoomMeetingRepository extends JpaRepository<ZoomMeeting, Long> {
}