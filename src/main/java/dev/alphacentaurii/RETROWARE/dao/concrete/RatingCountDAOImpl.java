package dev.alphacentaurii.RETROWARE.dao.concrete;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dev.alphacentaurii.RETROWARE.dao.RatingCountDAO;
import dev.alphacentaurii.RETROWARE.model.RatingCount;

@Repository
public class RatingCountDAOImpl implements RatingCountDAO{

    private final JdbcTemplate jdbcTemplate;

    private final String COUNT_GAME_RATINGS = """
            SELECT COUNT(CASE WHEN RATING = 1 THEN 1 END)  - COUNT(CASE WHEN LAST_PROCESSED_RATING = 1 THEN 1 END) AS \"DELTA_LIKES\",
            COUNT(CASE WHEN RATING = -1 THEN 1 END) - COUNT(CASE WHEN LAST_PROCESSED_RATING = -1 THEN 1 END) AS \"DELTA_DISLIKES\",
            FROM GAME_RATINGS WHERE GAME_ID = ? AND RATED_ON >= ?
            """;

    private final String UPDATE_GAME_RATING_COUNT = "UPDATE RATINGS_COUNT SET LIKES = ?, DISLIKES = ? WHERE GAME_ID = ?";
    private final String UPDATE_LAST_PROCESSED_RATING = "UPDATE GAME_RATINGS SET LAST_PROCESSED_RATING = RATING WHERE GAME_ID = ?";

    private final String GET_GAME_RATING_COUNT = "SELECT * FROM RATINGS_COUNT WHERE GAME_ID = ?";
    private final String GET_RATING_COUNT_ROWS = "SELECT * FROM RATINGS_COUNT OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";


    @Autowired
    public RatingCountDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RatingCount getDeltaRatingCountForGame(Integer game_id, Timestamp threshold) {
        return jdbcTemplate.queryForObject(COUNT_GAME_RATINGS, RatingCount::rowMapDelta, game_id, threshold);
    }

    @Override
    public void updateRatingCountForGame(final List<RatingCount> delta_ratings) {
        jdbcTemplate.batchUpdate(UPDATE_GAME_RATING_COUNT,
                                new BatchPreparedStatementSetter() {

                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        RatingCount rating = delta_ratings.get(i);
                                        ps.setInt(1, rating.getLikes());
                                        ps.setInt(2, rating.getDislikes());
                                        ps.setInt(3, rating.getGame_id());
                                    }

                                    @Override
                                    public int getBatchSize() {
                                        return delta_ratings.size();
                                    }
                                    
                                });
    }

    @Override
    public void updateUserRatingsLastProcessed(final List<RatingCount> ratings){
        jdbcTemplate.batchUpdate(UPDATE_LAST_PROCESSED_RATING,
                                new BatchPreparedStatementSetter() {

                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        Integer game_id = ratings.get(i).getGame_id();
                                        ps.setInt(1, game_id);
                                    }

                                    @Override
                                    public int getBatchSize() {
                                        return ratings.size();
                                    }
                                    
                                });
    }

    @Override
    public RatingCount getCurrentRatingCountForGame(Integer game_id) {
        return jdbcTemplate.queryForObject(GET_GAME_RATING_COUNT, RatingCount::rowMap, game_id);
    }

    @Override
    public List<RatingCount> getRatingCountByRowBounds(Integer offset, Integer size) {
        return jdbcTemplate.query(GET_RATING_COUNT_ROWS, RatingCount::rowMap, offset, size);
    }
    
}//End of class
