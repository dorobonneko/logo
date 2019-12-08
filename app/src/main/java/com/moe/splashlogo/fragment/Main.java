package com.moe.splashlogo.fragment;
import android.app.*;
import android.view.*;
import android.widget.*;

import android.content.Intent;
import android.os.Bundle;
import com.moe.splashlogo.R;
import com.moe.splashlogo.util.FileUtil;
import java.io.InputStream;
import java.io.IOException;
import com.moe.splashlogo.entity.Logo;
import com.moe.splashlogo.entity.Logo3;

public class Main extends Fragment implements View.OnClickListener
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		return inflater.inflate(R.layout.main,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.new_project).setOnClickListener(this);
		view.findViewById(R.id.open_img).setOnClickListener(this);
	}

	
	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.new_project:{
				Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("application/zip");
				startActivityForResult(intent,7467);
				}break;
			case R.id.open_img:{
				Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");
				startActivityForResult(intent,9467);
				}
				break;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK)
			switch(requestCode){
				case 7467:{
						Bundle b=new Bundle();
						b.putParcelable("data",FileUtil.getFile(getContext(),data.getData()));
						Fragment f=new NewProject();
						f.setArguments(b);
						getFragmentManager().beginTransaction().replace(android.R.id.content,f).addToBackStack(null).commitAllowingStateLoss();
					}break;
				case 9467:{
					ProgressDialog pd=new ProgressDialog(getActivity());
					pd.setProgressStyle(android.R.style.Widget_DeviceDefault_ProgressBar_Large);
						new Thread(){
							public void run(){
								InputStream input=null;
								try{
									input=getContext().getContentResolver().openInputStream(data.getData());
									Logo logo=null;
									try{
										logo=Logo.decode1(input,Logo.HEADER_OFFSET);
										}catch(Exception e){
											input.close();
											input=getContext().getContentResolver().openInputStream(data.getData());
											try{
												logo=Logo.decode2(input,Logo.HEADER_OFFSET);
												}catch(Exception ee){
													input.close();
													logo=new Logo3(getContext().getContentResolver().openInputStream(data.getData()));
													
												}
										}
									LogoFragment f=new LogoFragment();
									f.setLogo(logo);
									getFragmentManager().beginTransaction().replace(android.R.id.content,f).addToBackStack(null).commitAllowingStateLoss();

									
								}catch(Exception e){
									getActivity().runOnUiThread(new Runnable(){

											@Override
											public void run()
											{
												Toast.makeText(getContext(),"解析失败",Toast.LENGTH_SHORT).show();
											}
										});
								}finally{
									try
									{
										if (input != null)
											input.close();
									}
									catch (IOException e)
									{}
								}
							}
						}.start();
						}
					break;
			}
	}
	
}
