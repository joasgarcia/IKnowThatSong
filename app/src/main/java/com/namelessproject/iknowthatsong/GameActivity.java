package com.namelessproject.iknowthatsong;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

public class GameActivity extends ActionBarActivity {

    MediaPlayer mp;
    ArrayList listOfSong;
    Timer timerAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SongsManager songsManager = new SongsManager();
        listOfSong = songsManager.getPlayList();

        /*timerAnimation = new Timer();
        timerAnimation.schedule(new TimerTask() {
            @Override
            public void run() {
                createButton(0, "", false);
                createButton(1, "", false);
                createButton(2, "", false);
                createButton(3, "", false);
            }
        }, 10000, 0);*/

        newAttempt();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
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

    private void newAttempt() {
        if(!listOfSong.isEmpty()){
            if(mp != null && mp.isPlaying()){
                mp.stop();
                mp.release();
            }

            ArrayList<Integer> listOfButtonKey = new ArrayList<Integer>(4);
            listOfButtonKey.add(0);
            listOfButtonKey.add(1);
            listOfButtonKey.add(2);
            listOfButtonKey.add(3);
            Collections.shuffle(listOfButtonKey);
            
            Boolean isRightAnswer;

            for(Integer i = 0; i < listOfButtonKey.size(); i++) {
                HashMap<String, String> song = getRandomSong();

                isRightAnswer = i.equals(0) ? true : false;

                if(isRightAnswer) {
                    playSong(song.get("songPath"));
                }

                createButton(listOfButtonKey.get(i), song.get("songTitle"), isRightAnswer);
            }
        }
    }

    private void createButton(int buttonNumber, String songTitle, Boolean isRight) {
        Button buttonAnswer;
        switch (buttonNumber) {
            case 0:
                buttonAnswer = (Button) findViewById(R.id.btn_0);
                break;
            case 1:
                buttonAnswer = (Button) findViewById(R.id.btn_1);
                break;
            case 2:
                buttonAnswer = (Button) findViewById(R.id.btn_2);
                break;
            case 3:
                buttonAnswer = (Button) findViewById(R.id.btn_3);
                break;
            default:
                buttonAnswer = (Button) findViewById(R.id.btn_0);
                songTitle = "Error 4000004";
                break;
        }

        if (songTitle != "") {
            buttonAnswer.setVisibility(View.VISIBLE);
            buttonAnswer.setEnabled(true);

            buttonAnswer.setText(songTitle);

            if (isRight) {
                buttonAnswer.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        rightAnswer();
                        newAttempt();
                    }
                });
            } else {
                buttonAnswer.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        wrongAnswer();
                        newAttempt();
                    }
                });
            }
        } else{
            timerAnimation.cancel();
            mp.stop();
            buttonAnswer.setEnabled(false);
            buttonAnswer.setVisibility(View.INVISIBLE);
        }
    }

    private void rightAnswer() {
        TextView resultLabel = (TextView)findViewById(R.id.lbl_result);
        resultLabel.setText("Acertou! ->");

        TextView rightAnswerCounter = (TextView)findViewById(R.id.lbl_rightAnswer);
        rightAnswerCounter.setText(String.valueOf(Integer.parseInt(rightAnswerCounter.getText().toString()) + 1));
    }

    private void wrongAnswer() {
        TextView resultLabel = (TextView)findViewById(R.id.lbl_result);
        resultLabel.setText("<- Errou!");

        TextView wrongAnswerCounter = (TextView)findViewById(R.id.lbl_wrongAnswer);
        wrongAnswerCounter.setText(String.valueOf(Integer.parseInt(wrongAnswerCounter.getText().toString()) + 1));

        playErrorAudio();
        mp.stop();
        SystemClock.sleep(1000);
    }

    private void playErrorAudio() {
        MediaPlayer errorAudio;
        errorAudio = MediaPlayer.create(this, R.raw.erro);
        errorAudio.start();
        errorAudio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    public void playSong(String path){
        if(this.mp != null){
            this.mp.release();
        }

        this.mp = new MediaPlayer();

        try {
            Random rand = new Random();

            this.mp.setDataSource(path);
            this.mp.prepare();
            this.mp.seekTo((int)(mp.getDuration()*(rand.nextInt(90)/100.0)));
            this.mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap getRandomSong() {
        //TODO Corrigir bug de trazer duas músicas iguais na
        // mesma tentativa. Implementar uma lista que guarde as opções de músicas já sorteadas para aquela tentativa

        Integer randomSongIndex = new Random().nextInt(this.listOfSong.size());
        return (HashMap) listOfSong.get(randomSongIndex);
    }
}
