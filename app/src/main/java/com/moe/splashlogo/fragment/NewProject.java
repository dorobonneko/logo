package com.moe.splashlogo.fragment;
import android.app.*;
import android.view.*;
import android.widget.*;

import android.content.Intent;
import android.os.Bundle;
import com.moe.splashlogo.R;
import android.net.Uri;
import com.moe.splashlogo.LogoConfig;
import java.io.IOException;
import com.moe.splashlogo.adapter.ImageAdapter;
import com.moe.splashlogo.adapter.ConfigImageAdapter;
import com.moe.splashlogo.util.FileUtil;
import android.graphics.BitmapFactory;
import java.io.FileNotFoundException;
import com.moe.splashlogo.util.LogoImgUtil;
import java.io.File;
import android.os.Environment;

public class NewProject extends Fragment implements GridView.OnItemClickListener
{
	private LogoConfig lc;
	private TextView product;
	private GridView mGridView;
	private ConfigImageAdapter adapter;
	private int click;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		return inflater.inflate(R.layout.new_project,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		product=view.findViewById(R.id.product);
		mGridView=view.findViewById(R.id.gridview);
		mGridView.setOnItemClickListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
		Uri file=getArguments().getParcelable("data");
		try
		{
			lc = new LogoConfig(file.getPath());
		}
		catch (IOException e)
		{}
		if(lc==null){
			//toast
			Toast.makeText(getContext(),"配置文件错误",Toast.LENGTH_SHORT).show();
			getFragmentManager().popBackStack();
		}else{
			product.setText(lc.product);
			mGridView.setAdapter(adapter=new ConfigImageAdapter(lc));
		}
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
						final boolean result=LogoImgUtil.makeImg(getContext(),lc,new File(folder,lc.product+"_logo.img"));
						getActivity().runOnUiThread(new Runnable(){

								@Override
								public void run()
								{
									Toast.makeText(getContext(),result?"成功":"失败",Toast.LENGTH_SHORT).show();
									((ViewFlipper)item.getActionView()).setDisplayedChild(0);
									
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
		LogoConfig.Offset offset=(LogoConfig.Offset) adapter.getItem(p3);
		if(offset.type==LogoConfig.Offset.Type.REF){
			click=p3;
			startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),8448);
		}else if(offset.type==LogoConfig.Offset.Type.FILE){
			//new AlertDialog.Builder(getActivity()).setMessage().show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK&&requestCode==8448){
			Uri file=data.getData();
			LogoConfig.RefOffset offset=(LogoConfig.RefOffset) adapter.getItem(click);
			offset.uri=file;
			try
			{
				try{if(offset.bitmap!=null)offset.bitmap.recycle();}catch(Exception e){}
				offset.bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(file));
			}
			catch (FileNotFoundException e)
			{}
			adapter.notifyDataSetChanged();
		}
	}

	
}
