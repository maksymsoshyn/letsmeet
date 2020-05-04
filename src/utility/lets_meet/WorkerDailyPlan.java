package utility.lets_meet;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class WorkerDailyPlan {

   private Duration working_hours;
   private List<Duration> planned_meeting;


    public WorkerDailyPlan(){}

    public WorkerDailyPlan(Duration working_hours, List<Duration> planned_meeting) {
        this.working_hours = working_hours;
        this.planned_meeting = planned_meeting;
    }

    public Duration getWorking_hours() {
        return working_hours;
    }

    public void setWorking_hours(Duration working_hours) {
        this.working_hours = working_hours;
    }

    public List<Duration> getPlanned_meeting() {
        return planned_meeting;
    }

    public void setPlanned_meeting(List<Duration> planned_meeting) {
        this.planned_meeting = planned_meeting;
    }


    public List<Duration> getPlanInWindow(Duration window){
        return getPlanned_meeting()
                .stream()
                .filter(meeting -> {
                    long windowStartTime = window.getStart().getTime();
                    long meetingEndTime = meeting.getEnd().getTime();
                    long windowEndTime = window.getEnd().getTime();
                    return (meetingEndTime <= windowEndTime && meetingEndTime >= windowStartTime);
                })
                .collect(Collectors.toList());
    }

    @Override
    public String toString(){
        return "Working hours: \n".concat(working_hours.toString()).concat("\nList of planned meetings: ").concat(planned_meeting.toString());
    }

    public static class Duration{

       private Date start;
       private Date end;

       public Duration(){
       }


        // In constructing Duration objects it is easier to work with Date while it has access to hour and minute fields
        // (JSON doesn't want to map LocalTime while there is no standard constructor)
       public Duration(Date start, Date end) {
           this.start = start;
           this.end = end;
       }

       public Date getStart() {
            return start;
        }

       public Date getEnd() {
            return end;
        }

        // In computation it is easier to work with ints
       public int getStartAsInt(){
           return Integer.parseInt(String.format("%d%02d", start.getHours(), start.getMinutes()));
       }

        public int getEndAsInt(){
            return Integer.parseInt(String.format("%d%02d", end.getHours(), end.getMinutes()));
        }
        @Override
        public String toString(){
           return String.format("[\"%d:%02d\",\"%d:%02d\"]", start.getHours(), start.getMinutes() ,end.getHours(), end.getMinutes());
        }
   }
}
