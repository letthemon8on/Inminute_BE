package org.example.inminute_demo.repository.schedule;

import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.Schedule;

import java.util.List;

public interface ScheduleCustomRepository {

    List<Schedule> findByYearAndMonth(Integer year, Integer month, Member member);

}
