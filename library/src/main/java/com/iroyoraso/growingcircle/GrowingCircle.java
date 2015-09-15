package com.iroyoraso.growingcircle;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.iroyoraso.growingcircle.library.R;

import java.text.DecimalFormat;

/**
 * Created by iroyo on 15/09/2015.
 */
public class GrowingCircle extends View {

    private String unit = "";
    private float startAngle = 270f;
    private float stepSize = 1f;
    private float angle = 0f;
    private float phase = 0f;
    private float value = 0f;
    private float max;
    private float min;
    private float thickness;
    private boolean touch = true;
    private String textTitle = "";
    private DecimalFormat formatValue;
    private RectF circleBox = new RectF();

    private Paint MainCirclePaint;
    private Paint BaseCirclePaint;
    private Paint BackgroundPaint;
    private Paint ValuePaint;
    private Paint TitlePaint;

    private SelectionListener listener;
    private ObjectAnimator drawAnimator;
    private boolean mBoxSetup = false;

    public GrowingCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GrowingCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mBoxSetup = false;

        MainCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        MainCirclePaint.setStyle(Paint.Style.FILL);

        BaseCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BaseCirclePaint.setStyle(Paint.Style.FILL);

        BackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BackgroundPaint.setStyle(Paint.Style.FILL);

        ValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ValuePaint.setStyle(Paint.Style.STROKE);
        ValuePaint.setTextAlign(Paint.Align.CENTER);

        TitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TitlePaint.setStyle(Paint.Style.STROKE);
        TitlePaint.setTextAlign(Paint.Align.CENTER);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GrowingSlider);

        // GET VALUES FROM XML

        this.setBackgroundColor(a.getColor(R.styleable.GrowingSlider_circle_colorBackground, Color.WHITE));
        this.setThickness(a.getFloat(R.styleable.GrowingSlider_circle_thickness, 20f));
        this.setUnit(a.getString(R.styleable.GrowingSlider_circle_resultUnit));
        this.setFormatDigits(a.getInt(R.styleable.GrowingSlider_circle_resultDecimals, 2));
        this.setResultColor(a.getColor(R.styleable.GrowingSlider_circle_resultColor, Color.BLACK));
        this.setResultSize(a.getDimension(R.styleable.GrowingSlider_circle_resultSize, 16));
        this.setTitleColor(a.getColor(R.styleable.GrowingSlider_circle_titleColor, Color.BLACK));
        this.setTitleSize(a.getDimension(R.styleable.GrowingSlider_circle_titleSize, 18));
        this.setMainColor(a.getColor(R.styleable.GrowingSlider_circle_colorMain, Color.CYAN));
        this.setBaseColor(a.getColor(R.styleable.GrowingSlider_circle_colorBase, Color.GRAY));
        this.setValue(a.getFloat(R.styleable.GrowingSlider_circle_initialValue, 50));
        this.setMax(a.getFloat(R.styleable.GrowingSlider_circle_maxValue, 100));
        this.setMin(a.getFloat(R.styleable.GrowingSlider_circle_minValue, 0));

        drawAnimator = ObjectAnimator.ofFloat(this, "phase", phase, 1.0f).setDuration(3000);
        drawAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        a.recycle();
    }

    // SETTERS
    public void setBackgroundColor(int color) {
        BackgroundPaint.setColor(color);
    }

    public void setMainColor(int color) {
        MainCirclePaint.setColor(color);
    }

    public void setBaseColor(int color) {
       BaseCirclePaint.setColor(color);
    }

    public void setMin(float min) {
        this.min = min;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void setValue(float value) {

    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setResultSize(float size) {
        ValuePaint.setTextSize(size);
    }

    public void setResultColor(int color) {
        ValuePaint.setColor(color);
    }

    public void setTitleSize(float size) {
        TitlePaint.setTextSize(size);
    }

    public void setTitleColor(int color) {
        TitlePaint.setColor(color);
    }

    public void setTitle(String title) {
        this.textTitle = title;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public void setFormatDigits(int digits) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            if (i == 0) b.append(".");
            b.append("0");
        }
        formatValue = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    // GETTERS

    public String getTitle() {
        return textTitle;
    }


    public void setSize(int size) {
        this.getLayoutParams().width = size;
        this.getLayoutParams().height = size;
        mDown = Math.round(size / 10.0f) + 4;
        mUp = Math.round(size / 10.0f) - 6;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mBoxSetup) {
            mBoxSetup = true;
            setupBox();
        }

        drawBaseCircle(canvas);
        drawMainCircle(canvas);
        drawBackgroundCircle(canvas);
        drawText(canvas);

    }

    // DRAWS

    private void drawText(Canvas c) {
        c.drawText(textTitle, getWidth() / 2, (getHeight() / 2) - mUp, TitlePaint);
        c.drawText(formatValue.format(value * phase) + " " + unit, getWidth() / 2, (getHeight() / 2) + mDown, ValuePaint);
    }

    private void drawBaseCircle(Canvas c) {
        float r = getRadius();
        c.drawCircle(getWidth() / 2, getHeight() / 2, r, MainCirclePaint);
    }

    private void drawBackgroundCircle(Canvas c) {
        c.drawCircle(getWidth() / 2, getHeight() / 2, getRadius() / 100f * (100f - thickness), BackgroundPaint);
    }

    private void drawMainCircle(Canvas c) {
        float angle = this.angle * phase;
        c.drawArc(circleBox, startAngle, angle, true, MainCirclePaint);
    }

    private void setupBox() {
        int width = getWidth();
        int height = getHeight();
        float diameter = getDiameter();
        circleBox = new RectF(width / 2 - diameter / 2, height / 2 - diameter / 2, width / 2 + diameter / 2, height / 2 + diameter / 2);
    }

    public void showValue(float toShow, float total, boolean animated) {
        angle = calcAngle(toShow / total * 100f);
        value = toShow;
        max = total;

        if (animated) startAnim();
        else {
            phase = 1f;
            invalidate();
        }
    }

    public float getValue() {
        return value;
    }

    public void startAnim() {
        phase = 0f;
        drawAnimator.start();
    }

    public void setAnimDuration(int durationmillis) {
        drawAnimator.setDuration(durationmillis);
    }

    public float getDiameter() {
        return Math.min(getWidth(), getHeight());
    }

    public float getRadius() {
        return getDiameter() / 2f;
    }

    private float calcAngle(float percent) {
        return percent / 100f * 360f;
    }

    public void setStartAngle(float angle) {
        startAngle = angle;
    }

    public float getPhase() {
        return phase;
    }

    public void setPhase(float phase) {
        this.phase = phase;
        invalidate();
    }

    private int mUp;
    private int mDown;

    public void setTextSize(float size) {
        ValuePaint.setTextSize(Utils.convertDpToPixel(getResources(), size));
    }

    public void setTitleTextSize(float size) {
        TitlePaint.setTextSize(Utils.convertDpToPixel(getResources(), size));
    }

    public static final int PAINT_TEXT = 1;
    public static final int PAINT_ARC = 2;
    public static final int PAINT_INNER = 3;


    public void setPaint(int which, Paint p) {
        switch (which) {
            case PAINT_ARC:
                MainCirclePaint = p;
                break;
            case PAINT_INNER:
                BackgroundPaint = p;
                break;
            case PAINT_TEXT:
                ValuePaint = p;
                break;
        }
    }

    public void setStepSize(float stepsize) {
        stepSize = stepsize;
    }

    public float getStepSize() {
        return stepSize;
    }

    public PointF getCenter() {
        return new PointF(getWidth() / 2, getHeight() / 2);
    }

    public void setTouchEnabled(boolean enabled) {
        touch = enabled;
    }

    public boolean isTouchEnabled() {
        return touch;
    }

    public void setSelectionListener(SelectionListener l) {
        listener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (touch) {

            float x = e.getX();
            float y = e.getY();

            float distance = distanceToCenter(x, y);
            float r = getRadius();

            if (distance >= r - r * thickness / 100f && distance < r) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        updateValue(x, y);
                        invalidate();
                        if (listener != null) listener.onSelectionUpdate(value, max);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (listener != null) listener.onValueSelected(value, max);
                        break;
                }
            }
            return true;
        } else
            return super.onTouchEvent(e);
    }

    private void updateValue(float x, float y) {
        float angle = getAngleForPoint(x, y);
        float newVal = max * angle / 360f;
        if (stepSize == 0f) {
            value = newVal;
            this.angle = angle;
            return;
        }

        float remainder = newVal % stepSize;

        if (remainder <= stepSize / 2f) newVal = newVal - remainder;
        else newVal = newVal - remainder + stepSize;

        this.angle = getAngleForValue(newVal);
        value = newVal;
    }

    public float getAngleForPoint(float x, float y) {
        PointF c = getCenter();

        double tx = x - c.x, ty = y - c.y;
        double length = Math.sqrt(tx * tx + ty * ty);
        double r = Math.acos(ty / length);

        float angle = (float) Math.toDegrees(r);

        if (x > c.x) angle = 360f - angle;
        angle = angle + 180;

        if (angle > 360f) angle = angle - 360f;

        return angle;
    }

    public float getAngleForValue(float value) {
        return value / max * 360f;
    }

    public float getValueForAngle(float angle) {
        return angle / 360f * max;
    }

    public float distanceToCenter(float x, float y) {
        PointF c = getCenter();
        float dist;
        float xDist;
        float yDist;

        if (x > c.x) xDist = x - c.x;
        else xDist = c.x - x;

        if (y > c.y) yDist = y - c.y;
        else yDist = c.y - y;

        dist = (float) Math.sqrt(Math.pow(xDist, 2.0) + Math.pow(yDist, 2.0));

        return dist;
    }

    public interface SelectionListener {
        void onSelectionUpdate(float val, float maxval);
        void onValueSelected(float val, float maxval);
    }

    public static abstract class Utils {

        public static float convertDpToPixel(Resources r, float dp) {
            DisplayMetrics metrics = r.getDisplayMetrics();
            return dp * (metrics.densityDpi / 160f);
        }
    }
}
