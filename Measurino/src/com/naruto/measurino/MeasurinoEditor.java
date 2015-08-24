package com.naruto.measurino;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.naruto.measurino.ColorPickerDialog.OnColorChangedListener;
import com.naruto.measurino.shapes.Line;
import com.naruto.measurino.shapes.Rectangle;
import com.naruto.measurino.shapes.Shape;

public class MeasurinoEditor extends Activity implements OnTouchListener,
		OnColorChangedListener {

	final private static String JSON_FILE_NAME = "Data.jaon";
	final private static int DEFAULT_SHAPE_COLOR = Color.RED;
	final private static int DEFAULT_POINTER_COLOR = Color.GRAY;
	final private static int DEFAULT_POINTER_RADUIS = 10;
	final private static int DEFAULT_STROKE_WIDTH = 5;
	final private static int IDEL_STATE = -1;
	final private static int DRAWING_LINE_STATE = 1;
	final private static int DRAWING_RECANGLE_STATE = 2;

	private RadioGroup radioGroup;
	private RadioButton radioButton;
	private ImageView imageView;
	private Bitmap originalBitmap;
	private Bitmap bitmap;
	private Canvas canvas;
	private Paint paint;
	private float downx = 0, downy = 0, upx = 0, upy = 0;
	private ArrayList<Shape> shapes;
	private Shape currentShape;
	private int canvasState;
	Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurino_editor);
		initializeButtons();
		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		imageView = (ImageView) findViewById(R.id.imageView);
		Intent intent = getIntent();

		// Bitmap bitmap = null;
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		try {
			uri = intent.getParcelableExtra("path");
			if (uri != null) {

				originalBitmap = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), uri);
				InputStream image_stream = getContentResolver()
						.openInputStream(uri);
				BitmapFactory.Options bounds = new BitmapFactory.Options();
				bounds.inSampleSize = 4;
				Bitmap imgBitmap = BitmapFactory.decodeStream(image_stream,
						null, bounds);

				// Bitmap imgBitmap = BitmapFactory.decodeFile(
				// new File(uri.toString()).getAbsolutePath().toString()
				// .toString(), bounds);

				if (imgBitmap != null) {
					originalBitmap = imgBitmap.copy(Bitmap.Config.ARGB_8888,
							true);
				} else {
					Toast.makeText(getApplicationContext(),
							"Loading Image Error", Toast.LENGTH_SHORT).show();
				}

				imageView.setImageBitmap(imgBitmap);
				imageView.setAdjustViewBounds(true);
				imageView.setMaxHeight(150);
				imageView.setMaxWidth(100);

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// imageView.setImageURI(uri);
		if (originalBitmap == null) {
			originalBitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);

			Toast.makeText(getApplicationContext(), "Bitmap Null",
					Toast.LENGTH_SHORT).show();
		} else {
			// originalBitmap = codec(originalBitmap,
			// Bitmap.CompressFormat.JPEG,
		}
		bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
		canvas = new Canvas(bitmap);

		imageView.setImageBitmap(bitmap);
		imageView.setOnTouchListener(this);

		paint = new Paint();
		paint.setColor(DEFAULT_SHAPE_COLOR);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		shapes = new ArrayList<Shape>();
		canvasState = IDEL_STATE;
	}

	private void initializeButtons() {
		radioButton = (RadioButton) findViewById(R.id.drawLineButton);
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
		radioButton = (RadioButton) findViewById(R.id.drawRectangleButton);
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
		radioButton = (RadioButton) findViewById(R.id.editDimentionsButton);
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
		radioButton = (RadioButton) findViewById(R.id.editColorButton);
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
		radioButton = (RadioButton) findViewById(R.id.deleteShapeButton);
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
		radioButton = (RadioButton) findViewById(R.id.saveButton);
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
	}

	private OnCheckedChangeListener btnNavBarOnCheckedChangeListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				switch (buttonView.getId()) {
				case R.id.drawLineButton:
					Toast.makeText(getApplicationContext(), "Draw Line",
							Toast.LENGTH_SHORT).show();
					canvasState = DRAWING_LINE_STATE;
					break;
				case R.id.drawRectangleButton:
					Toast.makeText(getApplicationContext(), "Draw Rectangle",
							Toast.LENGTH_SHORT).show();
					canvasState = DRAWING_RECANGLE_STATE;
					break;

				case R.id.editDimentionsButton:
					createDialog(true);
					break;
				case R.id.editColorButton:
					if (currentShape != null) {
						new ColorPickerDialog(MeasurinoEditor.this,
								MeasurinoEditor.this, paint.getColor()).show();
					} else {
						Toast.makeText(getApplicationContext(),
								"No shape Selected", Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.deleteShapeButton:
					if (currentShape == null) {
						Toast.makeText(getApplicationContext(),
								"No shape selected to delete",
								Toast.LENGTH_SHORT).show();
					} else {
						currentShape = null;
						repaint();
					}
					radioGroup.clearCheck();
					break;
				case R.id.saveButton:

					Toast.makeText(getApplicationContext(), "Save",
							Toast.LENGTH_SHORT).show();
					// savePhoto();
					createDialog(false);
					break;
				default:
					break;
				}
			}
		}
	};

	private void repaint() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

		// image.draw(canvas);
		// imageView.setImageBitmap(bitmap);
		canvas.drawBitmap(bitmap, 0, 0, null);
		for (Shape shape : shapes) {
			Log.e("COLOR", shape.getColor() + ", SIZE= " + shapes.size());
			paint.setColor(shape.getColor());
			shape.draw(canvas, paint, false);
		}
		if (currentShape != null) {
			paint.setColor(Color.CYAN);
			currentShape.draw(canvas, paint, true);
		}
		imageView.invalidate();
	}

	float[] f;

	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		f = getPointerCoords(imageView, event);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downx = f[0];
			downy = f[1];
			// downx = event.getX();
			// downy = event.getY();
			touchDown();
			break;
		case MotionEvent.ACTION_MOVE:
			upx = f[0];
			upy = f[1];
			// upx = event.getX();
			// upy = event.getY();
			touchMoved();
			break;
		case MotionEvent.ACTION_UP:
			v.performClick();
			upx = f[0];
			upy = f[1];
			// upx = event.getX();
			// upy = event.getY();
			touchUp();
			break;
		default:
			break;

		}
		return true;
	}

	private void touchDown() {
		if (currentShape != null) {
			shapes.add(currentShape);
			currentShape = null;
		}
		switch (canvasState) {
		case DRAWING_LINE_STATE:
			currentShape = new Line(downx, downy, downx, downy, Color.RED);
			break;
		case DRAWING_RECANGLE_STATE:
			currentShape = new Rectangle(downx, downy, downx, downy, Color.RED);
			break;
		default:
			boolean shapeSelected = false;
			for (int i = shapes.size() - 1; i >= 0; i--) {
				Shape shape = shapes.get(i);
				if (shape.inside(downx, downy)) {
					if (currentShape != null) {
						shapes.add(currentShape);
					}
					currentShape = shape;
					shapes.remove(i);
					shapeSelected = true;
					break;
				}
			}
			if (!shapeSelected) {
				if (currentShape != null) {
					if (!currentShape.inside(downx, downy)) {
						shapes.add(currentShape);
						currentShape = null;
					}
				}
			}
			break;
		}

		repaint();
		drawPointer(downx, downy);
	}

	private void touchMoved() {
		switch (canvasState) {
		case IDEL_STATE:
			if (currentShape != null) {
				currentShape.move(upx - downx, upy - downy);
				downx = upx;
				downy = upy;
			}
			break;
		default:
			currentShape.setX2(upx);
			currentShape.setY2(upy);
			break;
		}

		repaint();
		drawPointer(upx, upy);
	}

	private void touchUp() {
		switch (canvasState) {
		case IDEL_STATE:
			if (currentShape != null) {
				currentShape.move(upx - downx, upy - downy);
			}
			break;
		default:
			currentShape.setX2(upx);
			currentShape.setY2(upy);
			currentShape.correctCoordinates();
			createDialog(true);
			break;
		}
		canvasState = IDEL_STATE;
		repaint();
		// drawPointer(upx, upy);
		radioGroup.clearCheck();
	}

	private void drawPointer(float x, float y) {
		paint.setColor(DEFAULT_POINTER_COLOR);
		canvas.drawCircle(x, y, DEFAULT_POINTER_RADUIS, paint);
		imageView.invalidate();
	}

	@SuppressLint("InflateParams")
	private void createDialog(boolean function) {

		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(getApplicationContext());
		View promptsView = li.inflate(R.layout.prompts, null);
		final EditText widthInput = (EditText) promptsView
				.findViewById(R.id.widthTextEdit);
		final EditText heightInput = (EditText) promptsView
				.findViewById(R.id.heightTextEdit);
		final TextView widthTitle = (TextView) promptsView
				.findViewById(R.id.widthTextView);
		final TextView heightTitle = (TextView) promptsView
				.findViewById(R.id.heightTextView);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);
		if (function) {
			if (currentShape != null) {
				editDimentionDialog(widthInput, heightInput, widthTitle,
						heightTitle, alertDialogBuilder);
			} else {
				Toast.makeText(getApplicationContext(), "Dimentions Error",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			savePhotoDialog(widthInput, heightInput, widthTitle, heightTitle,
					alertDialogBuilder);
		}

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it

		alertDialog.show();
	}

	private void editDimentionDialog(final EditText widthInput,
			final EditText heightInput, final TextView widthTitle,
			final TextView heightTitle, AlertDialog.Builder alertDialogBuilder) {
		if (currentShape instanceof Line) {
			widthTitle.setText("Set Length in Cm:");
			heightInput.setVisibility(View.INVISIBLE);
			heightTitle.setVisibility(View.INVISIBLE);
		} else if (currentShape instanceof Rectangle) {
			widthTitle.setText("Set Width in Cm:");
			heightInput.setVisibility(View.VISIBLE);
			heightTitle.setVisibility(View.VISIBLE);
		}

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						currentShape.setWidth(widthInput.getText() + " cm");
						if (currentShape instanceof Rectangle) {
							((Rectangle) currentShape).setHeight(heightInput
									.getText() + " cm");
						}
						repaint();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// dialog.cancel();
							}
						});
	}

	private void savePhotoDialog(final EditText widthInput,
			final EditText heightInput, final TextView widthTitle,
			final TextView heightTitle, AlertDialog.Builder alertDialogBuilder) {

		widthTitle.setText("Save file as");
		heightInput.setVisibility(View.INVISIBLE);
		heightTitle.setVisibility(View.INVISIBLE);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (!widthInput.getText().toString().isEmpty()) {
							savePhoto(widthInput.getText().toString());
						} else {
							Toast.makeText(getApplicationContext(),
									"Empty Field", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// dialog.cancel();
							}
						});
	}

	@Override
	public void colorChanged(int color) {
		currentShape.setColor(color);
		repaint();
	}

	final float[] getPointerCoords(ImageView view, MotionEvent e) {
		final int index = e.getActionIndex();
		final float[] coords = new float[] { e.getX(index), e.getY(index) };
		Matrix matrix = new Matrix();
		view.getImageMatrix().invert(matrix);
		matrix.postTranslate(view.getScrollX(), view.getScrollY());
		matrix.mapPoints(coords);
		return coords;
	}

	private void savePhoto(String fileName) {
		// TODO Check same file name

		if (currentShape != null) {
			shapes.add(currentShape);
			currentShape = null;
			repaint();
		}
		BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
		Bitmap btmp = drawable.getBitmap();
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(MainActivity.DIRECTORY_NAME),
				fileName + ".jpg");
		try {
			double area = calculateArea();
			if (area != -1) {

				file.createNewFile();
				FileOutputStream ostream = new FileOutputStream(file);
				btmp.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
				ostream.flush();
				ostream.close();
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						Uri.fromFile(file)));

				SaveJSON(fileName, file, area);

				Toast.makeText(getApplicationContext(), "Photo Saved",
						Toast.LENGTH_SHORT).show();

			} else {
				Toast.makeText(getApplicationContext(), "Error in Dimentions",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Saving Error",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}

	private void SaveJSON(String fileName, File file, double area) {

		String name = fileName;
		String path = file.getAbsolutePath();

		MeasuredPhoto measuredPhoto = new MeasuredPhoto(path, name, area);
		Gson gson = new Gson();

		File jsonFile = new File(getFilesDir(), JSON_FILE_NAME);
		if (!jsonFile.exists()) {
			try {

				MeasuredPhotosArray list = new MeasuredPhotosArray();
				list.add(measuredPhoto);

				String json = gson.toJson(list);

				saveFile(getApplicationContext(), json);
			} catch (Exception e) {
				// TODO: handle exception

				Toast.makeText(getApplicationContext(), "Error Create JSON",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			try {
				String json = load(getApplicationContext());

				MeasuredPhotosArray list = gson.fromJson(json,
						MeasuredPhotosArray.class);

				list.add(measuredPhoto);
				json = gson.toJson(list);
				saveFile(getApplicationContext(), json);
			} catch (Exception e) {
				// TODO: handle exception

				Toast.makeText(getApplicationContext(), "Error Load JSON",
						Toast.LENGTH_SHORT).show();
			}

		}

	}

	public boolean saveFile(Context context, String mytext) {
		Log.i("TESTE", "SAVE");
		try {
			FileOutputStream fos = context.openFileOutput(JSON_FILE_NAME,
					Context.MODE_PRIVATE);
			Writer out = new OutputStreamWriter(fos);
			out.write(mytext);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String load(Context context) {
		Log.i("TESTE", "FILE");
		try {
			FileInputStream fis = context.openFileInput(JSON_FILE_NAME);
			BufferedReader r = new BufferedReader(new InputStreamReader(fis));
			String line = r.readLine();
			r.close();
			return line;
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("TESTE", "FILE - false");
			return null;
		}
	}

	private double calculateArea() {
		double area = 0;

		for (Shape shape : shapes) {
			if (shape instanceof Rectangle) {
				try {
					Rectangle r = (Rectangle) shape;
					double i = Double.parseDouble(r.getWidth().split(" ")[0]);
					double ii = Double.parseDouble(r.getHeight().split(" ")[0]);
					area += i * ii;
				} catch (Exception e) {
					// TODO: handle exception
					return -1;
				}
			}
		}
		return area;
	}
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.measurino_editor, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	// if (id == R.id.action_settings) {
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }
}
