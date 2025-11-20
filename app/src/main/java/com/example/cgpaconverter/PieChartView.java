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
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = (Math.min(getWidth(), getHeight()) / 2f) * 0.8f;

        rect.set(centerX - radius, centerY - radius,
                centerX + radius, centerY + radius);

        paint.setColor(Color.LTGRAY);
        canvas.drawArc(rect, 0, 360, true, paint);

        paint.setColor(Color.GREEN);
        float sweep = (percentage / 100f) * 360;
        canvas.drawArc(rect, -90, sweep, true, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(48f);
        paint.setTextAlign(Paint.Align.CENTER);

        String text = ((int) percentage) + "%";
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int textHeight = bounds.height();

        canvas.drawText(text, centerX, centerY + textHeight / 2f, paint);
    }
}
