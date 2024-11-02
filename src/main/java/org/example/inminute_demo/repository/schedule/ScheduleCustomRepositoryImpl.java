package org.example.inminute_demo.repository.schedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.Schedule;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.example.inminute_demo.domain.QSchedule.schedule;

@Repository
@RequiredArgsConstructor
public class ScheduleCustomRepositoryImpl implements ScheduleCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Schedule> findByYearAndMonth(Integer year, Integer month, Member member) {
        return jpaQueryFactory
                .selectFrom(schedule)
                .where(schedule.startDateTime.year().eq(year)
                        .and(schedule.startDateTime.month().eq(month))
                        .and(schedule.member.eq(member)))
                .fetch();
    }
}
