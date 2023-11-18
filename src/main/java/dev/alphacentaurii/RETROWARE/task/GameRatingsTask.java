package dev.alphacentaurii.RETROWARE.task;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dev.alphacentaurii.RETROWARE.dao.GameDAO;
import dev.alphacentaurii.RETROWARE.dao.RatingCountDAO;
import dev.alphacentaurii.RETROWARE.model.Game;
import dev.alphacentaurii.RETROWARE.model.RatingCount;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableAsync
@EnableScheduling
@Slf4j
public class GameRatingsTask {
    
    private final int UPDATE_PAGE_SIZE = 20; //Represents how many games will be selected for update at a given moment in the update process
    private final double Z_SCORE = 1.96d;
    private final double Z_SCORE_SQ = Z_SCORE * Z_SCORE;
    private final int MINIMUM_RATINGS = 20;

    private final RatingCountDAO ratingCountDAO;
    private final GameDAO gameDAO;

    private Timestamp lastExecuted;

    @Autowired
    public GameRatingsTask(RatingCountDAO ratingCount, GameDAO gameDAO){
        this.ratingCountDAO = ratingCount;
        this.gameDAO = gameDAO;
        lastExecuted = Timestamp.valueOf("1980-01-01 00:00:00");
    }


    @Async("asyncTaskExecutor")
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void updateRatings(){
       
        int last_fetched_index = 0;
        int current_result_size = 0;

        Timestamp start = Timestamp.valueOf(LocalDateTime.now());
        log.debug("Counting changes in rating...");
        
        List<Game> current_games = new ArrayList<Game>(UPDATE_PAGE_SIZE);

        //Update ratings
        do{
            List<RatingCount> current_ratings = ratingCountDAO.getRatingCountByRowBounds(last_fetched_index, UPDATE_PAGE_SIZE);
            current_result_size = current_ratings.size();
            
            current_games.clear();

            for(RatingCount rating: current_ratings){
                // Count user ratings and fetch
                RatingCount delta_rating = ratingCountDAO.getDeltaRatingCountForGame(rating.getGame_id(), lastExecuted);

                // Update the current rating record
                rating.add(delta_rating);

                int total = rating.getLikes() + rating.getDislikes();

                short final_rating = 0;

                if(total == 0)
                    final_rating = 0;
                else if(total < MINIMUM_RATINGS)
                    final_rating = (short)(ciLowerBound(rating.getLikes() + MINIMUM_RATINGS - total, rating.getDislikes()) * 100);
                else
                    final_rating = (short)(ciLowerBound(rating.getLikes(), rating.getDislikes()) * 100);

                Game g = new Game();
                g.setGame_id(rating.getGame_id());
                g.setRating(final_rating);

                current_games.add(g);
            }
     
            // Save changes in rating counts in the database
            ratingCountDAO.updateRatingCountForGame(current_ratings); //Not updated with propper values

            // Update last processed rating for each rating affected
            ratingCountDAO.updateUserRatingsLastProcessed(current_ratings);

            //Update the games entry with the results
            gameDAO.updateGameRating(current_games);

            last_fetched_index += UPDATE_PAGE_SIZE;
        }while(current_result_size == UPDATE_PAGE_SIZE);

        lastExecuted.setTime(start.getTime());

        Timestamp end = Timestamp.valueOf(LocalDateTime.now());
        long elapsed = end.getTime() - start.getTime();    
        log.debug("Finished updating game rating counts in: " + elapsed/1000d + " seconds.");
    }

    // Lower bound of Wilson score confidence interval for a Bernoulli parameter
    private double ciLowerBound(int positive, int negative){
        int total = positive + negative;
        
        double ph = (double)positive/total;
     
        return ( ph + Z_SCORE_SQ/(2*total) - Z_SCORE * Math.sqrt((ph*(1-ph)+Z_SCORE_SQ/(4*total))/total) ) / (1+Z_SCORE_SQ/total);
    }
    

}//End of class
