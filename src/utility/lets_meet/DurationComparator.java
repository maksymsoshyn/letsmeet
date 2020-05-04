package utility.lets_meet;

import java.util.Comparator;
import utility.lets_meet.WorkerDailyPlan.Duration;

public class DurationComparator implements Comparator<Duration> {
    @Override
    public int compare(Duration duration1, Duration duration2) {
        long duration1StartTime = duration1.getStart().getTime();
        long duration1EndTime = duration1.getEnd().getTime();
        long duration2StartTime = duration2.getStart().getTime();
        long duration2EndTime = duration2.getEnd().getTime();

        if (duration1StartTime != duration2StartTime)
            return Long.compare(duration1StartTime, duration2StartTime);
        else
            return Long.compare(duration1EndTime, duration2EndTime);
    }
}
