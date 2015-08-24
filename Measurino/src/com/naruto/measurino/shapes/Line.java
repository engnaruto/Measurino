package com.naruto.measurino.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

public class Line extends Shape {

	// String string = "Hiii";
	double angle;
	int diameter;

	public Line(float x1, float y1, float x2, float y2, int color) {
		setX1(Math.min(x1, x2));
		setY1(Math.min(y1, y2));
		setColor(color);
		setX2(getX1() + Math.abs(x1 - x2));
		setY2(getY1() + Math.abs(y1 - y2));
		angle = Math.atan2(getY2() - getY1(), getX2() - getX1());
		diameter = (int) Math.sqrt((getX1() - getX2()) * (getX1() - getX2())
				+ (getY1() - getY2()) * (getY1() - getY2()));
	}

	@Override
	public void draw(Canvas g, Paint p, boolean isSelected) {
		if (!isSelected) {
			p.setColor(getColor());
		}
		g.drawLine(getX1(), getY1(), getX2(), getY2(), p);

		p.setStrokeWidth(0);
		p.setTextSize(30);

		g.save();
		g.rotate((float) -angle * 180, (getX2() + getX1()) / 2,
				(getY2() + getY1()) / 2);
		g.drawText(getWidth(), (getX2() + getX1()) / 2,
				(getY2() + getY1()) / 2 - 5, p);
		g.restore();
		p.setStrokeWidth(DEFAULT_STROKE_WIDTH);
	}

	@Override
	public boolean inside(float x, float y) {
		return isPointOnLineSegment(new PointF(x, y));
	}

	public boolean isPointOnLine(PointF lineStaPt, PointF lineEndPt,
			PointF point) {
		final float EPSILON = 40;
		if (Math.abs(lineStaPt.x - lineEndPt.x) < EPSILON) {
			// We've a vertical line, thus check only the x-value of the point.
			return (Math.abs(point.x - lineStaPt.x) < EPSILON);
		} else {
			float m = (lineEndPt.y - lineStaPt.y) / (lineEndPt.x - lineStaPt.x);
			float b = lineStaPt.y - m * lineStaPt.x;
			return (Math.abs(point.y - (m * point.x + b)) < EPSILON);
		}
	}

	public boolean isPointOnLineSegment(PointF point) {
		float EPSILON = .001f;
		PointF staPt = new PointF(getX1(), getY1());
		PointF endPt = new PointF(getX2(), getY2());

		if (isPointOnLine(staPt, endPt, point)) {
			// Create lineSegment bounding-box.
			RectF lb = new RectF(staPt.x, staPt.y, endPt.x, endPt.y);
			// Extend bounds with epsilon.
			RectF bounds = new RectF(lb.left - EPSILON, lb.top - EPSILON,
					lb.right + EPSILON, lb.bottom + EPSILON);
			Log.e("IN", bounds.contains(point.x, point.y) + "");
			// Check if point is contained within lineSegment-bounds.
			return bounds.contains(point.x, point.y);
		}
		return false;
	}

	// public boolean isPointOnLine(PointF lineStaPt, PointF lineEndPt,
	// PointF point, Canvas canvas, Paint paint) {
	// // draw(canvas, paint);
	// final float EPSILON = 50;
	// // paint.setColor(Color.BLACK);
	// // paint.setStrokeWidth(10);
	// // canvas.drawLine(lineStaPt.x, lineStaPt.y, lineEndPt.x, lineEndPt.y,
	// // paint);
	// if (Math.abs(lineStaPt.x - lineEndPt.x) < EPSILON) {
	// // We've a vertical line, thus check only the x-value of the point.
	// return (Math.abs(point.x - lineStaPt.x) < EPSILON);
	// } else {
	// float m = (lineEndPt.y - lineStaPt.y) / (lineEndPt.x - lineStaPt.x);
	// float b = lineStaPt.y - m * lineStaPt.x;
	// return (Math.abs(point.y - (m * point.x + b)) < EPSILON);
	// }
	// }
	//
	// public boolean isPointOnLineSegment(PointF point, Canvas canvas, Paint
	// paint) {
	// float EPSILON = .001f;
	// PointF staPt = new PointF(getX1(), getY1());
	// PointF endPt = new PointF(getX2(), getY2());
	//
	// // paint.setStrokeWidth(10);
	// if (isPointOnLine(staPt, endPt, point, canvas, paint)) {
	// // Create lineSegment bounding-box.
	// RectF lb = new RectF(staPt.x, staPt.y, endPt.x, endPt.y);
	// // Extend bounds with epsilon.
	// RectF bounds = new RectF(lb.left - EPSILON, lb.top - EPSILON,
	// lb.right + EPSILON, lb.bottom + EPSILON);
	// // paint.setColor(Color.BLUE);
	// // canvas.drawRect(bounds, paint);
	// // paint.setColor(Color.YELLOW);
	// // // // canvas.drawRect(bounds2, paint);
	// // draw(canvas, paint);
	// Log.e("IN", bounds.contains(point.x, point.y) + "");
	// // Check if point is contained within lineSegment-bounds.
	// return bounds.contains(point.x, point.y);
	// }
	// return false;
	// }

	@Override
	public Shape getThis() {
		return new Line(getX1(), getY1(), getX2(), getY2(), getColor());
	}

	@Override
	public void move(float x, float y) {
		// TODO Auto-generated method stub
		setX1(getX1() + x);
		setX2(getX2() + x);
		setY1(getY1() + y);
		setY2(getY2() + y);
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