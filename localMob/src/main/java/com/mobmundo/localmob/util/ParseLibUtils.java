package com.mobmundo.localmob.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.parse.ParseFile;

public class ParseLibUtils {
	
	public static ParseFile convertImageViewToFile(ImageView imgView) {
		imgView.buildDrawingCache();
		Bitmap bm = imgView.getDrawingCache();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return new ParseFile(stream.toByteArray());
	}
}
