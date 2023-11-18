package dev.alphacentaurii.RETROWARE.task;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dev.alphacentaurii.RETROWARE.dao.GameDAO;
import dev.alphacentaurii.RETROWARE.dao.PlayCountDAO;
import dev.alphacentaurii.RETROWARE.model.PlayCount;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableAsync
@EnableScheduling
@Slf4j
public class GamePlayCountTask {
    
    private final PlayCountDAO playCountDAO;
    private final GameDAO gameDAO;

    public GamePlayCountTask(PlayCountDAO playCountDAO, GameDAO gameDAO){
        this.playCountDAO = playCountDAO;
        this.gameDAO = gameDAO;
    }

    @Async("asyncTaskExecutor")
    @Scheduled(initialDelay = 10, fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void updatePlayCounts(){

        
        Timestamp start = Timestamp.valueOf(LocalDateTime.now());

        List<PlayCount> delta_counts = playCountDAO.countPlays(start);
        
        // Update the play count for each game
        gameDAO.updateDeltaGamePlayCount(delta_counts);

        // Mark counted entries as processed
        playCountDAO.updateProcessedEntries(start);

        Timestamp end = Timestamp.valueOf(LocalDateTime.now());
        long elapsed = end.getTime() - start.getTime();    
        log.info("Finished updating game play counts in: " + elapsed/1000d + " seconds.");
    }

    @Async("asyncTaskExecutor")
    @Scheduled(initialDelay = 2, fixedDelay = 2, timeUnit = TimeUnit.HOURS)
    public void deleteExpiredLogEntries(){
        playCountDAO.deleteExpiredEntries("HOUR", 2);
        log.debug("Deleted expired entries from game play log.");
    }

}//End of class
