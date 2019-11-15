package com.moe.splashlogo;

import android.widget.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import java.io.File;
import android.os.Environment;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;

public class MainActivity extends Activity implements View.OnClickListener 
{
	private EditText logo1;
	private Button create;
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==4644&&resultCode==RESULT_OK){
			logo1.setText(data.getDataString());
		}
	}
	
private void init(){
	setContentView(R.layout.main);
	logo1=findViewById(R.id.logo1);
	logo1.setOnClickListener(this);
	create=findViewById(R.id.create);
	create.setOnClickListener(this);
	
}
	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.logo1:
				startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),4644);
				break;
			case R.id.create:
				if(logo1.getText().length()>0){
				p1.setEnabled(false);
				final Uri logo1path=Uri.parse(logo1.getText().toString());
				new Thread(){
					public void run(){
						if(!MakeImg.createImg(getApplicationContext(),Environment.getExternalStorageDirectory(),logo1path,logo1path,false))
							runOnUiThread(new Runnable(){

									@Override
									public void run()
									{
										create.setEnabled(true);
										Toast.makeText(getApplicationContext(),"创建失败",Toast.LENGTH_SHORT).show();
									}
								});
							else
								//MakeImg.test(getApplicationContext(),logo1path);
							runOnUiThread(new Runnable(){

									@Override
									public void run()
									{
										create.setEnabled(true);
									}
								});
					}
				}.start();
				}
				break;
		}
	}

	
}
