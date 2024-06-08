package org.example.inminute_demo.zoom.repository;

import org.example.inminute_demo.zoom.domain.ZoomMeetingObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoomMeetingRepository extends JpaRepository<ZoomMeetingObjectEntity, Long> {
}