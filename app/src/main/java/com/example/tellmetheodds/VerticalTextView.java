package com.example.tellmetheodds;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.RequiresApi;

public class VerticalTextView extends androidx.appcompat.widget.AppCompatTextView {

    Rect bounds = new Rect();

    public VerticalTextView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        //dimensions swapped
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onDraw(Canvas canvas){
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();


        canvas.translate(0, getHeight());
        canvas.rotate(-90);
//        textPaint.getTextBounds(getText(), 0, getText().length(), bounds);


        canvas.translate(getCompoundPaddingRight(), getExtendedPaddingTop());

        getLayout().draw(canvas);
//        canvas.drawText(getText().toString(),
//                (float)getCompoundPaddingLeft(),
//                (float)((bounds.height()-getWidth())/2),
//                textPaint);
        canvas.restore();
    }
}