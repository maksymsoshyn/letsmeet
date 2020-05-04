package utility.lets_meet;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Main {

    private static Logger logger = Logger.getLogger("LetsMeet");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    public static void main(String[] args){

        ObjectMapper mapper = new ObjectMapper()
                .setDateFormat(dateFormat)
                // Enables parsing both JS Object (which was a format of calendar in a task) and standard JSON
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                // Enables parsing object as string from arguments if the argument is not a file
                .enable(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS);
        logger.log(Level.INFO, "Object mapper was created");

        File planFile1 = new File(args[0]);
        File planFile2 = new File(args[1]);

        try {
            WorkerDailyPlan workerDailyPlan1 = (planFile1.isFile()) ?
                    mapper.readValue(planFile1, WorkerDailyPlan.class):
                    mapper.readValue(args[0], WorkerDailyPlan.class);
            logger.log(Level.INFO, "Plan 1 was successfully parsed and mapped to object");

            WorkerDailyPlan workerDailyPlan2 = (planFile2.isFile()) ?
                    mapper.readValue(planFile2, WorkerDailyPlan.class):
                    mapper.readValue(args[1], WorkerDailyPlan.class);
            logger.log(Level.INFO, "Plan 2 was successfully parsed and mapped to object");

            Date meetingDuration = getMeetingDuration(args[2]);
            logger.log(Level.INFO, "Meeting duration is set up. Time to find a windows for meeting planning");
            List<WorkerDailyPlan.Duration> availableForNewMeetings = getTimeForMeetings(workerDailyPlan1, workerDailyPlan2, meetingDuration);
            logger.log(Level.INFO, "Windows search was ended successfully");
            System.out.println(availableForNewMeetings);
            writeResult(availableForNewMeetings);


        } catch (IOException | ParseException e) {
            logger.log(Level.SEVERE, "Something went wrong while parsing plan 1 or plan 2 or meeting duration option or writing to file. Error message: ".concat(e.getMessage()));
            e.printStackTrace();
        }
    }

    private static void writeResult(List<WorkerDailyPlan.Duration> availableForNewMeetings) throws IOException {
        FileWriter myWriter = new FileWriter("lets_meet_result.txt");
        myWriter.write(String.valueOf(availableForNewMeetings));
        myWriter.close();
    }


    private static List<WorkerDailyPlan.Duration> getTimeForMeetings(WorkerDailyPlan plan1, WorkerDailyPlan plan2, Date meetingDuration){
        WorkerDailyPlan.Duration windowForPlanning = getWindowForPlanning(plan1.getWorking_hours(), plan2.getWorking_hours());
        List<WorkerDailyPlan.Duration> generalDailyPlanList = getGeneralWorkingPlanForWindow(plan1, plan2, windowForPlanning);

        Set<WorkerDailyPlan.Duration> availableForNewMeetings = new TreeSet<>(new DurationComparator());

        if (generalDailyPlanList.size() > 0) {

            availableForNewMeetings.addAll(tryToAddExtremeWindowForPlanning(generalDailyPlanList, windowForPlanning, meetingDuration));
            System.out.println(generalDailyPlanList);
            for (int i = 1; i<generalDailyPlanList.size(); i++){
                WorkerDailyPlan.Duration meeting1 = generalDailyPlanList.get(i-1);
                WorkerDailyPlan.Duration meeting2 = generalDailyPlanList.get(i);

                if (canPlan(meeting1.getEndAsInt(), meeting2.getStartAsInt(), getDateAsInt(meetingDuration))) {
                    logger.log(Level.INFO, "New entry on list of available windows for meeting");
                    availableForNewMeetings.add(
                            new WorkerDailyPlan.Duration(meeting1.getEnd(), meeting2.getStart())
                    );
                }

            }

        }
        return new ArrayList<>(availableForNewMeetings);
    }

    private static int getDateAsInt(Date toConvert){
        return Integer.parseInt(String.format("%d%02d", toConvert.getHours(), toConvert.getMinutes()));
    }

    private static List<WorkerDailyPlan.Duration> getGeneralWorkingPlanForWindow(WorkerDailyPlan plan1, WorkerDailyPlan plan2,  WorkerDailyPlan.Duration windowForPlan){
        Set<WorkerDailyPlan.Duration> generalDailyPlanSet = new TreeSet<>(new DurationComparator());
        generalDailyPlanSet.addAll(plan1.getPlanInWindow(windowForPlan));
        generalDailyPlanSet.addAll(plan2.getPlanInWindow(windowForPlan));
        return new ArrayList<>(generalDailyPlanSet);
    }

    private static Set<WorkerDailyPlan.Duration> tryToAddExtremeWindowForPlanning(
            List<WorkerDailyPlan.Duration> generalDailyPlanList,
            WorkerDailyPlan.Duration windowForPlanning,
            Date meetingDuration){

        Set<WorkerDailyPlan.Duration> availableForNewMeetings = new TreeSet<>(new DurationComparator());
        WorkerDailyPlan.Duration firstMeeting = generalDailyPlanList.get(0);
        WorkerDailyPlan.Duration lastMeeting = generalDailyPlanList.get(generalDailyPlanList.size()-1);

        if (canPlan(windowForPlanning.getStartAsInt(), firstMeeting.getStartAsInt(), getDateAsInt(meetingDuration)))
            availableForNewMeetings.add(new WorkerDailyPlan.Duration(windowForPlanning.getStart(), firstMeeting.getStart()));

        if (canPlan(windowForPlanning.getEndAsInt(), lastMeeting.getEndAsInt(), getDateAsInt(meetingDuration)))
            availableForNewMeetings.add(new WorkerDailyPlan.Duration(lastMeeting.getEnd(), windowForPlanning.getEnd()));

        return availableForNewMeetings;

    }
    private static boolean canPlan(int start, int end, int duration){
        return end - start >= duration;
    }


    private static WorkerDailyPlan.Duration getWindowForPlanning(WorkerDailyPlan.Duration work_hours1, WorkerDailyPlan.Duration work_hours2){
        Date work_hours1Start = work_hours1.getStart();
        Date work_hours1End = work_hours1.getEnd();
        Date work_hours2Start = work_hours2.getStart();
        Date work_hours2End = work_hours2.getEnd();

        Date windowForPlanningStart = work_hours1Start.getTime() > work_hours2Start.getTime() ? work_hours1Start : work_hours2Start;
        Date windoForPlanningEnd = work_hours1End.getTime() < work_hours2End.getTime() ? work_hours1End : work_hours2End;

        return new WorkerDailyPlan.Duration(windowForPlanningStart, windoForPlanningEnd);
    }


    private static Date getMeetingDuration(String durationStr) throws ParseException {
        return dateFormat.parse(durationStr.replaceAll("[\\[\\]]", ""));
    }
}