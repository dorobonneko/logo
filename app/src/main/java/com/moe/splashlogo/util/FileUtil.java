package com.moe.splashlogo.util;
import android.net.Uri;
import android.content.Context;
import android.os.Environment;
import java.io.File;
import android.database.Cursor;
import android.text.TextUtils;
import android.provider.MediaStore;

public class FileUtil
{
	public static Uri getFile(Context context,Uri uri){
		switch(uri.getScheme()){
			case "file":
				return uri;
			case "content":
				Cursor cursor=context.getContentResolver().query(uri,new String[]{MediaStore.MediaColumns.DATA},null,null,null);
				if(cursor!=null){
					try{
					if(cursor.moveToNext()){
						String path=cursor.getString(0);
						if(!TextUtils.isEmpty(path)){
							return Uri.fromFile(new File(path));
						}
					}}catch(Exception e){}
					finally{
					cursor.close();
					}
				}
				String path=uri.getPath();
					return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),path.substring(path.indexOf(":",1)+1)));
				
		}
		return null;
	}
}
