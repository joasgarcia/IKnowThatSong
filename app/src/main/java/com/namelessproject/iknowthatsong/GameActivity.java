package com.namelessproject.iknowthatsong;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class GameActivity extends ActionBarActivity {
    MediaPlayer mp;
    ArrayList<HashMap<String, String>> listOfCurrentSongs = new ArrayList<HashMap<String, String>>();
    public AppSession appSession;
    TextView timerLabel;
    int hitSequence = 0;
    CountDownTimer counter;
    int timeLeft;
    //(qtdeAcertos*500) + ((10/tempoResposta)*300) -> inteiro

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        appSession = (AppSession) getApplicationContext();
        setContentView(R.layout.activity_game);

        createTimer(60000);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy()
    {
        if(mp != null && mp.isPlaying()){
            counter.cancel();
            mp.stop();
            mp.release();
        }
        super.onDestroy();
        //Mostra uma msg na tela.. pode ser bom saber..
        //Toast.makeText(getApplicationContext(),"16. onDestroy()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause()
    {
        if(mp != null && mp.isPlaying()){
            TextView timer = (TextView)findViewById(R.id.txt_timer);
            timeLeft = Integer.parseInt(timer.getText().toString());
            counter.cancel();
            mp.stop();
        }
        super.onPause();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(mp != null && !mp.isPlaying()){
            try {
                if(timeLeft>1) {
                    createTimer(timeLeft * 1000);
                }
                mp.prepare();
                mp.start();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createTimer(int miliseconds){
        timerLabel = (TextView)findViewById(R.id.txt_timer);

        counter = new CountDownTimer(miliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                timerLabel.setText(new SimpleDateFormat("ss").format(new Date(millisUntilFinished)));
            }

            public void onFinish() {
                TextView rightAnswerCounter = (TextView)findViewById(R.id.lbl_rightAnswer);
                Intent i = new Intent(getApplicationContext(), GameOverActivity.class);
                i.putExtra("SCORE", rightAnswerCounter.getText());
                startActivity(i);
            }
        }.start();
    }

    private void newAttempt() {
        if(!appSession.getListOfSong().isEmpty()){
            if(mp != null && mp.isPlaying()){
                mp.stop();
                mp.release();
            }

            listOfCurrentSongs = new ArrayList<HashMap<String, String>>();

            ArrayList<Integer> listOfButtonKey = new ArrayList<Integer>(4);
            listOfButtonKey.add(0);
            listOfButtonKey.add(1);
            listOfButtonKey.add(2);
            listOfButtonKey.add(3);
            Collections.shuffle(listOfButtonKey);
            
            Boolean isRightAnswer;

            for(Integer i = 0; i < listOfButtonKey.size(); i++) {
                HashMap<String, String> song = getRandomSong();
                this.listOfCurrentSongs.add(song);

                isRightAnswer = i.equals(0);

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
                songTitle = "Error 400004";
                break;
        }

        if (songTitle != "") {
            buttonAnswer.setVisibility(View.VISIBLE);
            buttonAnswer.setEnabled(true);

            buttonAnswer.setText(songTitle);

            if (isRight) {
                buttonAnswer.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setButtonEnabled(false);
                        rightAnswer();
                        newAttempt();
                        setButtonEnabled(true);
                    }
                });
            } else {
                buttonAnswer.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setButtonEnabled(false);
                        wrongAnswer();
                        newAttempt();
                        setButtonEnabled(true);
                    }
                });
            }
        } else{
            //timer.cancel();
            mp.stop();
            buttonAnswer.setEnabled(false);
            buttonAnswer.setVisibility(View.INVISIBLE);
        }
    }

    private void rightAnswer() {
        TextView resultLabel = (TextView)findViewById(R.id.lbl_result);
        resultLabel.setText("Acertou! ->");

        TextView rightAnswerCounter = (TextView)findViewById(R.id.lbl_rightAnswer);

        int points = Integer.parseInt(rightAnswerCounter.getText().toString());
        hitSequence += 1;
        points += 2*hitSequence;

        rightAnswerCounter.setText(String.valueOf(points));
    }

    private void wrongAnswer() {
        TextView resultLabel = (TextView)findViewById(R.id.lbl_result);
        resultLabel.setText("<- Errou!");

        hitSequence = 0;

        TextView wrongAnswerCounter = (TextView)findViewById(R.id.lbl_wrongAnswer);
        wrongAnswerCounter.setText(String.valueOf(Integer.parseInt(wrongAnswerCounter.getText().toString()) + 1));

        playErrorAudio();
        mp.stop();
        SystemClock.sleep(1000);
    }

    private void setButtonEnabled(Boolean enabled) {
        Button btn_0, btn_1, btn_2, btn_3;

        btn_0 = (Button) findViewById(R.id.btn_0);
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_3 = (Button) findViewById(R.id.btn_3);

        btn_0.setEnabled(enabled);
        btn_1.setEnabled(enabled);
        btn_2.setEnabled(enabled);
        btn_3.setEnabled(enabled);
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

    private HashMap<String, String> getRandomSong() {
        //TODO Corrigir bug de trazer duas músicas iguais na
        // mesma tentativa. Implementar uma lista que guarde as opções de músicas já sorteadas para aquela tentativa
        Boolean exists = true;
        HashMap<String, String> selectedSong = new HashMap<String, String>();
        Integer count = 0;
        while(exists) {
            Integer randomSongIndex = new Random().nextInt(appSession.getListOfSong().size());
            selectedSong = appSession.getListOfSong().get(randomSongIndex);
            count++;

            if(!this.listOfCurrentSongs.contains(selectedSong)) {
                exists = false;
            }
        }

        return selectedSong;
    }
}
