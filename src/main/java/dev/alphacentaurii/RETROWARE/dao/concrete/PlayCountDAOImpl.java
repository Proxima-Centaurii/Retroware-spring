package dev.alphacentaurii.RETROWARE.dao.concrete;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dev.alphacentaurii.RETROWARE.dao.PlayCountDAO;
import dev.alphacentaurii.RETROWARE.model.PlayCount;

@Repository
public class PlayCountDAOImpl implements PlayCountDAO{
    
    private final JdbcTemplate jdbcTemplate;

    private final String INSERT_NEW_ENTRY = 
    """
    INSERT INTO GAME_PLAY_LOG (CLIENT, GAME_ID) 
    SELECT ?, ? WHERE NOT EXISTS (SELECT CLIENT, GAME_ID FROM GAME_PLAY_LOG WHERE CLIENT = ? and game_id = ?)
    """;

    private final String COUNT_PLAYS = "SELECT GAME_ID, COUNT(GAME_ID) AS \"COUNT\" FROM GAME_PLAY_LOG  WHERE PLAYED_ON < ? AND PROCESSED = FALSE GROUP BY GAME_ID";
    private final String UPDATE_PROCESSED_ENTRIES = "UPDATE GAME_PLAY_LOG SET PROCESSED = TRUE WHERE PLAYED_ON < ?";

    private final String DELETE_EXPIRED_ENTRIES = "DELETE FROM GAME_PLAY_LOG WHERE DATEDIFF(%s, PLAYED_ON, CURRENT_TIMESTAMP(0)) > ?";

    public PlayCountDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertUniqueEntry(String client, int game_id) {
        jdbcTemplate.update(INSERT_NEW_ENTRY, client, game_id, client, game_id);
    }

    @Override
    public List<PlayCount> countPlays(Timestamp threshold) {
        return jdbcTemplate.query(COUNT_PLAYS, PlayCount::rowMap, threshold);
    }

    @Override
    public void updateProcessedEntries(Timestamp processed_on) {
        jdbcTemplate.update(UPDATE_PROCESSED_ENTRIES, processed_on);
    }

    /**
     * Deletes from the database all logged play counts that are older than the time amount passed to this function.
     * @param timeUnit Do not confuse this with Java's 'TimeUnit' class. This is a string that will be
     *  inserted in an SQL query and can have values such as: YEAR, MONTH, DAY, HOUR, MINUTE, SECOND etc
     * @param amount The amount of time units specified
     */
    @Override
    public void deleteExpiredEntries(String timeUnit, int amount) {
        jdbcTemplate.execute(String.format(DELETE_EXPIRED_ENTRIES, timeUnit, amount));
    }



}//End of class
