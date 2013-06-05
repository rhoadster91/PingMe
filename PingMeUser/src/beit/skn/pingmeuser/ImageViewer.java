package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageViewer extends Activity
{
	private static Bitmap bitmap;
	private static ImageView imgView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		setContentView(R.layout.imageviewer);
		super.onCreate(savedInstanceState);
		PushableMessage m = (PushableMessage)getIntent().getSerializableExtra("image message");
		byte []byteArray = (byte [])m.getMessageContent();
		bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		imgView = (ImageView)findViewById(R.id.imgViewBitmap);
		imgView.setImageBitmap(bitmap);
		imgView.setScaleType(ScaleType.FIT_XY);
	}
}
