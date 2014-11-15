package com.namelessproject.iknowthatsong;

/**
 * Created by Lucas on 15/11/2014.
 */
public class Score implements Comparable<Score> {
    private String scoreDate;
    public int scoreNum;

    @Override
    public int compareTo(Score score) {
        return score.scoreNum>scoreNum? 1 : score.scoreNum<scoreNum? -1 : 0;
    }

    public Score(String date, int num){
        scoreDate=date;
        scoreNum=num;
    }

    public String getScoreText()
    {
        return scoreDate+" - "+scoreNum;
    }
}
