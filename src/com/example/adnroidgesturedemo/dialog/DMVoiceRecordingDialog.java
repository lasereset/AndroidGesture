package com.example.adnroidgesturedemo.dialog;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dianming.support.Log;
import com.example.androidgesturedemo.R;
import com.example.androidgesturedemo.tool.EffectHelper;
import com.example.androidgesturedemo.tool.ToolUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by husiolois on 16-8-16.
 */

public class DMVoiceRecordingDialog{

    ImageView micImage;
    LinearLayout recordingContainer;
    
    private OnResultListence resultListence;
    private SpeechRecognizer mAsr = null;

    private Context context;
    
    public DMVoiceRecordingDialog(Context context, OnResultListence resultListence, ImageView micImage, LinearLayout recordingContainer) {
        this.context = context;
        this.resultListence = resultListence;
        this.micImage = micImage;
        this.recordingContainer = recordingContainer;
        init(context);
    }
    
    private void init(Context context) {
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=5a98c73a");
        mAsr = SpeechRecognizer.createRecognizer(context, srInitListener);
    }
    
    public void release() {
        if (mAsr != null) {
            mAsr.cancel();
            mAsr.destroy();
            mAsr = null;
        }
    }
    
    /**
     * 初始化监听器（命令词）。
     */
    private InitListener srInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Log.d("TAG", "ERROR IS:" + "命令词初始化失败,错误码：" + code);
//                UtilLog.d("命令词初始化失败,错误码：" + code);
            } else {
                mAsr.setParameter(SpeechConstant.PARAMS, null);
                // 设置编码
                mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
                // 设置返回类型
                mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");
                // 设置录音格式
                mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
                // 设置听写引擎
                mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
                // 设置返回结果格式
                mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");
                // 设置语言
                mAsr.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                // 设置语音前端点
                mAsr.setParameter(SpeechConstant.VAD_BOS, "5000");
                // 设置语音后端点
                mAsr.setParameter(SpeechConstant.VAD_EOS, "1800");
                // 设置标点符号，默认：1（有标点）
                mAsr.setParameter(SpeechConstant.ASR_PTT, "1");
                // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
                // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
                mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
                mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/gestureInputmethod/sud.wav");
            }
        }
    };
    
    public void beginVoice() {
        if (getAvailaleSize() < 10) {
            if (resultListence != null) {
                resultListence.error(context.getString(R.string.media_no_memory));
            }
            return;
        }

        if (!ToolUtils.isExistExternalStore()) {
            if (resultListence != null) {
                resultListence.error(context.getString(R.string.media_ejected));
            }
            return;
        }

        mVoiceInputtedText.setLength(0);
        speechUnderstander(new RecognizerListener() {
            @Override
            public void onVolumeChanged(int i, byte[] bytes) {
                displayAmplitude(i * 10);
            }

            @Override
            public void onBeginOfSpeech() {
                EffectHelper.getInstance().playTip();
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (null != recognizerResult) {
                    String text = parseIatResult(recognizerResult.getResultString());
                    mVoiceInputtedText.append(text);
                }
                if (isLast && resultListence != null) {
                    if (mVoiceInputtedText.length() > 0) {
                        resultListence.getResultString(mVoiceInputtedText.toString().substring(0, mVoiceInputtedText.length() - 1));
                    } else {
                        resultListence.error("您好像没有说话哦");
                    }
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                if (resultListence != null) {
                    resultListence.error(speechError.getErrorDescription());
                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {
            }
        });
    }
    
    public void speechUnderstander(RecognizerListener recognizerListener) {
        // 开始前检查状态
        if (mAsr.isListening()) {
            mAsr.stopListening();
        }

        // 开始Listening
        int ret = mAsr.startListening(recognizerListener);
        if (ret != 0) {
        }
    }

    /**
     * @return
     */
    public long getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize) / 1024 / 1024;// MIB单位
    }

    public String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    private StringBuilder mVoiceInputtedText = new StringBuilder();
    
    public void setResultListence(OnResultListence resultListence) {
        this.resultListence = resultListence;
    }

    public interface OnResultListence{
        void getResultString(String resultText);
        void error(String error);
    }

    private static final int ampValue[] = {
            0, 7, 14, 21, 28, 35, 42, 49, 56, 64, 70, 77, 84, 91, 100
    };
    private static final int ampIcon[] = {
            R.drawable.record_animate_01, R.drawable.record_animate_02, R.drawable.record_animate_03, R.drawable.record_animate_04, R.drawable.record_animate_05, R.drawable.record_animate_06, R.drawable.record_animate_07,
            R.drawable.record_animate_08, R.drawable.record_animate_09, R.drawable.record_animate_10, R.drawable.record_animate_11, R.drawable.record_animate_12, R.drawable.record_animate_13, R.drawable.record_animate_14,
    };

    public void displayAmplitude(double amplitude) {
        for (int i = 0; i < ampIcon.length; i++) {
            if (amplitude < ampValue[i] || amplitude >= ampValue[i + 1]) {
                continue;
            }
            micImage.setImageResource(ampIcon[i]);
            if ((amplitude == -1) && recordingContainer != null) {
                recordingContainer.setVisibility(View.GONE);
            }
            return;
        }
    }

}
