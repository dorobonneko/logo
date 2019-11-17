package com.moe.splashlogo.adapter;
import com.moe.splashlogo.LogoConfig;
import android.view.View;
import android.view.ViewGroup;
import com.moe.splashlogo.adapter.ImageAdapter.ViewHolder;
import android.graphics.BitmapFactory;
import java.io.IOException;
import com.moe.splashlogo.R;

public class ConfigImageAdapter extends ImageAdapter
{
	private LogoConfig config;
	public ConfigImageAdapter(LogoConfig config){
		this.config=config;
	}

	@Override
	public int getCount()
	{
		return config.offset.length-1;
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return config.offset[p1+1];
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		// TODO: Implement this method
		View v=super.getView(p1, p2, p3);
		ViewHolder vh=(ViewHolder) v.getTag();
		LogoConfig.Offset offset=config.offset[p1+1];
		vh.hint.setHint(offset.type==LogoConfig.Offset.Type.REF?offset.data:null);
		try
		{
			if (offset.type == LogoConfig.Offset.Type.FILE)
				vh.img.setImageBitmap(BitmapFactory.decodeStream(config.getOffset(offset)));
			else if(offset.type==LogoConfig.RefOffset.Type.REF&&((LogoConfig.RefOffset)offset).uri!=null)
				vh.img.setImageBitmap(((LogoConfig.RefOffset)offset).bitmap);
			else
				throw new IOException();
		}
		catch (IOException e)
		{
			vh.img.setImageResource(R.drawable.plus);
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
	
	public static class ViewHolder extends ImageAdapter.ViewHolder{
		ViewHolder(View v){
			super(v);
			check.setVisibility(View.INVISIBLE);
		}
	}
}
