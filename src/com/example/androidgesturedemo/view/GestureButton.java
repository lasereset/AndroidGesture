package com.example.androidgesturedemo.view;

import com.dianming.common.Util;
import com.dianming.support.Fusion;
import com.example.androidgesturedemo.R;
import com.example.androidgesturedemo.entity.Circle;
import com.example.androidgesturedemo.entity.RectMain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

public class GestureButton extends Button{
    
    private static final int CIRCLE_NORMAL = 1;// normal state of circle
    private static final int CIRCLE_SELECTED = 2;// selected state of circle
    
    private int mColor;
    private String mText;
    private int mTextSize;
    
    private int width;
    private int height;
    
    private Bitmap circletBmp;// used for drawing circles
    private Canvas mCanvas;
    private Paint cirNorPaint;// paint of normal state circles
    private Paint smallCirSelPaint;// paint of selected state small circles
    private Paint bigCirSelPaint;
    private Paint textPaint;
    private int strokeWidth = 2;// width of big circles;
    private Shader shader_normal;
    private Shader shader_select;
    private Shader shader_big_select;
    
    private int normalR = 20;// radius of small circles;
    private int selectR = 30;
    
    private GestureSelectedListener gestureListener;// the listener of creating gesture
    
    private Circle circle;
    
    private boolean hasSelected;
    private boolean isSelected;

    public GestureButton(Context context) {
        super(context);
    }

    public GestureButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.GestureButton);

        // 解析集合中的属性circle_color属性
        // 该属性的id为:R.styleable.CircleView_circle_color
        // 将解析的属性传入到画圆的画笔颜色变量当中（本质上是自定义画圆画笔的颜色）
        // 第二个参数是默认设置颜色（即无指定circle_color情况下使用）
        mColor = a.getColor(R.styleable.GestureButton_center_text_color,Color.BLACK);
        mText = a.getString(R.styleable.GestureButton_center_text);
        mTextSize = a.getDimensionPixelOffset(R.styleable.GestureButton_center_text_size, GestureView.dip2px(context, 30));
        normalR = a.getDimensionPixelOffset(R.styleable.GestureButton_circle_normal_color_size, GestureView.dip2px(context, 20));
        selectR = a.getDimensionPixelOffset(R.styleable.GestureView_circle_normal_color_size, GestureView.dip2px(context, 30));
        shader_normal = new LinearGradient(0,0,40,60,new int[] {Color.parseColor("#A65B3A"),Color.parseColor("#DEB3A1")},null,Shader.TileMode.REPEAT);
        shader_select = new LinearGradient(0,0,40,60,new int[] {Color.parseColor("#554B95"),Color.parseColor("#9088C3")},null,Shader.TileMode.REPEAT);
        shader_big_select = new LinearGradient(0,0,40,60,new int[] {Color.parseColor("#DCEEF4"),Color.parseColor("#9088C3")},null,Shader.TileMode.REPEAT);
        // 解析后释放资源
        a.recycle();
        
        strokeWidth = GestureView.dip2px(context, strokeWidth);
    }
    
    

    public GestureButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    /**
     * reset all states
     */
    private void resetAll() {
        if (circle != null) {
            circle.setState(CIRCLE_NORMAL);
        }
        clearCanvas();
    }
    
    /**
     * clear canvas
     */
    private void clearCanvas() {
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }
    
    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Util.Log("TAG1", "Enter into this onLayout");
        // 普通状态小圆画笔
        circletBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(circletBmp);
        cirNorPaint = new Paint();
        cirNorPaint.setAntiAlias(true);
        cirNorPaint.setDither(true);
        cirNorPaint.setShader(shader_normal);
        // 文字画笔
        textPaint = new Paint();
        textPaint.setColor(mColor);
        textPaint.setTextSize(mTextSize);
        //选中状态大圆画笔
        bigCirSelPaint = new Paint();
        bigCirSelPaint.setAntiAlias(true);
        bigCirSelPaint.setDither(true);
        bigCirSelPaint.setShader(shader_big_select);
        // 选中状态小圆画笔
        smallCirSelPaint = new Paint();
        smallCirSelPaint.setAntiAlias(true);
        smallCirSelPaint.setDither(true);
        smallCirSelPaint.setShader(shader_select);

        // init all circles
        int tempX = width / 2;
        int tempY = height / 2;
        RectMain rectMain = new RectMain(0, width, 0, height);
        circle = new Circle(0, tempX, tempY, CIRCLE_NORMAL, rectMain, null);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(circletBmp, 0, 0, null);
        float tWidth = textPaint.measureText(mText);
        canvas.drawText(mText, circle.getX() - tWidth / 2, circle.getY() + tWidth / 2, textPaint);
        drawCircles(circle);
    }
    
    private void drawCircles(Circle circle) {
        switch (circle.getState()) {
            case CIRCLE_NORMAL:
                mCanvas.drawCircle(circle.getX(), circle.getY(), normalR, cirNorPaint);
                break;
            case CIRCLE_SELECTED:
            	mCanvas.drawCircle(circle.getX(), circle.getY(), selectR, bigCirSelPaint);
                mCanvas.drawCircle(circle.getX(), circle.getY(), normalR, smallCirSelPaint);
                break;
        }
    }
    
    @Override
    public boolean onHoverEvent(MotionEvent event) {
        return onTouchEvent(event);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TAG", "Enter into this onTouchEvent2 and this code is:" + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
            case MotionEvent.ACTION_DOWN:
                if (circle != null) {
                    isSelected = true;
                    hasSelected = true;
                    circle.setState(CIRCLE_SELECTED);
                    Fusion.syncTTS(mText);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
            case MotionEvent.ACTION_UP:
                isSelected = false;
                if (gestureListener != null) {
                    gestureListener.onGestureSelected(this);;
                }
                resetAll();
                invalidate();
                break;
        }
        return true;
    }
    
    /**
     * Create Mode Listener
     */
    public interface GestureSelectedListener {
        void onGestureSelected(GestureButton view);
    }

    public void setGestureListener(GestureSelectedListener gestureListener) {
        this.gestureListener = gestureListener;
    }

    public boolean isHasSelected() {
        return hasSelected;
    }

    public void setHasSelected(boolean hasSelected) {
        this.hasSelected = hasSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
