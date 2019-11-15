package com.moe.splashlogo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ViewFlipper;
import android.widget.Toast;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.view.MenuItem;
import com.moe.splashlogo.fragment.Main;

public class MainActivity extends Activity 
{
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		if(checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},284);
			else
			{
				init();
        }
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode==284&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
			init();
			else
			finish();
	}


private void init(){
	getFragmentManager().beginTransaction().add(android.R.id.content,new Main()).commitAllowingStateLoss();
	
}
	
	

	
}
