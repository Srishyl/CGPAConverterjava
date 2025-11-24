package com.example.cgpaconverter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {

    private Paint paint;
    private RectF rect;
    private float percentage = 0f;

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rect = new RectF();
    }

    public void setPercentage(double percent) {
        percentage = (float) Math.max(0, Math.min(100, percent));
        invalidate(); // redraw chart
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = (Math.min(getWidth(), getHeight()) / 2f) * 0.82f;

        // Set chart area
        rect.set(centerX - radius, centerY - radius,
                centerX + radius, centerY + radius);

        // Background circle (light blue)
        paint.setColor(Color.parseColor("#D6E8FF"));
        canvas.drawArc(rect, 0, 360, true, paint);

        // Foreground arc (blue)
        paint.setColor(Color.parseColor("#1A73E8"));
        float sweepAngle = (percentage / 100f) * 360;
        canvas.drawArc(rect, -90, sweepAngle, true, paint);

        // Text (dark blue)
        paint.setColor(Color.parseColor("black"));
        paint.setTextSize(52f);
        paint.setTextAlign(Paint.Align.CENTER);

        String text = ((int) percentage) + "%";
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        canvas.drawText(text, centerX, centerY + bounds.height() / 2f, paint);
    }
}
