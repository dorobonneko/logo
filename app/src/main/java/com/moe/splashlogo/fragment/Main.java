package com.moe.splashlogo.fragment;
import android.app.*;
import android.view.*;
import android.widget.*;

import android.content.Intent;
import android.os.Bundle;
import com.moe.splashlogo.R;
import com.moe.splashlogo.util.FileUtil;

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
	public void onActivityResult(int requestCode, int resultCode, Intent data)
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
						Bundle b=new Bundle();
						b.putParcelable("data",FileUtil.getFile(getContext(),data.getData()));
						Fragment f=new OpenImg();
						f.setArguments(b);
						getFragmentManager().beginTransaction().replace(android.R.id.content,f).addToBackStack(null).commitAllowingStateLoss();

					}
					break;
			}
	}
	
}
