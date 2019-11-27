package com.moe.splashlogo.adapter;
import android.view.*;
import com.moe.splashlogo.*;

import android.graphics.BitmapFactory;
import com.moe.splashlogo.entity.Logo;
import java.io.IOException;
import android.graphics.Bitmap;

public class LogoImageAdapter extends ImageAdapter
{
	private Logo logo;
	public LogoImageAdapter(Logo config)
	{
		this.logo = config;
	}

	@Override
	public int getCount()
	{
		return logo.getCount();
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return logo.getImage(p1);
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		// TODO: Implement this method
		View v=super.getView(p1, p2, p3);
		ViewHolder vh=(ViewHolder) v.getTag();
		Logo.Image image=logo.getImage(p1);
		Bitmap bitmap=image.getBitmap();
		if (bitmap == null)
			vh.img.setImageResource(R.drawable.plus);
		else
		{
			vh.img.setImageBitmap(bitmap);
			vh.hint.setText(image.getBitmap().getWidth() + "x" + image.getBitmap().getHeight());
		}
		return v;
	}

	@Override
	public int getItemViewType(int position)
	{
		return position;
	}

	@Override
	public ImageAdapter.ViewHolder onCreateViewHolder(View v)
	{
		return new ViewHolder(v);
	}

	public static class ViewHolder extends ImageAdapter.ViewHolder
	{
		ViewHolder(View v)
		{
			super(v);
			check.setVisibility(View.INVISIBLE);
		}
	}
}
