package com.in_sync.daos;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.in_sync.models.Log;
import com.in_sync.models.LogSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddData {
    private LogsFirebaseService _service;

    public AddData() {
        _service = new LogsFirebaseService();
    }

    public void addDataScenario1() {
        Random rd = new Random();
        String scenarioId = "36b06ee8-4d4a-4130-9164-8c782981483f";
// Đối tượng LogSession 1
        LogSession session1 = new LogSession("Morning Routine", "iPhone 15 Pro Max", scenarioId);
        List<Log> logsForSession1 = new ArrayList<>();
        logsForSession1.add(new Log(session1.getSession_id(), "Started morning routine", "No issues"));
        logsForSession1.add(new Log(session1.getSession_id(), "Checked emails", "No new emails"));
        logsForSession1.add(new Log(session1.getSession_id(), "Prepared breakfast", "Made coffee and toast"));
        logsForSession1.add(new Log(session1.getSession_id(), "Read news", "Read tech news"));
        logsForSession1.add(new Log(session1.getSession_id(), "Morning exercise", "Did 30 minutes of yoga"));
        logsForSession1.add(new Log(session1.getSession_id(), "Shower", "Quick shower"));
        logsForSession1.add(new Log(session1.getSession_id(), "Dressed up", "Casual wear"));
        logsForSession1.add(new Log(session1.getSession_id(), "Commute to work", "Took the bus"));
        logsForSession1.add(new Log(session1.getSession_id(), "Arrived at work", "On time"));
        logsForSession1.add(new Log(session1.getSession_id(), "Started work", "Checked schedule"));
        for (Log log : logsForSession1) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session1, logsForSession1, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
// Đối tượng LogSession 2
        LogSession session2 = new LogSession("Evening Check", "Samsung Galaxy S24 Ultra", scenarioId);
        List<Log> logsForSession2 = new ArrayList<>();
        logsForSession2.add(new Log(session2.getSession_id(), "Lunch break", "Ate at local cafe"));
        logsForSession2.add(new Log(session2.getSession_id(), "Team meeting", "Discussed project updates"));
        logsForSession2.add(new Log(session2.getSession_id(), "Client call", "Call with client ABC"));
        logsForSession2.add(new Log(session2.getSession_id(), "Code review", "Reviewed pull requests"));
        logsForSession2.add(new Log(session2.getSession_id(), "Bug fixing", "Fixed login issue"));
        logsForSession2.add(new Log(session2.getSession_id(), "Feature development", "Worked on new feature"));
        logsForSession2.add(new Log(session2.getSession_id(), "Testing", "Tested new feature"));
        logsForSession2.add(new Log(session2.getSession_id(), "Documentation", "Updated project docs"));
        logsForSession2.add(new Log(session2.getSession_id(), "Coffee break", "Quick coffee break"));
        logsForSession2.add(new Log(session2.getSession_id(), "Wrap up", "Summarized day's work"));
        for (Log log : logsForSession2) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session2, logsForSession2, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData3", "Success");
            }
        });
// Đối tượng LogSession 3
        LogSession session3 = new LogSession("Weekly Maintenance", "Google Pixel 9", scenarioId);
        List<Log> logsForSession3 = new ArrayList<>();
        logsForSession3.add(new Log(session3.getSession_id(), "Dinner preparation", "Cooked pasta"));
        logsForSession3.add(new Log(session3.getSession_id(), "Family time", "Watched a movie"));
        logsForSession3.add(new Log(session3.getSession_id(), "Reading", "Read a book"));
        logsForSession3.add(new Log(session3.getSession_id(), "Evening walk", "Walked in the park"));
        logsForSession3.add(new Log(session3.getSession_id(), "Phone call", "Called a friend"));
        logsForSession3.add(new Log(session3.getSession_id(), "Planning next day", "Made a to-do list"));
        logsForSession3.add(new Log(session3.getSession_id(), "Relaxation", "Listened to music"));
        logsForSession3.add(new Log(session3.getSession_id(), "Bedtime routine", "Brushed teeth"));
        logsForSession3.add(new Log(session3.getSession_id(), "Meditation", "10 minutes meditation"));
        logsForSession3.add(new Log(session3.getSession_id(), "Sleep", "Went to bed"));
        for (Log log : logsForSession3) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session3, logsForSession3, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData3", "Success");
            }
        });


    }


    public void addDataScenario2() {
        Random rd = new Random();
        String scenarioId = "932ae6e1-9a1c-4b69-accd-c47ad15f3d5f";
// Đối tượng LogSession 1

        LogSession session1 = new LogSession("Morning Routine", "iPhone 15 Pro Max", scenarioId);
        List<Log> logsForSession1 = new ArrayList<>();
        logsForSession1.add(new Log(session1.getSession_id(), "Started morning routine", "No issues"));
        logsForSession1.add(new Log(session1.getSession_id(), "Checked emails", "No new emails"));
        logsForSession1.add(new Log(session1.getSession_id(), "Prepared breakfast", "Made coffee and toast"));
        logsForSession1.add(new Log(session1.getSession_id(), "Read news", "Read tech news"));
        logsForSession1.add(new Log(session1.getSession_id(), "Morning exercise", "Did 30 minutes of yoga"));
        logsForSession1.add(new Log(session1.getSession_id(), "Shower", "Quick shower"));
        logsForSession1.add(new Log(session1.getSession_id(), "Dressed up", "Casual wear"));
        logsForSession1.add(new Log(session1.getSession_id(), "Commute to work", "Took the bus"));
        logsForSession1.add(new Log(session1.getSession_id(), "Arrived at work", "On time"));
        logsForSession1.add(new Log(session1.getSession_id(), "Started work", "Checked schedule"));
        for (Log log : logsForSession1) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session1, logsForSession1, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
// Đối tượng LogSession 2
        LogSession session2 = new LogSession("Evening Debugging", "Google Pixel 7 Pro", scenarioId);
        List<Log> logsForSession2 = new ArrayList<>();
        logsForSession2.add(new Log(session2.getSession_id(), "Started debugging session", "No issues"));
        logsForSession2.add(new Log(session2.getSession_id(), "Opened bug tracker", "Found 5 new bugs"));
        logsForSession2.add(new Log(session2.getSession_id(), "Fixed bug #101", "Resolved database connection issue"));
        logsForSession2.add(new Log(session2.getSession_id(), "Fixed bug #102", "Corrected UI alignment"));
        logsForSession2.add(new Log(session2.getSession_id(), "Tested fixes", "All tests passed"));
        logsForSession2.add(new Log(session2.getSession_id(), "Reviewed code changes", "Approved by team lead"));
        logsForSession2.add(new Log(session2.getSession_id(), "Updated documentation", "Added details to bug tracker"));
        logsForSession2.add(new Log(session2.getSession_id(), "Pushed fixes to repo", "Pushed to development branch"));
        logsForSession2.add(new Log(session2.getSession_id(), "Closed debugging session", "No issues"));

        for (Log log : logsForSession2) {
            log.setStatus(rd.nextBoolean());
        }

        _service.addLogSessionWithLogs(session2, logsForSession2, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData3", "Success");
            }
        });
// Đối tượng LogSession 3
        LogSession session3 = new LogSession("Afternoon Code Review", "OnePlus 11", scenarioId);
        List<Log> logsForSession3 = new ArrayList<>();
        logsForSession3.add(new Log(session3.getSession_id(), "Started code review session", "No issues"));
        logsForSession3.add(new Log(session3.getSession_id(), "Reviewed module A", "Found minor bugs"));
        logsForSession3.add(new Log(session3.getSession_id(), "Reviewed module B", "Code quality is good"));
        logsForSession3.add(new Log(session3.getSession_id(), "Discussed changes with team", "Agreed on improvements"));
        logsForSession3.add(new Log(session3.getSession_id(), "Updated code based on feedback", "Refactored functions"));
        logsForSession3.add(new Log(session3.getSession_id(), "Ran integration tests", "All tests passed"));
        logsForSession3.add(new Log(session3.getSession_id(), "Pushed reviewed code to repo", "Pushed to feature branch"));
        logsForSession3.add(new Log(session3.getSession_id(), "Documented review process", "Updated project wiki"));
        logsForSession3.add(new Log(session3.getSession_id(), "Ended code review session", "No issues"));

        for (Log log : logsForSession3) {
            log.setStatus(rd.nextBoolean());
        }

        _service.addLogSessionWithLogs(session3, logsForSession3, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData3", "Success");
            }
        });
    }


    public void addDataScenario3() {
        String scenarioId = "a11ce879-8645-4135-815d-0e0a5d9c139e";
        Random rd = new Random();

        // Session 1
        LogSession session1 = new LogSession("Coding Session", "Samsung Galaxy S23 Ultra", scenarioId);
        List<Log> logsForSession1 = new ArrayList<>();
        logsForSession1.add(new Log(session1.getSession_id(), "Started coding session", "No issues"));
        logsForSession1.add(new Log(session1.getSession_id(), "Opened IDE", "Loaded project successfully"));
        logsForSession1.add(new Log(session1.getSession_id(), "Reviewed pull requests", "Merged 2 PRs"));
        logsForSession1.add(new Log(session1.getSession_id(), "Implemented new feature", "Added user authentication"));
        logsForSession1.add(new Log(session1.getSession_id(), "Debugged code", "Fixed null pointer exception"));
        logsForSession1.add(new Log(session1.getSession_id(), "Ran unit tests", "All tests passed"));
        logsForSession1.add(new Log(session1.getSession_id(), "Refactored code", "Improved code readability"));
        logsForSession1.add(new Log(session1.getSession_id(), "Pushed changes to repo", "Pushed to main branch"));
        logsForSession1.add(new Log(session1.getSession_id(), "Documented changes", "Updated README.md"));
        logsForSession1.add(new Log(session1.getSession_id(), "Ended coding session", "No issues"));
        for (Log log : logsForSession1) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session1, logsForSession1, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
        // Session 2
        LogSession session2 = new LogSession("Evening Debugging", "Google Pixel 7 Pro", scenarioId);
        List<Log> logsForSession2 = new ArrayList<>();
        logsForSession2.add(new Log(session2.getSession_id(), "Started debugging session", "No issues"));
        logsForSession2.add(new Log(session2.getSession_id(), "Opened bug tracker", "Found 5 new bugs"));
        logsForSession2.add(new Log(session2.getSession_id(), "Fixed bug #101", "Resolved database connection issue"));
        logsForSession2.add(new Log(session2.getSession_id(), "Fixed bug #102", "Corrected UI alignment"));
        logsForSession2.add(new Log(session2.getSession_id(), "Tested fixes", "All tests passed"));
        logsForSession2.add(new Log(session2.getSession_id(), "Reviewed code changes", "Approved by team lead"));
        logsForSession2.add(new Log(session2.getSession_id(), "Updated documentation", "Added details to bug tracker"));
        logsForSession2.add(new Log(session2.getSession_id(), "Pushed fixes to repo", "Pushed to development branch"));
        logsForSession2.add(new Log(session2.getSession_id(), "Closed debugging session", "No issues"));
        for (Log log : logsForSession2) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session2, logsForSession2, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
        // Session 3
        LogSession session3 = new LogSession("Afternoon Code Review", "OnePlus 11", scenarioId);
        List<Log> logsForSession3 = new ArrayList<>();
        logsForSession3.add(new Log(session3.getSession_id(), "Started code review session", "No issues"));
        logsForSession3.add(new Log(session3.getSession_id(), "Reviewed module A", "Found minor bugs"));
        logsForSession3.add(new Log(session3.getSession_id(), "Reviewed module B", "Code quality is good"));
        logsForSession3.add(new Log(session3.getSession_id(), "Discussed changes with team", "Agreed on improvements"));
        logsForSession3.add(new Log(session3.getSession_id(), "Updated code based on feedback", "Refactored functions"));
        logsForSession3.add(new Log(session3.getSession_id(), "Ran integration tests", "All tests passed"));
        logsForSession3.add(new Log(session3.getSession_id(), "Pushed reviewed code to repo", "Pushed to feature branch"));
        logsForSession3.add(new Log(session3.getSession_id(), "Documented review process", "Updated project wiki"));
        logsForSession3.add(new Log(session3.getSession_id(), "Ended code review session", "No issues"));
        for (Log log : logsForSession3) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session3, logsForSession3, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
    }


    public void addDataScenario4() {
        String scenarioId = "d2a2ab0d-87a4-4da1-8fc1-96d4458ef89b";
        Random rd = new Random();

        // Session 4
        LogSession session4 = new LogSession("Morning Standup", "iPhone 14 Pro", scenarioId);
        List<Log> logsForSession4 = new ArrayList<>();
        logsForSession4.add(new Log(session4.getSession_id(), "Started standup meeting", "No issues"));
        logsForSession4.add(new Log(session4.getSession_id(), "Discussed project status", "On track"));
        logsForSession4.add(new Log(session4.getSession_id(), "Reviewed team progress", "Completed 80% of tasks"));
        logsForSession4.add(new Log(session4.getSession_id(), "Identified blockers", "No major blockers"));
        logsForSession4.add(new Log(session4.getSession_id(), "Assigned new tasks", "Distributed evenly"));
        logsForSession4.add(new Log(session4.getSession_id(), "Ended standup meeting", "No issues"));
        for (Log log : logsForSession4) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session4, logsForSession4, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
        // Session 5
        LogSession session5 = new LogSession("Afternoon Sprint Planning", "Huawei P50 Pro", scenarioId);
        List<Log> logsForSession5 = new ArrayList<>();
        logsForSession5.add(new Log(session5.getSession_id(), "Started sprint planning", "No issues"));
        logsForSession5.add(new Log(session5.getSession_id(), "Reviewed backlog", "Prioritized tasks"));
        logsForSession5.add(new Log(session5.getSession_id(), "Estimated task effort", "Used story points"));
        logsForSession5.add(new Log(session5.getSession_id(), "Assigned tasks to team", "Balanced workload"));
        logsForSession5.add(new Log(session5.getSession_id(), "Set sprint goals", "Defined clear objectives"));
        logsForSession5.add(new Log(session5.getSession_id(), "Ended sprint planning", "No issues"));
        for (Log log : logsForSession5) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session5, logsForSession5, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
        // Session 6
        LogSession session6 = new LogSession("Evening Retrospective", "Xiaomi Mi 11 Ultra", scenarioId);
        List<Log> logsForSession6 = new ArrayList<>();
        logsForSession6.add(new Log(session6.getSession_id(), "Started retrospective meeting", "No issues"));
        logsForSession6.add(new Log(session6.getSession_id(), "Reviewed sprint outcomes", "Achieved 90% of goals"));
        logsForSession6.add(new Log(session6.getSession_id(), "Discussed what went well", "Good team collaboration"));
        logsForSession6.add(new Log(session6.getSession_id(), "Identified areas for improvement", "Need better time management"));
        logsForSession6.add(new Log(session6.getSession_id(), "Created action items", "Assigned follow-up tasks"));
        logsForSession6.add(new Log(session6.getSession_id(), "Ended retrospective meeting", "No issues"));
        for (Log log : logsForSession6) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session6, logsForSession6, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
    }

    public void addDataScenario5() {
        Random rd = new Random();
        String scenarioId = "f5d0c555-6139-452f-a1ec-726dbea85fa7";
        // Session 7
        LogSession session7 = new LogSession("Late Night Bug Fixing", "Sony Xperia 1 IV", scenarioId);
        List<Log> logsForSession7 = new ArrayList<>();
        logsForSession7.add(new Log(session7.getSession_id(), "Started bug fixing session", "No issues"));
        logsForSession7.add(new Log(session7.getSession_id(), "Identified critical bug", "Memory leak detected"));
        logsForSession7.add(new Log(session7.getSession_id(), "Analyzed bug", "Found root cause"));
        logsForSession7.add(new Log(session7.getSession_id(), "Fixed memory leak", "Optimized memory usage"));
        logsForSession7.add(new Log(session7.getSession_id(), "Tested fix", "All tests passed"));
        logsForSession7.add(new Log(session7.getSession_id(), "Reviewed code changes", "Approved by peer"));
        logsForSession7.add(new Log(session7.getSession_id(), "Pushed fix to repo", "Pushed to hotfix branch"));
        logsForSession7.add(new Log(session7.getSession_id(), "Documented fix", "Updated bug tracker"));
        logsForSession7.add(new Log(session7.getSession_id(), "Ended bug fixing session", "No issues"));
        for (Log log : logsForSession7) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session7, logsForSession7, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
        // Session 8
        LogSession session8 = new LogSession("Weekend Hackathon", "Asus ROG Phone 6", scenarioId);
        List<Log> logsForSession8 = new ArrayList<>();
        logsForSession8.add(new Log(session8.getSession_id(), "Started hackathon", "No issues"));
        logsForSession8.add(new Log(session8.getSession_id(), "Brainstormed ideas", "Selected project theme"));
        logsForSession8.add(new Log(session8.getSession_id(), "Formed teams", "Assigned roles"));
        logsForSession8.add(new Log(session8.getSession_id(), "Developed prototype", "Built initial version"));
        logsForSession8.add(new Log(session8.getSession_id(), "Tested prototype", "Identified bugs"));
        logsForSession8.add(new Log(session8.getSession_id(), "Fixed bugs", "Resolved all issues"));
        logsForSession8.add(new Log(session8.getSession_id(), "Presented project", "Received positive feedback"));
        logsForSession8.add(new Log(session8.getSession_id(), "Ended hackathon", "No issues"));
        for (Log log : logsForSession8) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session8, logsForSession8, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
        // Session 9
        LogSession session9 = new LogSession("Midday Feature Development", "Oppo Find X5 Pro", scenarioId);
        List<Log> logsForSession9 = new ArrayList<>();
        logsForSession9.add(new Log(session9.getSession_id(), "Started feature development", "No issues"));
        logsForSession9.add(new Log(session9.getSession_id(), "Designed feature architecture", "Created UML diagrams"));
        logsForSession9.add(new Log(session9.getSession_id(), "Implemented feature", "Developed core functionality"));
        logsForSession9.add(new Log(session9.getSession_id(), "Integrated with existing system", "Ensured compatibility"));
        logsForSession9.add(new Log(session9.getSession_id(), "Conducted code review", "Received feedback"));
        logsForSession9.add(new Log(session9.getSession_id(), "Refactored code", "Improved performance"));
        logsForSession9.add(new Log(session9.getSession_id(), "Tested feature", "All tests passed"));
        logsForSession9.add(new Log(session9.getSession_id(), "Deployed feature", "Released to production"));
        logsForSession9.add(new Log(session9.getSession_id(), "Documented feature", "Updated user guide"));
        logsForSession9.add(new Log(session9.getSession_id(), "Ended feature development", "No issues"));
        for (Log log : logsForSession9) {
            log.setStatus(rd.nextBoolean());
        }
        _service.addLogSessionWithLogs(session9, logsForSession9, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                android.util.Log.e("AddData1", "Success");
            }
        });
    }

}
