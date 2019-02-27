package com.project.myapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.TypedValue;

public class ImageLoadingUtils {
    private Context context;
    public Bitmap icon;

    public ImageLoadingUtils(Context context) {
        this.context = context;
        this.icon = BitmapFactory.decodeResource(context.getResources(), C0256R.drawable.ic_launcher);
    }

    public int convertDipToPixels(float dips) {
        return (int) TypedValue.applyDimension(1, dips, this.context.getResources().getDisplayMetrics());
    }

    public int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round(((float) height) / ((float) (((double) reqHeight) + 0.1d)));
            int widthRatio = Math.round(((float) width) / ((float) (((double) reqWidth) + 0.1d)));
            if (heightRatio < widthRatio) {
                inSampleSize = heightRatio;
            } else {
                inSampleSize = widthRatio;
            }
        }
        while (((double) ((float) (width * height))) / (((double) (inSampleSize * inSampleSize)) + 0.1d) > ((double) ((float) ((reqWidth * reqHeight) * 2)))) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    @SuppressWarnings("deprecation")
	public Bitmap decodeBitmapFromPath(String filePath) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(150.0f), convertDipToPixels(200.0f));
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
