package cn.chestnut.mvvm.teamworker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.chestnut.mvvm.teamworker.R;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/4 20:06:11
 * Description：字母索引侧边栏
 * Email: xiaoting233zhang@126.com
 */

public class WordsIndexBar extends View {

    /*字母画笔*/
    private Paint wordPaint;

    /*字母圆圈背景画笔*/
    private Paint bgPaint;

    /*字母字体大小*/
    private float wordSize;

    /*侧边索引栏的宽度(字母宽度)*/
    private float barWidth;

    /*每一个字母所占的高度*/
    private float itemHeight;

    /*索引栏的字母*/
    private String words[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    /*被选中的字母颜色*/
    private int wordsSelectedColor;

    /*未被选中的字母颜色*/
    private int wordsUnselectedColor;

    /*字母背景圆圈颜色*/
    private int wordsBackgroundColor;

    /*手指按下的字母的索引*/
    private int touchIndex;

    //获取字母的宽高
    private Rect rect = new Rect();

    /*手指按下的字母改变接口*/
    private OnWordChangeListener onWordChangeListener;

    public WordsIndexBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WordsIndexBar);
        wordSize = typedArray.getDimension(R.styleable.WordsIndexBar_words_size, 23);
        wordsSelectedColor = typedArray.getColor(R.styleable.WordsIndexBar_words_selected_color, getResources().getColor(R.color.appTheme));
        wordsUnselectedColor = typedArray.getColor(R.styleable.WordsIndexBar_words_unselected_color, getResources().getColor(R.color.transparent));
        wordsBackgroundColor = typedArray.getColor(R.styleable.WordsIndexBar_words_background_color, getResources().getColor(R.color.appTheme));
        typedArray.recycle();

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(wordsBackgroundColor);
        wordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wordPaint.setTextSize(wordSize);
        rect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        barWidth = getMeasuredWidth();
        itemHeight = getMeasuredHeight() / 27;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 27; i++) {
            //判断是否为按下的字母，若是则绘制字母的圆形背景色,并改变字母画笔的颜色
            if (i == touchIndex) {
                canvas.drawCircle(barWidth / 2, itemHeight * i + itemHeight / 2, itemHeight / 2, bgPaint);
                wordPaint.setColor(wordsSelectedColor);
            } else {
                wordPaint.setColor(wordsUnselectedColor);
            }
            //获取字母的宽高
            wordPaint.getTextBounds(words[i], 0, 1, rect);
            int wordWidth = rect.width();
            int wordHeight = rect.height();
            //绘制字母
            float wordX = barWidth / 2 - wordWidth / 2;
            float wordY = itemHeight / 2 + i * itemHeight + wordHeight / 2;
            canvas.drawText(words[i], wordX, wordY, wordPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

            case MotionEvent.ACTION_MOVE:
                //获取按下的字母的索引
                int index = (int) (event.getY() / itemHeight);
                if (index != touchIndex) {
                    touchIndex = index;
                }
                //防止数组越界
                if (onWordChangeListener != null && touchIndex >= 0 && touchIndex < words.length) {
                    onWordChangeListener.onWordChange(words[touchIndex]);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /*设置当前按下的是那个字母*/
    public void setTouchIndex(String word) {
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(word)) {
                touchIndex = i;
                invalidate();
                return;
            }
        }
    }

    /*手指按下了哪个字母的回调接口*/
    public interface OnWordChangeListener {
        void onWordChange(String words);
    }

    /*设置手指按下字母改变监听*/
    public void setOnWordsChangeListener(OnWordChangeListener onWordChangeListener) {
        this.onWordChangeListener = onWordChangeListener;
    }
}
