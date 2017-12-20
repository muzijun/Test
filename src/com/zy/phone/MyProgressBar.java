package com.zy.phone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;
/**
 * 自定义加载圈，打开积分墙时显示
 * @author lws
 *
 */
public class MyProgressBar extends ProgressBar {
	String text;
	Paint mPaint;

	public MyProgressBar(Context context) {
		super(context);
		initText();
	}
	/**
	 * 构造函数
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initText();
	}
	/**
	 * 构造函数
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initText();
	}
	/**
	 * 构造函数
	 * @param context
	 */
	@Override
	public synchronized void setProgress(int progress) {
		setText(progress);
		super.setProgress(progress);
	}
	/**
	 * 画进度信息
	 */
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect();
		this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
		int x = (getWidth() / 2) - rect.centerX();

		int y = (getHeight() / 2) - rect.centerY();

		canvas.drawText(this.text, x, y, this.mPaint);
	}
	/**
	 * 初始化画笔
	 */
	private void initText() {
		this.mPaint = new Paint();
		this.mPaint.setColor(Color.BLACK);
		mPaint.setFakeBoldText(true);
		mPaint.setTextSize(16);
	}
	/**
	 * 进度值
	 * @param progress
	 */
	public void setText(int progress) {
		int i = (progress * 1) / this.getMax();
		this.text = progress + "%";
		invalidate();
	}
}
