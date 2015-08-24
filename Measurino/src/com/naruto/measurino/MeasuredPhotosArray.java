package com.naruto.measurino;

import java.util.ArrayList;

public class MeasuredPhotosArray {
	private ArrayList<MeasuredPhoto> list;

	public ArrayList<MeasuredPhoto> getList() {
		return list;
	}

	public void setList(ArrayList<MeasuredPhoto> list) {
		this.list = list;
	}

	public MeasuredPhotosArray() {
		list = new ArrayList<MeasuredPhoto>();
	}

	public void add(MeasuredPhoto m) {
		list.add(m);
	}

	public String[] toStringArray() {
		// TODO Auto-generated method stub
		String[] strings = new String[list.size()];

		for (int i = 0; i < strings.length; i++) {
			strings[i] = list.get(i).getName();
		}
		return strings;

	}
}
