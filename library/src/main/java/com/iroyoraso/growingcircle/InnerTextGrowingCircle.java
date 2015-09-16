package com.iroyoraso.growingcircle;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.iroyoraso.growingcircle.library.R;

import java.text.DecimalFormat;

/**
 * Created by iroyo on 15/09/2015.
 */
public class InnerTextGrowingCircle extends View {

    private float titleSize;
    private String titleText = "";
    private float resultSize;
    private String unit = "";
    private float value = 0f;
    private float max;
    private float min;

    private float startAngle = 270f;
    private float angle = 0f;
    private float phase = 0f;

    private float thickness;
    private DecimalFormat formatValue;
    private RectF circleBox = new RectF();

    private Paint MainCirclePaint;
    private Paint BaseCirclePaint;
    private Paint BackgroundPaint;
    private Paint ValuePaint;
    private Paint TitlePaint;

    private int widthCanvas;
    private int heightCanvas;

    private ObjectAnimator drawAnimator;

    public InnerTextGrowingCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public InnerTextGrowingCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        MainCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        MainCirclePaint.setStyle(Paint.Style.STROKE);

        BaseCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BaseCirclePaint.setStyle(Paint.Style.STROKE);

        BackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BackgroundPaint.setStyle(Paint.Style.FILL);

        ValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ValuePaint.setStyle(Paint.Style.STROKE);
        ValuePaint.setTextAlign(Paint.Align.CENTER);

        TitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TitlePaint.setStyle(Paint.Style.STROKE);
        TitlePaint.setTextAlign(Paint.Align.CENTER);

        drawAnimator = ObjectAnimator.ofFloat(this, "phase", 0, 1);
        drawAnimator.setInterpolator(new AccelerateDecelerateInterpolator());


        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GrowingSlider);

        // GET VALUES FROM XML

        this.setThickness(a.getDimension(R.styleable.GrowingSlider_circle_thickness, 20f));
        this.setUnit(a.getString(R.styleable.GrowingSlider_circle_resultUnit));
        this.setFormatDigits(a.getInt(R.styleable.GrowingSlider_circle_resultDecimals, 2));
        this.setResultColor(a.getColor(R.styleable.GrowingSlider_circle_resultColor, Color.BLACK));
        this.setResultSize(a.getDimension(R.styleable.GrowingSlider_circle_resultSize, 16));
        this.setTitleColor(a.getColor(R.styleable.GrowingSlider_circle_titleColor, Color.BLACK));
        this.setTitleSize(a.getDimension(R.styleable.GrowingSlider_circle_titleSize, 18));
        this.setTitleStyle(a.getInt(R.styleable.GrowingSlider_circle_titleStyle, 0));
        this.setTitle(a.getString(R.styleable.GrowingSlider_circle_titleText));
        this.setMainColor(a.getColor(R.styleable.GrowingSlider_circle_colorMain, Color.CYAN));
        this.setBaseColor(a.getColor(R.styleable.GrowingSlider_circle_colorBase, Color.GRAY));
        this.setMax(a.getFloat(R.styleable.GrowingSlider_circle_maxValue, 100));
        this.setMin(a.getFloat(R.styleable.GrowingSlider_circle_minValue, 0));
        this.setValue(a.getFloat(R.styleable.GrowingSlider_circle_initialValue, 50));
        this.setAnimationDuration(a.getInt(R.styleable.GrowingSlider_circle_animateDuration, 2000));

        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float n = thickness / 2;
        this.circleBox = new RectF(n, n, w - n, h - n);
        this.widthCanvas = w;
        this.heightCanvas = h;
    }

    // SETTERS

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
        this.value = value;

        float result = (value - min) / (max - min) * 100;
        this.angle = (result * 360) / 100;

        drawAnimator.start();
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setResultSize(float size) {
        this.resultSize = size;
        ValuePaint.setTextSize(size);
    }

    public void setResultColor(int color) {
        ValuePaint.setColor(color);
    }

    public void setTitleSize(float size) {
        this.titleSize = size;
        TitlePaint.setTextSize(size);
    }

    public void setTitleColor(int color) {
        TitlePaint.setColor(color);
    }

    public void setTitleStyle(int v) {
        switch (v) {
            case 0: TitlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)); break;
            case 1: TitlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); break;
            case 2: TitlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)); break;
        }
    }

    public void setTitle(String title) {
        this.titleText = title;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
        MainCirclePaint.setStrokeWidth(thickness);
        BaseCirclePaint.setStrokeWidth(thickness);
    }

    public void setStartAngle(float angle) {
        startAngle = angle;
    }

    public void setAnimationDuration(int durationmillis) {
        drawAnimator.setDuration(durationmillis);
    }

    public void setFormatDigits(int digits) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            if (i == 0) b.append(".");
            b.append("0");
        }
        formatValue = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    public void setPhase(float phase) {
        this.phase = phase;
        invalidate();
    }

    public float getPhase() {
        return phase;
    }

    // DRAWS ----------------------------------------

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBaseCircle(canvas);
        drawMainCircle(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas c) {
        String textValue = formatValue.format(value * phase) + " " + unit;
        c.drawText(titleText, widthCanvas / 2, (heightCanvas / 2) - (titleSize / 2), TitlePaint);
        c.drawText(textValue, widthCanvas / 2, (heightCanvas / 2) + resultSize, ValuePaint);
    }

    private void drawBaseCircle(Canvas c) {
        c.drawArc(circleBox, startAngle, 360, false, BaseCirclePaint);
    }

    private void drawMainCircle(Canvas c) {
        float angle = this.angle * phase;
        c.drawArc(circleBox, startAngle, angle, false, MainCirclePaint);
    }

}
