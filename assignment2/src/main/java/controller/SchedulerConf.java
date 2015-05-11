package controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by aravind on 4/17/15.
 */

@Component
public class SchedulerConf {

    @Autowired
    ModeratorController serviceObj;

   @Scheduled(fixedRate = 30000)
    public void messageScheduler()
    {
        System.out.println("Scheduler Started");
        serviceObj.checkForExpiredPolls();
    }
}
