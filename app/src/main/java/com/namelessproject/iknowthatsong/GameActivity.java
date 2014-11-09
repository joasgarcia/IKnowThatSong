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
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

public class GameActivity extends ActionBarActivity {
    /*Commit Hello World*/
    MediaPlayer mp;
    ArrayList songs;
    Timer timerAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SongsManager songsManager = new SongsManager();

        songs =  songsManager.getPlayList();

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

        newGuess();
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

    private void newGuess() {
        if(!songs.isEmpty()){

           /* Button btnGuess = (Button)findViewById(R.id.btn_guess);
            btnGuess.setEnabled(false);*/

            if(mp != null && mp.isPlaying()){
                mp.stop();
                mp.release();
            }

            String isUsed = "" ;
            int buttonKey;
            int count = songs.size();
            boolean continueLoop = true;

            Random random = new Random();

            int song = random.nextInt(count);

            HashMap hash = (HashMap)songs.get(song);
            audioPlayer(hash.get("songPath").toString());

            while(continueLoop) {
                buttonKey = random.nextInt(4);
                if(!isUsed.contains(String.valueOf(buttonKey))){
                    createButton(buttonKey, hash.get("songTitle").toString(), true);
                    isUsed += "|" + String.valueOf(buttonKey);
                    continueLoop = false;
                }
            }


            song = random.nextInt(count);
            hash = (HashMap)songs.get(song);

            continueLoop = true;
            while(continueLoop) {
                buttonKey = random.nextInt(4);
                if (!isUsed.contains(String.valueOf(buttonKey))) {
                    createButton(buttonKey, hash.get("songTitle").toString(), false);
                    isUsed += "|" + String.valueOf(buttonKey);
                    continueLoop = false;
                }
            }

            song = random.nextInt(count);
            hash = (HashMap)songs.get(song);

            continueLoop = true;
            while(continueLoop) {
                buttonKey = random.nextInt(4);
                if (!isUsed.contains(String.valueOf(buttonKey))) {
                    createButton(buttonKey, hash.get("songTitle").toString(), false);
                    isUsed += "|" + String.valueOf(buttonKey);
                    continueLoop = false;
                }
            }

            song = random.nextInt(count);
            hash = (HashMap)songs.get(song);

            continueLoop = true;
            while(continueLoop) {
                buttonKey = random.nextInt(4);
                if (!isUsed.contains(String.valueOf(buttonKey))) {
                    createButton(buttonKey, hash.get("songTitle").toString(), false);
                    isUsed += "|" + String.valueOf(buttonKey);
                    continueLoop = false;
                }
            }
            //btnGuess.setEnabled(true);
        }
    }

    private void createButton(int btn_number, String songTitle, boolean isRight) {
        Button btn_answer;
        switch (btn_number) {
            case 0:
                btn_answer = (Button) findViewById(R.id.btn_0);
                break;
            case 1:
                btn_answer = (Button) findViewById(R.id.btn_1);
                break;
            case 2:
                btn_answer = (Button) findViewById(R.id.btn_2);
                break;
            case 3:
                btn_answer = (Button) findViewById(R.id.btn_3);
                break;
            default:
                btn_answer = (Button) findViewById(R.id.btn_0);
                songTitle = "Fucking Error!";
                break;
        }


        if (songTitle != "") {

            btn_answer.setVisibility(View.VISIBLE);
            btn_answer.setEnabled(true);

            btn_answer.setText(songTitle);

            if (isRight) {
                btn_answer.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        rightAnswer();
                    }
                });
            } else {
                btn_answer.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });
            }
        }else{
            timerAnimation.cancel();
            mp.stop();
            btn_answer.setEnabled(false);
            btn_answer.setVisibility(View.INVISIBLE);
        }
    }

    private void rightAnswer() {
        TextView lbl = (TextView)findViewById(R.id.lbl_result);

        lbl.setText("Acertooo! ->");

        lbl = (TextView)findViewById(R.id.lbl_rightAnswer);

        lbl.setText(String.valueOf(Integer.parseInt(lbl.getText().toString()) + 1));

        newGuess();
    }

    private void wrongAnswer() {
        TextView lbl = (TextView)findViewById(R.id.lbl_result);

        lbl.setText("<- Errooooo!");

        lbl = (TextView)findViewById(R.id.lbl_wrongAnswer);

        lbl.setText(String.valueOf(Integer.parseInt(lbl.getText().toString()) + 1));

        MediaPlayer erroAudio;
        erroAudio = MediaPlayer.create(this, R.raw.erro);
        erroAudio.start();
        erroAudio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        mp.stop();
        SystemClock.sleep(1000);

        newGuess();
    }

    public void audioPlayer(String path){
        //set up MediaPlayer

        if(mp != null){
            mp.release();
        }
        mp = new MediaPlayer();

        try {
            Random rand = new Random();

            mp.setDataSource(path);
            mp.prepare();
            //in miliseconds
            mp.seekTo((int)(mp.getDuration()*(rand.nextInt(90)/100.0)));

            //mp.prepareAsync();

            mp.start();

        } catch (Exception e) {
            newGuess();
        }
    }
}
