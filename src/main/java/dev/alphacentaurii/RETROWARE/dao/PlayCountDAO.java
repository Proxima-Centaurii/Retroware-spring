package dev.alphacentaurii.RETROWARE.dao;

import java.sql.Timestamp;
import java.util.List;

import dev.alphacentaurii.RETROWARE.model.PlayCount;

public interface PlayCountDAO {

    public void insertUniqueEntry(String client_address, int game_id);
    public List<PlayCount> countPlays(Timestamp threshold);
    public void updateProcessedEntries(Timestamp processed_on);
    public void deleteExpiredEntries(String timeUnit, int amount);

}//End of class
