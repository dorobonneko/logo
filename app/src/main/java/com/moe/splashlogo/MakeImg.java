package com.moe.splashlogo;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import android.content.res.AssetManager;
import java.io.InputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import android.net.Uri;
import android.content.Context;
import java.io.FileOutputStream;

public class MakeImg
{
	private final static int offset0 = 0x4000,offset1 = 0x5000,offset2 = 0x740000,offset3 = 0xD2F000,offset4 = 0x146A000;
	private final static byte[] magic=new byte[]{0x4C, 0x4F, 0x47, 0x4F, 0x21, 0x21, 0x21, 0x21, 0x05, 0x00, 0x00, 0x00,0x3B, 0x07, 0x00, 0x00, 0x40, 0x07, 0x00, 0x00, (byte)0xef, 0x05, 0x00, 0x00,0x2F, 0x0D, 0x00, 0x00, 0x3B, 0x07, 0x00, 0x00, 0x6A, 0x14, 0x00, 0x00,(byte)0xef, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	/*图1 7581696个字节
	 图2 7581696个字节*/
	 public static void test(Context c,Uri logo1){
		 try
		 {
			 Bitmap b=BitmapFactory.decodeStream(c.getContentResolver().openInputStream(logo1));
			BmpUtil.save2Bmp(b,new FileOutputStream("/sdcard/test.bmp"));
			 b.recycle();
		 }
		 catch (Exception e)
		 {}
	 }
	public static boolean createImg(Context am, File savePath, Uri logo1, Uri logo2, boolean force)
	{
		if (logo1 == null&&logo2 == null)
		{
			return false;
		}
		RandomAccessFile img=null;
		File saveFile=new File(savePath, "logo.img");
		if(saveFile.exists())saveFile.delete();
		try
		{
			img = new RandomAccessFile(saveFile, "rw");
			img.setLength(0xDCCFFF);
			img.seek(offset0);
			img.write(magic);
			if (logo1 != null)
			{
				img.seek(offset1);
				//图1
				Bitmap bit=BitmapFactory.decodeStream(am.getContentResolver().openInputStream(logo1));
				if (bit == null)return false;
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				try
				{
					BmpUtil.save2Bmp(bit,baos);
					//new BitmapEX(bit).saveAsBMP(baos);
					if (!force && baos.size() > 7581696)return false;
					img.write(baos.toByteArray());
				}
				catch (Exception e)
				{}
				finally
				{
					baos.close();
					bit.recycle();
				}
			}
			img.seek(offset2);
			InputStream is=am.getAssets().open("02.bmp");
			write(img, is);
			if (logo2 != null)
			{
				img.seek(offset3);
				//图2
				Bitmap bit=BitmapFactory.decodeStream(am.getContentResolver().openInputStream(logo2));
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				try
				{
					BmpUtil.save2Bmp(bit,baos);
					//new BitmapEX(bit).saveAsBMP(baos);
					if (!force && baos.size() > 7581696)return false;

					img.write(baos.toByteArray());
				}
				catch (Exception e)
				{}
				finally
				{
					baos.close();
					bit.recycle();
				}
			}
			img.seek(offset4);
			is = am.getAssets().open("04.bmp");
			write(img, is);
			return true;
		}
		catch (Exception e)
		{
			try
			{
				if (img != null)img.close();
			}
			catch (IOException ee)
			{}
			saveFile.delete();
		}
		finally
		{
			try
			{
				if (img != null)img.close();
			}
			catch (IOException e)
			{}
		}
		return false;
	}
	private static void write(RandomAccessFile raf, InputStream is) throws IOException
	{
		byte[] buff=new byte[10240];
		int len=-1;
		while ((len = is.read(buff)) != -1)
		{
			raf.write(buff, 0, len);
		}
		is.close();
	}
}
