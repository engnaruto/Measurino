package com.naruto.measurino;

import java.util.ArrayList;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MeasurinoViewer extends ListActivity {
	TextView test;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create a new Adapter containing a list of colors
		// Set the adapter on this ListActivity's built-in ListView

		String json = MeasurinoEditor.load(getApplicationContext());

		final Gson gson = new Gson();

		MeasuredPhotosArray list = gson.fromJson(json,
				MeasuredPhotosArray.class);

		String[] string = new String[list.getList().size()];

		for (int i = 0; i < string.length; i++) {
			string[i] = list.getList().get(i).getName();
		}

		setListAdapter(new PhotoAdapter(this, R.layout.list_item,
				list.getList()));

		ListView lv = getListView();
		lv.setBackgroundColor(Color.WHITE);
		// Enable filtering when the user types in the virtual keyboard
		lv.setTextFilterEnabled(true);
		// Set an setOnItemClickListener on the ListView
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Display a Toast message indicting the selected item
				// Toast.makeText(getApplicationContext(),
				// ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				MeasuredPhoto o = (MeasuredPhoto) getListAdapter().getItem(
						position);

//				Toast.makeText(getApplicationContext(), o.getName(),
//						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(MeasurinoViewer.this,
						MeasurinoPhotoViewer.class);
				String g = gson.toJson(o);
				intent.putExtra("data", g);
				startActivity(intent);

			}
		});


	}

	private class PhotoAdapter extends ArrayAdapter<MeasuredPhoto> {

		private ArrayList<MeasuredPhoto> items;

		public PhotoAdapter(Context context, int textViewResourceId,
				ArrayList<MeasuredPhoto> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_item, null);
			}
			MeasuredPhoto o = items.get(position);
			if (o != null) {
				TextView tt = (TextView) v.findViewById(R.id.row);
				if (tt != null) {
					tt.setText(o.getName());
					tt.setTextColor(Color.BLACK);
				}

			}

			return v;
		}
	}
}
