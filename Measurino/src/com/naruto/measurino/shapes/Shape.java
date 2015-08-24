package com.naruto.measurino.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public abstract class Shape {
	final public static int DEFAULT_COLOR = Color.RED;
	final public static int DEFAULT_STROKE_WIDTH = 10;
	private float x1, y1, x2, y2;
	private int color;
	private String width = "???";

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public float getX1() {
		return x1;
	}

	public void setX1(float x1) {
		this.x1 = x1;
	}

	public float getY1() {
		return y1;
	}

	public void setY1(float y1) {
		this.y1 = y1;
	}

	public float getX2() {
		return x2;
	}

	public void setX2(float x2) {
		this.x2 = x2;
	}

	public float getY2() {
		return y2;
	}

	public void setY2(float y2) {
		this.y2 = y2;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public abstract void correctCoordinates();

	public abstract void draw(Canvas g, Paint p, boolean isSelected);

	public abstract boolean inside(float x, float y);

	public abstract Shape getThis();

	public abstract void move(float x, float y);

}
