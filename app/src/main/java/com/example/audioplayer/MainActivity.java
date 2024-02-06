package com.example.audioplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Button btnPlayPause;
    private SeekBar volumeSeekBar;
    private Spinner spinnerTracks;
    private TextView remainingTime;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlayPause = findViewById(R.id.btnPlayPause);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        spinnerTracks = findViewById(R.id.spinnerTracks);
        remainingTime = findViewById(R.id.remainingTime);

        mediaPlayer = new MediaPlayer();

        // Здесь вы указываете имена файлов в папке raw
        final int[] audioFiles = {R.raw.deutschland, R.raw.sonne, R.raw.lesnik, R.raw.kukla, R.raw.notch};

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = (float) progress / 100;
                mediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Настройка Spinner
        List<String> trackNames = new ArrayList<>();
        for (int i = 0; i < audioFiles.length; i++) {
            trackNames.add("Трек " + (i + 1));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trackNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTracks.setAdapter(adapter);

        spinnerTracks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // При выборе трека из Spinner запускаем воспроизведение выбранного трека
                String[] audioFilesPaths = getResources().getStringArray(R.array.Songs);

                // Извлекаем имя файла из пути
                String selectedFileName = audioFilesPaths[position].substring(audioFilesPaths[position].lastIndexOf('/') + 1);

                // Отображаем имя файла как название трека
                ((TextView) findViewById(R.id.selectedTrackName)).setText(selectedFileName);

                // Запускаем воспроизведение выбранного трека
                playAudio(audioFiles[position]);
                btnPlayPause.setText("Pause");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Обработка события отсутствия выбора
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Воспроизведение завершено, можно выполнить дополнительные действия
                btnPlayPause.setText("Play");
            }
        });



        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer.isPlaying()) {
                                    int currentPosition = mediaPlayer.getCurrentPosition();

                                    // Проверяем, что getDuration() возвращает корректное значение
                                    if (mediaPlayer.getDuration() > 0) {
                                        int remainingSeconds = (mediaPlayer.getDuration() - currentPosition) / 1000;
                                        int minutes = remainingSeconds / 60;
                                        int seconds = remainingSeconds % 60;
                                        remainingTime.setText(String.format("%d:%02d", minutes, seconds));
                                    }
                                }
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();



    }

    public void onPlayPauseClick(View view) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayPause.setText("Play");
        } else {
            mediaPlayer.start();
            btnPlayPause.setText("Pause");
        }
    }

    private void playAudio(int resourceId) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getResources().openRawResourceFd(resourceId));
            mediaPlayer.prepare();
            mediaPlayer.start();
            btnPlayPause.setText("Pause");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}