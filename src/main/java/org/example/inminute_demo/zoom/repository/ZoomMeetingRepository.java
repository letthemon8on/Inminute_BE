package org.example.inminute_demo.zoom.repository;

import org.example.inminute_demo.zoom.domain.ZoomMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoomMeetingRepository extends JpaRepository<ZoomMeeting, Long> {
}