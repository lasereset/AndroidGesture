package com.example.adnroidgesturedemo.service;

import com.dianming.common.SessionManager;
import com.dianming.common.Util;
import com.dianming.phoneapp.ISpeakService;
import com.dianming.support.Fusion;
import com.example.adnroidgesturedemo.dialog.DMVoiceRecordingDialog;
import com.example.adnroidgesturedemo.dialog.DMVoiceRecordingDialog.OnResultListence;
import com.example.androidgesturedemo.R;
import com.example.androidgesturedemo.tool.EffectHelper;
import com.example.androidgesturedemo.tool.IRResponse;
import com.example.androidgesturedemo.tool.IRResponse.DMExplain;
import com.example.androidgesturedemo.tool.ToolUtils;
import com.example.androidgesturedemo.view.GestureButton;
import com.example.androidgesturedemo.view.GestureButton.GestureSelectedListener;
import com.example.androidgesturedemo.view.GestureView;
import com.example.androidgesturedemo.view.GestureView.CreateGestureListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AndroidInputMethodService extends InputMethodService implements CreateGestureListener, GestureSelectedListener, OnClickListener, OnResultListence, OnHoverListener {

    private static final int FIRST_MODEL = 100;
    private static final int SECOND_MODEL = 101;
    GestureView gestureView;
    GestureView gestureView2;
    TextView showFinalResult;
    TextView changeModel;

    LinearLayout firstModelView;
    LinearLayout secondModelView;
    LinearLayout voiceView;
    ImageView voiceImage;

    GestureButton threeButton1;
    GestureButton threeButton2;
    GestureButton sixButton1;
    GestureButton sixButton2;

    Button confirmButton;
    Button backButton;
    Button voiceInputButton;

    private String gestureViewResult;

    private int current_model = FIRST_MODEL;

    private DMVoiceRecordingDialog dialog;
    
    public static ISpeakService mService = null;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            SessionManager.getInstance().setSpeakService(mService, Util.currentContext, mConnection);
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            mService = ISpeakService.Stub.asInterface(service);
            SessionManager.getInstance().setSpeakService(mService, Util.currentContext, mConnection);
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        EffectHelper.getInstance().init(AndroidInputMethodService.this);;
    }

    @Override
    public View onCreateInputView() {
        if (!ToolUtils.isAccessibilityEnabled(this) || ToolUtils.isAccessibilityServiceEnabled(this, ToolUtils.DM_CURRENT_ACCESSIBILITY_SERVICE)) {
            if (Util.isSignedByDMCertificate(this, getPackageName()) && Util.isSignedByDMCertificate(this, "com.dianming.phoneapp")) {
                /* bind Speak Service */
                Intent intent = new Intent("com.dianming.phoneapp.SpeakServiceForApp");
                intent.setPackage("com.dianming.phoneapp");
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        }

        // 装载keyboard.xml文件
        View view = getLayoutInflater().inflate(R.layout.activity_main, null);
        // 设置布局中5个按钮的单击事件
        showFinalResult = (TextView) view.findViewById(R.id.finalResult);
        changeModel = (TextView) view.findViewById(R.id.changeModel);
        firstModelView = (LinearLayout) view.findViewById(R.id.firstLinear);
        secondModelView = (LinearLayout) view.findViewById(R.id.secondLinear);
        voiceView = (LinearLayout) view.findViewById(R.id.voiceLinear);
        gestureView = (GestureView) view.findViewById(R.id.gestureView);
        gestureView2 = (GestureView) view.findViewById(R.id.gestureView_2);
        threeButton1 = (GestureButton) view.findViewById(R.id.gesture_three);
        threeButton2 = (GestureButton) view.findViewById(R.id.gesture_three_2);
        sixButton1 = (GestureButton) view.findViewById(R.id.gesture_six);
        sixButton2 = (GestureButton) view.findViewById(R.id.gesture_six_2);
        confirmButton = (Button) view.findViewById(R.id.confrimButton);
        backButton = (Button) view.findViewById(R.id.backButton);
        voiceImage = (ImageView) view.findViewById(R.id.mic_image);
        voiceInputButton = (Button) view.findViewById(R.id.voiceInputButton);

        gestureView.setGestureListener(this);
        gestureView2.setGestureListener(this);
        threeButton1.setGestureListener(this);
        threeButton2.setGestureListener(this);
        sixButton1.setGestureListener(this);
        sixButton2.setGestureListener(this);
        confirmButton.setOnClickListener(this);
        confirmButton.setOnHoverListener(this);
        backButton.setOnClickListener(this);
        backButton.setOnHoverListener(this);
        voiceInputButton.setOnClickListener(this);
        voiceInputButton.setOnHoverListener(this);
        showFinalResult.setOnClickListener(this);
        changeModel.setOnClickListener(this);
        
        // 返回View对象
        return view;
    }
    
    /**
     * 打开关闭点明读屏
     * @param isClose
     */
    private void openOrCloseDMService(boolean isClose) {
        Intent newIntent = new Intent();
        newIntent.setPackage("com.dianming.phoneapp");
        newIntent.setAction("com.dianming.phoneapp.SpeakServiceForApp");
        newIntent.putExtra("TouchExplorationMode", isClose ? 0x80 : 0x81); //0x81
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(newIntent);
    }
    
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        openOrCloseDMService(true);
        super.onStartInputView(info, restarting);
    }
    
    @Override
    public void onFinishInputView(boolean finishingInput) {
        openOrCloseDMService(false);
        super.onFinishInputView(finishingInput);
    }
    
    public void reportCurrentContent() {
        InputConnection connection = getCurrentInputConnection();
        if (connection != null) {
            ExtractedText extracted = connection.getExtractedText(
                    new ExtractedTextRequest(), 0);
            if (extracted != null) {
                String content = extracted.text.toString();
                if (Fusion.isEmpty(content)) {
                    Fusion.syncTTS("当前还没有输入任何内容！");return;
                }
                String finalResult = IRResponse.getDMExplainByUnicode(content);
                Log.d("TAG", "Enter into this reportCurrentContent is:" + finalResult);
                Fusion.syncTTS(finalResult);
            }
        }
    }
    
    @Override
    public void onClick(View v) {
        if (v instanceof Button) {
            Button button = (Button) v;
            Fusion.syncTTS(button.getText().toString());
        }
        if (v.getId() == R.id.confrimButton) {
            getCurrentInputConnection().commitText(IRResponse.all_dm_hashMap.get(0).getUnicode(), 1);
        } else if (v.getId() == R.id.backButton) {
            getCurrentInputConnection().deleteSurroundingText(1, 0);
        } else if (v.getId() == R.id.voiceInputButton) {
            showVoiceInput();
        } else if (v.getId() == R.id.changeModel) {
            firstModelView.setVisibility(current_model == FIRST_MODEL ? View.GONE : View.VISIBLE);
            secondModelView.setVisibility(current_model == FIRST_MODEL ? View.VISIBLE : View.GONE);
            Fusion.syncTTS(current_model == FIRST_MODEL ? "模式二" : "模式一");
            changeModel.setText(current_model == FIRST_MODEL ? "模式二" : "模式一");
            current_model = current_model == FIRST_MODEL ? SECOND_MODEL : FIRST_MODEL;
        } else if (v.getId() == R.id.finalResult) {
            reportCurrentContent();
        }
    }

    private void showVoiceInput() {
        if (voiceView.isShown()) {
            cancleDialog();
            return;
        }
        showDialog();
        if (dialog == null) {
            dialog = new DMVoiceRecordingDialog(this, this, voiceImage, voiceView);
            SessionManager.getInstance().speakNowToEndWithRunnable("请在听到提示音之后开始讲话", new Runnable() {
                @Override
                public void run() {
                    dialog.beginVoice();
                }
            });
        } else {
            dialog.beginVoice();
        }
    }

    private void cancleDialog() {
        firstModelView.setVisibility(current_model == FIRST_MODEL ? View.VISIBLE : View.GONE);
        secondModelView.setVisibility(current_model == FIRST_MODEL ? View.GONE : View.VISIBLE);
        voiceView.setVisibility(View.GONE);
    }

    private void showDialog() {
        firstModelView.setVisibility(View.GONE);
        secondModelView.setVisibility(View.GONE);
        voiceView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGestureCreated(String result) {
        this.gestureViewResult = result;
        if (!buttonHasSelected()) {
            getFianlResult();
        }
    }

    private boolean otherNoPress(GestureButton view) {
        if (gestureView.isUnlocking() || gestureView2.isUnlocking())
            return false;
        if (firstModelView.isShown()) {
            if (view.getId() == threeButton1.getId() && !sixButton1.isSelected()) {
                return true;
            }
            if (view.getId() == sixButton1.getId() && !threeButton1.isSelected()) {
                return true;
            }
        } else {
            if (view.getId() == threeButton2.getId() && !sixButton2.isSelected()) {
                return true;
            }
            if (view.getId() == sixButton2.getId() && !threeButton2.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private boolean buttonHasSelected() {
        return threeButton1.isSelected() || threeButton2.isSelected() || sixButton1.isSelected() || sixButton2.isSelected();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            getFianlResult();
            return true;
        }
    });

    @Override
    public void onGestureSelected(GestureButton view) {
        if (otherNoPress(view)) {
            handler.sendEmptyMessageAtTime(0, 100);
        }
    }

    private void getFianlResult() {
        StringBuffer stringBuffer = new StringBuffer();
        if (firstModelView.isShown()) {
            if (threeButton1.isHasSelected()) {
                threeButton1.setHasSelected(false);
                stringBuffer.append("30");
            }
            if (sixButton1.isHasSelected()) {
                sixButton1.setHasSelected(false);
                stringBuffer.append("60");
            }
        } else {
            if (threeButton2.isHasSelected()) {
                threeButton2.setHasSelected(false);
                stringBuffer.append("30");
            }
            if (sixButton2.isHasSelected()) {
                sixButton2.setHasSelected(false);
                stringBuffer.append("60");
            }
        }
        if (!Fusion.isEmpty(gestureViewResult)) {
            stringBuffer.append(gestureViewResult);
        }
        gestureViewResult = null;
        try {
            IRResponse.DMExplain dmExplain = IRResponse.all_dm_hashMap.get(Integer.valueOf(stringBuffer.toString()));
            getCurrentInputConnection().commitText(dmExplain.getUnicode(), 1);
            Fusion.syncForceTTS(dmExplain.getSpeakDes());
        } catch (Exception e) {
            e.printStackTrace();
            Fusion.syncForceTTS("未检测到结果！");
        }
    }

    @Override
    public void getResultString(String resultText) {
        DMExplain dmExplain = IRResponse.getDMExplainBySpeak(resultText);
        if (dmExplain != null) {
            getCurrentInputConnection().commitText(dmExplain.getUnicode(), 1);
            Fusion.syncForceTTS(dmExplain.getSpeakDes());
        } else {
            Fusion.syncForceTTS(resultText + "未找到对应的结果！");
        }
        cancleDialog();
    }

    @Override
    public void error(String error) {
        ToolUtils.syncToast(AndroidInputMethodService.this, error);
        cancleDialog();
    }
    
    @Override
    public void onDestroy() {
        if (dialog != null) {
            dialog.release();
            dialog = null;
        }
        gestureViewResult = null;
        current_model = FIRST_MODEL;
        super.onDestroy();
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
                if (v instanceof Button) {
                    Button button = (Button) v;
                    Fusion.syncTTS(button.getText().toString() + "按钮");
                }
                break;
        }
        return true;
    }
}
