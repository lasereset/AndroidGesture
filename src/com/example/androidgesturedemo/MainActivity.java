package com.example.androidgesturedemo;

import com.dianming.common.SessionManager;
import com.dianming.common.Util;
import com.dianming.phoneapp.ISpeakService;
import com.dianming.support.Fusion;
import com.example.androidgesturedemo.tool.IRResponse;
import com.example.androidgesturedemo.view.GestureButton;
import com.example.androidgesturedemo.view.GestureButton.GestureSelectedListener;
import com.example.androidgesturedemo.view.GestureView;
import com.example.androidgesturedemo.view.GestureView.CreateGestureListener;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, CreateGestureListener, GestureSelectedListener {

    GestureView gestureView;
    GestureView gestureView2;
    TextView showFinalResult;

    LinearLayout firstModelView;
    LinearLayout secondModelView;

    GestureButton threeButton1;
    GestureButton threeButton2;
    GestureButton sixButton1;
    GestureButton sixButton2;

    Button confirmButton;
    Button backButton;

    private String gestureViewResult;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if(Util.isSignedByDMCertificate(MainActivity.this, getPackageName()) &&
        // Util.isSignedByDMCertificate(MainActivity.this, "com.dianming.phoneapp")){
        // /* bind Speak Service */
        // bindService(new Intent("com.dianming.phoneapp.SpeakServiceForApp"), mConnection, Context.BIND_AUTO_CREATE);
        // }

        showFinalResult = (TextView) findViewById(R.id.finalResult);
        firstModelView = (LinearLayout) findViewById(R.id.firstLinear);
        secondModelView = (LinearLayout) findViewById(R.id.secondLinear);
        gestureView = (GestureView) findViewById(R.id.gestureView);
        gestureView2 = (GestureView) findViewById(R.id.gestureView_2);
        threeButton1 = (GestureButton) findViewById(R.id.gesture_three);
        threeButton2 = (GestureButton) findViewById(R.id.gesture_three_2);
        sixButton1 = (GestureButton) findViewById(R.id.gesture_six);
        sixButton2 = (GestureButton) findViewById(R.id.gesture_six_2);
        confirmButton = (Button) findViewById(R.id.confrimButton);

        gestureView.setGestureListener(this);
        gestureView2.setGestureListener(this);
        threeButton1.setGestureListener(this);
        threeButton2.setGestureListener(this);
        sixButton1.setGestureListener(this);
        sixButton2.setGestureListener(this);
        confirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // if (v.getId() == R.id.changeModel) {
        //// firstModelView.setVisibility(firstModelView.isShown() ? View.GONE : View.VISIBLE);
        //// secondModelView.setVisibility(secondModelView.isShown() ? View.GONE : View.VISIBLE);
        //// changeMobileTextView.setText(firstModelView.isShown() ? "模式一" : "模式二");
        // } else
        if (v.getId() == R.id.confrimButton) {
            // fillTextView.setText(fillTextView.getText().toString() + " " + IRResponse.all_dm_hashMap.get(0).getUnicode());
        }
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
            showFinalResult.setTextSize(10);
            // showFinalResult.setText(String.valueOf(dmExplain.getCode()));
//            fillTextView.setText(fillTextView.getText().toString() + " " + dmExplain.getUnicode());
            showFinalResult.setText(dmExplain.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
            showFinalResult.setText("未检测到结果！");
        }
        Fusion.syncTTS(showFinalResult.getText().toString());
    }
}
