package dev.alphacentaurii.RETROWARE.dao;

import java.util.List;

public interface GameListsDAO {
    
    public List<Integer> getTopMostPlayedGames(int top);
    public List<Integer> getTopBestRatedGames(int top);

    public void setMostPlayedGames(List<Integer> game_ids);
    public void setBestRatedGames(List<Integer> game_ids);

}//End of class
