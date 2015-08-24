package com.naruto.measurino;

public class MeasuredPhoto {
	private String path;
	private String name;
	private double area;

	public MeasuredPhoto(String path, String name, double area) {
		this.path = path;
		this.name = name;
		this.area = area;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

}
