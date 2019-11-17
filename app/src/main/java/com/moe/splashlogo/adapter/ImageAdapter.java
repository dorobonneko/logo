package com.moe.splashlogo.adapter;
import android.widget.ListAdapter;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import com.moe.splashlogo.R;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter
{

	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		if(p2==null){
			p2=LayoutInflater.from(p3.getContext()).inflate(R.layout.image,p3,false);
		}
		ViewHolder vh=(ImageAdapter.ViewHolder) p2.getTag();
		if(vh==null){
			p2.setTag(vh=onCreateViewHolder(p2));
		}
		return p2;
	}
	public ViewHolder onCreateViewHolder(View v){
		return new ViewHolder(v);
	}
	public static class ViewHolder{
		 TextView hint;
		 CheckBox check;
		 ImageView img;
		ViewHolder(View v){
			img=v.findViewById(R.id.img);
			check=v.findViewById(R.id.check);
			hint=v.findViewById(R.id.hint);
		}
	}
}
