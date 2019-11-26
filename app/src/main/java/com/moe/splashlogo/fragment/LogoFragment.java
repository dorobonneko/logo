package com.moe.splashlogo.fragment;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.moe.splashlogo.*;
import java.io.*;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.moe.splashlogo.adapter.ConfigImageAdapter;
import com.moe.splashlogo.util.LogoImgUtil;
import com.moe.splashlogo.entity.Logo;
import com.moe.splashlogo.adapter.LogoImageAdapter;
import android.graphics.Bitmap;

public class LogoFragment extends Fragment implements GridView.OnItemClickListener
{
	private GridView mGridView;
	private LogoImageAdapter adapter;
	private int click;
	private Logo logo;
	public void setLogo(Logo logo){
		this.logo=logo;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		return inflater.inflate(R.layout.project,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		mGridView=view.findViewById(R.id.gridview);
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
		mGridView.setAdapter(adapter=new LogoImageAdapter(logo));
		adapter.notifyDataSetChanged();
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
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.save:
				((ViewFlipper)item.getActionView()).setDisplayedChild(1);
				new Thread(){
					public void run(){
						File folder=new File(Environment.getExternalStorageDirectory(),"logo");
						if(!folder.isDirectory())folder.mkdirs();
						final File file=new File(folder,"logo_new.img");
						final boolean result=logo.save(file);
						
						getActivity().runOnUiThread(new Runnable(){

								@Override
								public void run()
								{
									((ViewFlipper)item.getActionView()).setDisplayedChild(0);
									if(result){
										Toast.makeText(getContext(),file.getAbsolutePath(),Toast.LENGTH_LONG).show();
									}else{
										Toast.makeText(getContext(),"保存失败",Toast.LENGTH_SHORT).show();
									}
								}
							});
					}
				}.start();
				break;
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		click=p3;
		startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),8448);
		}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK&&requestCode==8448){
			BitmapFactory.Options bo=new BitmapFactory.Options();
			bo.inJustDecodeBounds=true;
			try
			{
				BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(data.getData()), null, bo);
				if(bo.outWidth*bo.outHeight*3+54>logo.getImage(click).size)
					throw new IllegalArgumentException();
					logo.setImage(click,new Logo.Image(logo.getImage(click),BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(data.getData()))));
					adapter.notifyDataSetChanged();
			}catch(IllegalArgumentException e){
				Toast.makeText(getContext(),"图片尺寸过大",Toast.LENGTH_SHORT).show();
			}
			catch (Exception e)
			{
				Toast.makeText(getContext(),"错误的图片",Toast.LENGTH_SHORT).show();
			}

		}
	}
}
