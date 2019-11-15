package com.moe.splashlogo.fragment;
import android.app.*;
import android.view.*;
import android.widget.*;

import android.content.Intent;
import android.os.Bundle;
import com.moe.splashlogo.R;

public class NewProject extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
	}

	
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflate)
	{
		inflate.inflate(R.menu.menu,menu);
		final MenuItem save=menu.findItem(R.id.save);
		ViewFlipper vf=(ViewFlipper) save.getActionView();
		vf.setPadding(0,0,getResources().getDimensionPixelSize(R.dimen.margin),0);
		ImageView check=null;
		vf.addView(check=new ImageView(getActivity(),null,0,R.style.Borderless));
		vf.addView(new ProgressBar(getActivity(),null,0,android.R.style.Widget_DeviceDefault_ProgressBar_Small));
		check.setImageResource(R.drawable.check);
		check.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		check.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					onOptionsItemSelected(save);
				}
			});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.save:
				((ViewFlipper)item.getActionView()).setDisplayedChild(1);
				break;
			case R.id.new_project:
				break;
			case R.id.open_img:
				break;
		}
		return true;
	}
}
