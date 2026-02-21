package org.spon.uspehliftbot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spon.uspehliftbot.entity.User;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;


@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    public final TaskScheduler taskScheduler;

    @Getter
    private final Map<User, List<ScheduledFuture<?>>> scheduledFutures = new HashMap<>();

    public void scheduling(final Runnable task, Instant instant, User user) {
        ScheduledFuture<?> schedule = taskScheduler.schedule(task, instant);
        scheduledFutures.computeIfAbsent(user, k -> new ArrayList<>()).add(schedule);
    }

    public void cancelSchedulingForUser(User user) {
        Optional.ofNullable(scheduledFutures.get(user))
                .ifPresent(
                        futuresList -> futuresList
                                .forEach(scheduledFuture -> scheduledFuture.cancel(true))
                );
        scheduledFutures.remove(user);
    }

}
