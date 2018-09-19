package com.example.androidgesturedemo.view;

import java.util.ArrayList;

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
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Wulei on 2017/7/27.
 */

public class GestureView extends View {
    public static final int CIRCLE_NORMAL = 1;// normal state of circle
    public static final int CIRCLE_SELECTED = 2;// selected state of circle
    private int width;// the width of screen,valued in onMeasure
    private int height;// the height of screen,valued in onMeasure
    private int rootX;// root position of the line which can move
    private int rootY;// root position of the line which can move
    private Context ctx;
    private ArrayList<Circle> circleList = new ArrayList<Circle>();// store the circles on screen
    private ArrayList<RectMain> rectMains = new ArrayList<RectMain>();
    private Bitmap circletBmp;// used for drawing circles
    private Canvas mCanvas;
    private Paint cirNorPaint;// paint of normal state circles
    private Paint rectSelPaint;// paint of selected state circles
    private Paint smallCirSelPaint;// paint of selected state small circles
    private Paint pathPaint;// paint of the lines
    private Paint textPaint;
    private Path mPath;
    private Path tempPath;
    private int pathWidth = 3;// width of the paint of path
    private int normalR = 20;// radius of small circles;
    private int strokeWidth = 2;// width of big circles;
    private int normalColor = Color.parseColor("#D5DBE8");// defalt color of normal state
    private int selectColor = Color.parseColor("#508CEE");// defalt color of selected state
    
    private int mColor;
    private int mTextSize;

    private boolean isUnlocking;
    private boolean hasNewCircles;
    private ArrayList<String> passList = new ArrayList<String>();
    private CreateGestureListener createListener;// the listener of creating gesture
    
    private static final String[] numberList = new String[] {
            "1", "4", "2", "5"
    };

    /**
     * used for refresh the canvas after MotionEvent.ACTION_UP
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            resetAll();
            invalidate();
            return true;
        }
    });

    public GestureView(Context context) {
        this(context, null, 0);
    }

    public GestureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.GestureView);

        // 解析集合中的属性circle_color属性
        // 该属性的id为:R.styleable.CircleView_circle_color
        // 将解析的属性传入到画圆的画笔颜色变量当中（本质上是自定义画圆画笔的颜色）
        // 第二个参数是默认设置颜色（即无指定circle_color情况下使用）
        mColor = a.getColor(R.styleable.GestureView_center_text_color,Color.BLACK);
        mTextSize = a.getDimensionPixelOffset(R.styleable.GestureView_center_text_size, GestureView.dip2px(context, 30));
        normalColor = a.getColor(R.styleable.GestureView_circle_normal_color,Color.parseColor("#D5DBE8"));
        selectColor = a.getColor(R.styleable.GestureView_circle_select_color,Color.parseColor("#508CEE"));
        normalR = a.getDimensionPixelOffset(R.styleable.GestureView_circle_normal_color_size, GestureView.dip2px(context, 20));
        // 解析后释放资源
        a.recycle();
        
        this.ctx = context;
        strokeWidth = dip2px(ctx, strokeWidth);
        pathWidth = dip2px(ctx, pathWidth);
    }

    /**
     * reset all states
     */
    private void resetAll() {
        isUnlocking = false;
        mPath.reset();
        tempPath.reset();
        rectMains.clear();
        passList.clear();
        for (Circle circle : circleList) {
            circle.setState(CIRCLE_NORMAL);
        }
        pathPaint.setColor(Color.GRAY);
        rectSelPaint.setColor(selectColor);
        smallCirSelPaint.setColor(selectColor);
        clearCanvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(circletBmp, 0, 0, null);
        for (int i = 0; i < circleList.size(); i++) {
            Circle circle = circleList.get(i);
            float tWidth = textPaint.measureText(numberList[i]);
            canvas.drawText(numberList[i], circle.getX() - tWidth / 2, circle.getY() + tWidth / 2, textPaint);
            drawCircles(circle);
        }
        canvas.drawPath(mPath, pathPaint);
    }
    
    @Override
    public boolean onHoverEvent(MotionEvent event) {
        return onTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TAG", "Enter into this onTouchEvent1 and this code is:" + event.getAction());
        int curX = (int) event.getX();
        int curY = (int) event.getY();
        Circle circle = getOuterCircle(curX, curY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER://9
            case MotionEvent.ACTION_DOWN://0
                Log.d("TAG", "Enter into this ACTION_DOWN1 and this circle is:" + circle);
                this.resetAll();
                if (circle != null) {
                    rootX = circle.getX();
                    rootY = circle.getY();
                    circle.setState(CIRCLE_SELECTED);
                    isUnlocking = true;
                    rectMains.add(circle.getRectMain());
                    tempPath.moveTo(rootX, rootY);
                    addItem(circle.getReadText());
                    Fusion.syncTTS(circle.getReadText());
                }
                break;
            case MotionEvent.ACTION_HOVER_MOVE://7
            case MotionEvent.ACTION_MOVE://2
                if (isUnlocking) {
                    mPath.reset();
                    mPath.addPath(tempPath);
                    mPath.moveTo(rootX, rootY);
                    mPath.lineTo(curX, curY);
                    handleMove(circle);
                }
                break;
            case MotionEvent.ACTION_HOVER_EXIT://10
            case MotionEvent.ACTION_UP://1
                isUnlocking = false;
                if (rectMains.size() > 0) {
                    mPath.reset();
                    mPath.addPath(tempPath);
                    StringBuilder sb = new StringBuilder();
                    for (String num : passList) {
                        sb.append(num);
                    }

                    if (createListener != null) {
                        createListener.onGestureCreated(sb.toString());
                    }
                    resetAll();
                }
                break;
        }
        invalidate();
        return true;
    }

    private synchronized void handleMove(Circle c) {
        if (c != null && !(c.getState() == CIRCLE_SELECTED)) {
            c.setState(CIRCLE_SELECTED);
            rectMains.add(c.getRectMain());
            rootX = c.getX();
            rootY = c.getY();
            tempPath.lineTo(rootX, rootY);
            addItem(c.getReadText());
            Fusion.syncTTS(c.getReadText());
        }
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
        // init all path/paint
        mPath = new Path();
        tempPath = new Path();
        pathPaint = new Paint();
        pathPaint.setColor(Color.GRAY);
        pathPaint.setDither(true);
        pathPaint.setAntiAlias(true);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
        pathPaint.setStrokeWidth(pathWidth);
        // 普通状态小圆画笔
        circletBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(circletBmp);
        cirNorPaint = new Paint();
        cirNorPaint.setAntiAlias(true);
        cirNorPaint.setDither(true);
        cirNorPaint.setColor(normalColor);
        // 文字画笔
        textPaint = new Paint();
        textPaint.setColor(mColor);
        textPaint.setTextSize(mTextSize);
        // 选中状态外框画笔
        rectSelPaint = new Paint();
        rectSelPaint.setAntiAlias(true);
        rectSelPaint.setDither(true);
        rectSelPaint.setStyle(Paint.Style.STROKE);
        rectSelPaint.setStrokeWidth(strokeWidth);
        rectSelPaint.setColor(selectColor);
        // 选中状态小圆画笔
        smallCirSelPaint = new Paint();
        smallCirSelPaint.setAntiAlias(true);
        smallCirSelPaint.setDither(true);
        smallCirSelPaint.setColor(selectColor);

        // init all circles
        int hor = width / 4;
        int ver = height / 4;
        if (!hasNewCircles) {
            for (int i = 0; i < 4; i++) {
                int tempX = (i % 2 + 1) * 2 * hor - hor;
                int tempY = (i / 2 + 1) * 2 * ver - ver;
                float tempLeft = tempX - hor;
                float tempRight = tempX + hor;
                float tempTop = tempY - ver;
                float tempBootom = tempY + ver;
                RectMain rectMain = new RectMain(tempLeft, tempRight, tempTop, tempBootom);
                Circle circle = new Circle(i, tempX, tempY, CIRCLE_NORMAL, rectMain, numberList[i]);
                circleList.add(circle);
            }
        }
        hasNewCircles = true;
    }

    /**
     * called in onDraw for drawing all circles
     *
     * @param circle
     */
    private void drawCircles(Circle circle) {
        switch (circle.getState()) {
            case CIRCLE_NORMAL:
                mCanvas.drawCircle(circle.getX(), circle.getY(), normalR, cirNorPaint);
                break;
            case CIRCLE_SELECTED:
                RectMain rectMain = circle.getRectMain();
                mCanvas.drawRect(rectMain.getLeft(), rectMain.getTop(), rectMain.getRifht(), rectMain.getBottom(), rectSelPaint);
                mCanvas.drawCircle(circle.getX(), circle.getY(), normalR, smallCirSelPaint);
                break;
        }
    }

    /**
     * clear canvas
     */
    private void clearCanvas() {
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        mPath.reset();
        tempPath.reset();
    }

    /**
     * J U S T A T O O L !
     *
     * @param context
     *            Context
     * @param dipValue
     *            value of dp
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * check whether the point is in a circle
     *
     * @param x
     * @param y
     * @return
     */
    private Circle getOuterCircle(int x, int y) {
        for (int i = 0; i < circleList.size(); i++) {
            Circle circle = circleList.get(i);
            if ((x - circle.getX()) * (x - circle.getX()) + (y - circle.getY()) * (y - circle.getY()) <= (normalR + 10) * (normalR + 10)) {
                if (circle.getState() != CIRCLE_SELECTED) {
                    return circle;
                }
            }
        }
        return null;
    }

    /**
     * check whether the password list contains the number
     *
     * @param num
     * @return
     */
    private boolean arrContainsInt(String num) {
        for (String value : passList) {
            if (num.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * put the num into password list
     *
     * @param num
     */
    private void addItem(String text) {
        if (!arrContainsInt(text)) {
            passList.add(text);
        }
    }

    /**
     * Create Mode Listener
     */
    public interface CreateGestureListener {
        void onGestureCreated(String result);
    }

    public void setGestureListener(CreateGestureListener listener) {
        this.createListener = listener;
    }

    public void setPathWidth(int pathWidth) {
        this.pathWidth = pathWidth;
    }

    public void setNormalR(int normalR) {
        this.normalR = normalR;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public void setSelectColor(int selectColor) {
        this.selectColor = selectColor;
    }

    public boolean isUnlocking() {
        return isUnlocking;
    }
}
