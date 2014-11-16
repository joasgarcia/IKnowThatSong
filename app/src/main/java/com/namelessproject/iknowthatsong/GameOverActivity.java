package com.namelessproject.iknowthatsong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.namelessproject.iknowthatsong.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GameOverActivity extends ActionBarActivity {

    private SharedPreferences gamePrefs;
    public static final String GAME_PREFS = "ScoreFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        gamePrefs = getSharedPreferences(GAME_PREFS, MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String score = extras.getString("SCORE");
            TextView scoreLabel = (TextView)findViewById(R.id.scoreLabel);

            scoreLabel.setText(score);
        }

        Button btn_scores = (Button)findViewById(R.id.saveHighScoreButton);
        btn_scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHighScore();
                Intent i = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(i);
            }
        });

        Button viewScores = (Button)findViewById(R.id.viewMain);
        viewScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHighScore();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        verifyHighScore();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_over, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setHighScore(){
        TextView scoreLabel = (TextView)findViewById(R.id.scoreLabel);
        EditText nameInput = (EditText)findViewById(R.id.nameInput);
        int score = Integer.parseInt(scoreLabel.getText().toString());

        if(score>0){
            SharedPreferences.Editor scoreEdit = gamePrefs.edit();
            String playerName = nameInput.getText().toString();
            String listOfScores = gamePrefs.getString("highScores", "");

            if(listOfScores.length()>0){

                List<Score> scoreStrings = new ArrayList<Score>();
                String[] exScores = listOfScores.split("\\|");

                for(String eSc : exScores){
                    String[] parts = eSc.split(" - ");
                    scoreStrings.add(new Score(parts[0], Integer.parseInt(parts[1])));
                }

                if(playerName.length()==0){
                    playerName = "Player1";
                }

                Score newScore = new Score(playerName, score);
                scoreStrings.add(newScore);

                Collections.sort(scoreStrings);

                StringBuilder scoreBuild = new StringBuilder("");
                for(int s=0; s<scoreStrings.size(); s++){
                    if(s>=10) break;//only want ten
                    if(s>0) scoreBuild.append("|");//pipe separate the score strings
                    scoreBuild.append(scoreStrings.get(s).getScoreText());
                }
                //write to prefs
                scoreEdit.putString("highScores", scoreBuild.toString());
                scoreEdit.commit();
            }
            else{
                scoreEdit.putString("highScores", nameInput.getText().toString() + " - " + score);
                scoreEdit.commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    // Before 2.0
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void verifyHighScore(){
        TextView scoreTopLabel = (TextView)findViewById(R.id.scoreTopLabel);
        TextView scoreLabel = (TextView)findViewById(R.id.scoreLabel);
        int score = Integer.parseInt(scoreLabel.getText().toString());

        if(score>0){
            String listOfScores = gamePrefs.getString("highScores", "");

            if(listOfScores.length()>0){

                List<Score> scoreStrings = new ArrayList<Score>();
                String[] exScores = listOfScores.split("\\|");

                for(String eSc : exScores){
                    String[] parts = eSc.split(" - ");
                    scoreStrings.add(new Score(parts[0], Integer.parseInt(parts[1])));
                }

                Collections.sort(scoreStrings);

                if(score > scoreStrings.get(0).getScoreNum()){
                    scoreTopLabel.setText("NOVO RECORDE! =)");
                    scoreLabel.setTextSize(25);
                }
                else{
                    scoreTopLabel.setText("Você Fez: ");
                }
            }
        }
        else{
            scoreTopLabel.setText("NOVO RECORDE! =)");
            scoreLabel.setTextSize(25);
        }
    }
}
