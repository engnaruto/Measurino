package com.naruto.measurino.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

public class Rectangle extends Shape {

	private String height = "???";

	public Rectangle(float x1, float y1, float x2, float y2, int color) {
		setX1(Math.min(x1, x2));
		setY1(Math.min(y1, y2));
		setX2(getX1() + Math.abs(x1 - x2));
		setY2(getY1() + Math.abs(y1 - y2));
		setColor(color);
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	@Override
	public void draw(Canvas g, Paint p, boolean isSelected) {
		if (!isSelected) {
			p.setColor(getColor());
		}
		g.drawRect(getX1(), getY1(), getX2(), getY2(), p);

		p.setStrokeWidth(0);
		p.setTextSize(30);
		Typeface tf = Typeface.create("Helvetica", Typeface.NORMAL);
		p.setTypeface(tf);
		g.drawText(getWidth(), (getX2() + getX1()) / 2 - 20, getY1() - 5, p);
		g.save();
		g.rotate(-90, (getX2() + getX1()) / 2, (getY2() + getY1()) / 2);
		g.drawText(getHeight(), (getX2() + getX1()) / 2 - 20, getY1() - 5, p);
		g.restore();
		p.setStrokeWidth(DEFAULT_STROKE_WIDTH);

	}

	// @Override
	public boolean inside(float downx, float downy) {
		RectF tmp = new RectF(getX1(), getY1(), getX2(), getY2());
		return tmp.contains(downx, downy);
	}

	@Override
	public Shape getThis() {
		return new Rectangle(getX1(), getY1(), getX2(), getY2(), getColor());
	}

	@Override
	public void move(float _x, float _y) {
		setX1(getX1() + _x);
		setX2(getX2() + _x);
		setY1(getY1() + _y);
		setY2(getY2() + _y);
	}

	@Override
	public void correctCoordinates() {
		float x1 = getX1();
		float x2 = getX2();
		float y1 = getY1();
		float y2 = getY2();
		setX1(Math.min(x1, x2));
		setY1(Math.min(y1, y2));
		setX2(getX1() + Math.abs(x1 - x2));
		setY2(getY1() + Math.abs(y1 - y2));
	}

}
