package dev.alphacentaurii.RETROWARE.enums;

public enum SearchSortCriteria {
    
    HIGHEST_RATING("RATING DESC", "Highest rating"),
    LOWEST_RATING("RATING ASC", "Lowest rating"),
    MOST_PLAYED("PLAY_COUNT DESC", "Most played"),
    LEAST_PLAYED("PLAY_COUNT ASC", "Least played");

    public final String SQL;
    public final String display_name;

    SearchSortCriteria(String SQL, String display_name){
        this.SQL = SQL;
        this.display_name = display_name;
    }
}
