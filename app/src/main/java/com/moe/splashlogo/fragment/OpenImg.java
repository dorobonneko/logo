package com.moe.splashlogo.fragment;
import android.app.Fragment;
import android.os.Bundle;
import android.net.Uri;
import java.io.InputStream;
import java.io.FileNotFoundException;
import com.moe.splashlogo.entity.Logo;

public class OpenImg extends Fragment
{

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		Bundle bundle=getArguments();
		Uri file=bundle.getParcelable("data");
		try
		{
			InputStream input=getContext().getContentResolver().openInputStream(file);
			Logo logo=new Logo(input,Logo.HEADER_OFFSET);
		}
		catch (Exception e)
		{}
	}
	
}
