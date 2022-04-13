package com.example.musicplayer;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    TextView textView;
    ImageView play,previous,next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        textView = findViewById(R.id.textView2);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar2);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("song list");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position",0);

        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(PlaySong.this,uri);
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run(){
                int currentPosition = 0;

                try{
                    while(currentPosition< mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if(position!=songs.size()-1){
                    position = position +1;
                }else{
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(PlaySong.this,uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textView.setText(songs.get(position).getName());
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();
                mediaPlayer.release();

                if(position!=0){
                    position = position - 1;
                }else{
                    position = songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(PlaySong.this,uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textView.setText(songs.get(position).getName());
            }
        });
    }
}