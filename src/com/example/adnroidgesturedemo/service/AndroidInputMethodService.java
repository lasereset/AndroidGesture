package com.example.adnroidgesturedemo.service;

import java.util.List;

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
import com.example.androidgesturedemo.view.GestureButtonView;
import com.example.androidgesturedemo.view.GestureButtonView.CreateGestureButtonViewListener;
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

public class AndroidInputMethodService extends InputMethodService implements CreateGestureListener, OnClickListener, OnResultListence, OnHoverListener, CreateGestureButtonViewListener {

    private static final int FIRST_MODEL = 100;
    private static final int SECOND_MODEL = 101;
    GestureView gestureView;
    GestureView gestureView2;
    TextView showFinalResult;
    TextView changeModel;
    
    GestureButtonView gestureButtonView1;
    GestureButtonView gestureButtonView2;

    LinearLayout firstModelView;
    LinearLayout secondModelView;
    LinearLayout voiceView;
    ImageView voiceImage;

    Button confirmButton;
    Button backButton;
    Button voiceInputButton;

    private String gestureViewResult;
    private List<String> selectGestureButtonViews;

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
        if (Util.isSignedByDMCertificate(this, getPackageName()) && Util.isSignedByDMCertificate(this, "com.dianming.phoneapp")) {
            /* bind Speak Service */
            Intent intent = new Intent("com.dianming.phoneapp.SpeakServiceForApp");
            intent.setPackage("com.dianming.phoneapp");
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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
        gestureButtonView1 = (GestureButtonView) view.findViewById(R.id.gestureViewButton_1);
        gestureButtonView2 = (GestureButtonView) view.findViewById(R.id.gestureViewButton_2);
        confirmButton = (Button) view.findViewById(R.id.confrimButton);
        backButton = (Button) view.findViewById(R.id.backButton);
        voiceImage = (ImageView) view.findViewById(R.id.mic_image);
        voiceInputButton = (Button) view.findViewById(R.id.voiceInputButton);

        gestureView.setGestureListener(this);
        gestureView2.setGestureListener(this);
        gestureButtonView1.setGestureListener(this);
        gestureButtonView2.setGestureListener(this);
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
        getFianlResult();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            getFianlResult();
            return true;
        }
    });

    private boolean getFianlResult() {
    	if (!isFinished()) {
    		return false;
    	}
        StringBuffer stringBuffer = new StringBuffer();
        if (!Fusion.isEmpty(selectGestureButtonViews)) {
        	if (selectGestureButtonViews.contains("3")) {
        		stringBuffer.append("30");
        	} 
        	if (selectGestureButtonViews.contains("6")) {
        		stringBuffer.append("60");
        	}
        }
        if (!Fusion.isEmpty(gestureViewResult)) {
            stringBuffer.append(gestureViewResult);
        }
        gestureViewResult = null;
        try {
        	Log.d("TAG", "Enter into this reportCurrentContent is:" + stringBuffer.toString());
            IRResponse.DMExplain dmExplain = IRResponse.all_dm_hashMap.get(Integer.valueOf(stringBuffer.toString()));
            getCurrentInputConnection().commitText(dmExplain.getUnicode(), 1);
            Fusion.syncForceTTS(dmExplain.getSpeakDes());
        } catch (Exception e) {
            e.printStackTrace();
            Fusion.syncTTS("未检测到结果！");
        }
        return true;
    }

    @Override
    public void getResultString(String resultText) {
        DMExplain dmExplain = IRResponse.getDMExplainBySpeak(resultText);
        if (dmExplain != null) {
            getCurrentInputConnection().commitText(dmExplain.getUnicode(), 1);
            Fusion.syncForceTTS(dmExplain.getSpeakDes());
        } else {
            Fusion.syncTTS(resultText + "未找到对应的结果！");
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
    
    private boolean isFinished() {
    	return !gestureView.isUnlocking() && !gestureView2.isUnlocking() && !gestureButtonView1.isUnlocking() && !gestureButtonView2.isUnlocking();
    }

	@Override
	public void onGestureButtonViewCreated(List<String> selectViews) {
		if (Fusion.isEmpty(selectViews)) {
			boolean isCancle = getFianlResult();
			if (selectGestureButtonViews != null && isCancle) {
				selectGestureButtonViews.clear();
	            selectGestureButtonViews = null;
			}
			return;
		}
		this.selectGestureButtonViews = selectViews;
	}
}
