package dev.alphacentaurii.RETROWARE.task;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dev.alphacentaurii.RETROWARE.dao.GameListsDAO;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableAsync
@EnableScheduling
@Slf4j
public class GameListsTask {
    
    private final GameListsDAO gameLists;

    public GameListsTask(GameListsDAO gameLists){
        this.gameLists = gameLists;
    }

    @Async("asyncTaskExecutor")
    // @Scheduled(initialDelay = 2 , fixedDelay = 2 , timeUnit =  TimeUnit.MINUTES)
    @Scheduled(cron = "0 0 0 * * SUN")
    public void updateMostPlayedGames(){
        List<Integer> most_played = gameLists.getTopMostPlayedGames(3);
        gameLists.setMostPlayedGames(most_played);

        log.info("Updated popular games list.");
    }

    @Async("asyncTaskExecutor")
    // @Scheduled(initialDelay = 2 , fixedDelay = 2 , timeUnit =  TimeUnit.MINUTES)
    @Scheduled(cron = "0 0 0 * * SUN")
    public void updateBestRatedGames(){
        List<Integer> best_rated = gameLists.getTopBestRatedGames(3);
        gameLists.setBestRatedGames(best_rated);

        log.info("Updated featured games list.");
    }

}
