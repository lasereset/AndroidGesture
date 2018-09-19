package com.example.androidgesturedemo.tool;

import com.example.androidgesturedemo.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class EffectHelper {
    private SoundPool pool;
    private int play_id;

    private static EffectHelper instance;

    public static EffectHelper getInstance() {
        if (instance == null) {
            instance = new EffectHelper();

        }
        return instance;
    }
    
    public void init(Context context) {
        pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        play_id = pool.load(context, R.raw.start_tone, 1);
    }

    public void playTip() {
        pool.play(play_id, 1, 1, 0, 0, 1);
    }
}
