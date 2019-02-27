package com.project.myapp.sharing;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class GetOption {
	static DisplayImageOptions options;
	static DisplayImageOptions getFullOption;
	static ImageLoaderConfiguration config;
	static int current_dimen = 0;

	public static DisplayImageOptions getOption() {
		if (options == null) {
			options = new DisplayImageOptions.Builder().cacheOnDisk(true)
					.cacheInMemory(true).considerExifParams(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return options;
	}

	public static DisplayImageOptions getFullOption(int dimen) {
		if (getFullOption == null || current_dimen != dimen) {
			current_dimen = dimen;
			getFullOption = new DisplayImageOptions.Builder().cacheOnDisk(true)
					.cacheInMemory(true)
					.displayer(new RoundedBitmapDisplayer(dimen))
					.considerExifParams(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return getFullOption;
	}

	public static boolean checkNotNull(String url) {
		if (url == null)
			return false;
		else if (url.equals(""))
			return false;
		else
			return true;
	}

	public static ImageLoaderConfiguration getConfig(Context context) {
		if (config == null) {
			config = new ImageLoaderConfiguration.Builder(context).memoryCache(
					new WeakMemoryCache()).build();
		}
		return config;
	}
}
