package com.naruto.measurino;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MeasurinoPhotoViewer extends Activity {

	private Button button;
	private ImageView image;

	private double area;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurino_photo_viewer);

		button = (Button) findViewById(R.id.viewMeasurmentButton);
		image = (ImageView) findViewById(R.id.imageView1);

		Gson gson = new Gson();
		String json = getIntent().getStringExtra("data");
		MeasuredPhoto measuredPhoto = gson.fromJson(json, MeasuredPhoto.class);
		name = measuredPhoto.getName();
		setTitle(name);

		area = measuredPhoto.getArea();
		Uri uri = Uri.parse(measuredPhoto.getPath());

		File imagee = new File(uri.getPath().toString());

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();

		Bitmap bitmap = BitmapFactory.decodeFile(imagee.getAbsolutePath(),
				bmOptions);

		image.setImageBitmap(bitmap);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(MeasurinoPhotoViewer.this)
						.setTitle(name + " area measurement")
						.setMessage(
								Html.fromHtml("&#8226; " + area
										+ " m<sup>2</sup>"))
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

									}
								})

						.setIcon(android.R.drawable.ic_dialog_info).show();
			}
		});
		// image.invalidate();
	}
}
