package org.example.inminute_demo.repository.schedule;

import org.example.inminute_demo.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
