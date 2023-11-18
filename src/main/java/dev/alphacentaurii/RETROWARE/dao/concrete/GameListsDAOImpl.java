package dev.alphacentaurii.RETROWARE.dao.concrete;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dev.alphacentaurii.RETROWARE.dao.GameListsDAO;

@Repository
public class GameListsDAOImpl implements GameListsDAO{

    private final JdbcTemplate jdbcTemplate;

    private final String GET_TOP_MOST_PLAYED = "SELECT TOP ? GAME_ID FROM GAMES ORDER BY PLAY_COUNT DESC";
    private final String GET_TOP_BEST_RATED = "SELECT TOP ? GAME_ID FROM GAMES ORDER BY RATING_SCORE DESC";

    private final String INSERT_MOST_PLAYED_GAMES = "INSERT INTO POPULAR_GAMES_LIST (GAME_ID) VALUES ?";
    private final String INSERT_BEST_RATED_GAMES = "INSERT INTO FEATURED_GAMES_LIST (GAME_ID) VALUES ?";

    @Autowired
    public GameListsDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Integer> getTopMostPlayedGames(int top) {
        return jdbcTemplate.query(GET_TOP_MOST_PLAYED, (rs, rowNum)->{return rs.getInt(1);}, top);
    }

    @Override
    public List<Integer> getTopBestRatedGames(int top) {
        return jdbcTemplate.query(GET_TOP_BEST_RATED, (rs, rowNum)->{return rs.getInt(1);}, top);
    }

    @Override
    public void setMostPlayedGames(List<Integer> game_ids) {
        jdbcTemplate.update("DELETE FROM POPULAR_GAMES_LIST");

        jdbcTemplate.batchUpdate(INSERT_MOST_PLAYED_GAMES, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, game_ids.get(i));
            }

            @Override
            public int getBatchSize() {
                return game_ids.size();
            } 
        });
    }

    @Override
    public void setBestRatedGames(List<Integer> game_ids) {
        jdbcTemplate.update("DELETE FROM FEATURED_GAMES_LIST");
        
        jdbcTemplate.batchUpdate(INSERT_BEST_RATED_GAMES, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, game_ids.get(i));
            }

            @Override
            public int getBatchSize() {
                return game_ids.size();
            }
        });
    }
    
}
