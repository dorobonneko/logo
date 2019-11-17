package com.moe.splashlogo.util;
import com.moe.splashlogo.LogoConfig;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import android.graphics.BitmapFactory;
import android.content.Context;;

public class LogoImgUtil
{
	public static boolean makeImg(Context context,LogoConfig config,File save){
		if(config.offset==null)return false;
		for(LogoConfig.Offset offset:config.offset){
			if(offset instanceof LogoConfig.RefOffset&&((LogoConfig.RefOffset)offset).uri==null)
				return false;
		}
		RandomAccessFile saveFile=null;
		try
		{
			saveFile=new RandomAccessFile(save, "rw");
			if(config.size!=0)
				saveFile.setLength(config.size);
			for (LogoConfig.Offset offset:config.offset)
			{
				saveFile.seek(offset.offset);
				switch(offset.type){
					case FILE:
						InputStream is=config.getOffset(offset);
						int len=-1;
						byte[] buff=new byte[8092];
						while((len=is.read(buff))!=-1)
							saveFile.write(buff,0,len);
						break;
					case REF:
						LogoConfig.RefOffset ref=(LogoConfig.RefOffset) offset;
						if(ref.bitmap==null)ref.bitmap=BitmapFactory.decodeStream(context.getContentResolver().openInputStream(ref.uri));
						if((config.width>0&&ref.bitmap.getWidth()>config.width)||(config.height>0&&ref.bitmap.getHeight()>config.height))
							return false;
						BmpUtil.save2Bmp(ref.bitmap,saveFile);
						break;
					case BYTE:
						saveFile.write(byteArray(offset.data));
						break;
				}
			}
			return true;
		}
		catch (IOException e)
		{
			try
			{
				saveFile.close();
			}
			catch (IOException ee)
			{}
			save.delete();
		}finally{
			try
			{
				saveFile.close();
			}
			catch (IOException e)
			{}
		}
		return false;
	}
	public static byte[] byteArray(String str){
		String[] data=str.split(",");
		byte[] buffer=new byte[data.length];
		for(int i=0;i<buffer.length;i++){
			buffer[i]=(byte)(Integer.parseUnsignedInt(data[i].trim().substring(2),16)&0xff);
		}
		return buffer;
	}
}
