package com.naruto.measurino;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final static int PICK_FROM_CAMERA = 1;
	private final static int PICK_FROM_FILE = 2;
	protected final static String DIRECTORY_NAME = "Measurino";

	private Button capturePhotoButton;
	private Button viewPhotoButton;
	private Button openPhotoButton;
	private Button test;

	private Uri capturedPhotoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		test = (Button) findViewById(R.id.test1);
		capturePhotoButton = (Button) findViewById(R.id.capturePhotoButton);
		openPhotoButton = (Button) findViewById(R.id.openPhotoButton);
		viewPhotoButton = (Button) findViewById(R.id.viewPhotoButton);

		test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						MeasurinoEditor.class);
				startActivity(intent);

			}
		});

		capturePhotoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * To take a photo from camera, pass intent action
				 * ‘MediaStore.ACTION_IMAGE_CAPTURE‘ to open the camera app.
				 */
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				/**
				 * Also specify the Uri to save the image on specified path and
				 * file name. Note that this Uri variable also used by gallery
				 * app to hold the selected image path.
				 */
				capturedPhotoPath = Uri.fromFile(new File(Environment
						.getExternalStoragePublicDirectory(DIRECTORY_NAME),
						"Measurino_TEMP_"
								+ String.valueOf(System.currentTimeMillis())
								+ ".jpg"));

				intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedPhotoPath);

				try {
					intent.putExtra("return-data", true);

					startActivityForResult(intent, PICK_FROM_CAMERA);

				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}

			}
		});

		openPhotoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * To select an image from existing files, use
				 * Intent.createChooser to open image chooser. Android will
				 * automatically display a list of supported applications, such
				 * as image gallery or file manager.
				 */
				Intent intent = new Intent();

				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);

				startActivityForResult(
						Intent.createChooser(intent, "Complete action using"),
						PICK_FROM_FILE);
			}
		});

		viewPhotoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						MeasurinoViewer.class);
				if (capturedPhotoPath != null) {
					startActivity(intent);
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Intent in = new Intent(this, MeasurinoEditor.class);
		switch (requestCode) {
		case PICK_FROM_CAMERA:
			/**
			 * After taking a picture, do the crop
			 */
//			if (data != null) {
				in.putExtra("path", capturedPhotoPath);
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						capturedPhotoPath));

				startActivity(in);
//			}
			break;

		case PICK_FROM_FILE:
			/**
			 * After selecting image from files, save the selected path
			 */
			if (data != null) {

				capturedPhotoPath = data.getData();
				in.putExtra("path", capturedPhotoPath);
				if (capturedPhotoPath != null) {
					startActivity(in);
				}
			}
			break;
		default:
			Toast.makeText(getApplicationContext(), "Error in Result",
					Toast.LENGTH_SHORT).show();

		}
	}
}
