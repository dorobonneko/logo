package com.moe.splashlogo.entity;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import com.moe.splashlogo.util.Arrays;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayInputStream;

public class Logo
{
	public static final byte[] HEADER=new byte[]{'L','O','G','O','!','!','!','!'};
	public static final int HEADER_OFFSET=0x4000;
	private ArrayList<Image> images=new ArrayList<>();
	public Logo(InputStream dis,int offset) throws IOException{
		dis.skip(offset);
		int skip=HEADER_OFFSET;
		byte[] data=new byte[8];
		Arrays.readFully(dis,data);
		skip+=data.length;
		if(!Arrays.startsWith(data,HEADER))throw new IllegalStateException("it's not logo.img");
		do{
			Arrays.readFully(dis,data);
			skip+=data.length;
		if(Arrays.startsWith(data,new byte[8]))break;
			Image image=new Image();
			image.offset=Integer.parseInt(Integer.toHexString(data[3]&0xff)+Integer.toHexString(data[2]&0xff)+Integer.toHexString(data[1]&0xff)+Integer.toHexString(data[0]&0xff)+"000",16);
			image.size=Integer.parseInt(Integer.toHexString(data[7]&0xff)+Integer.toHexString(data[6]&0xff)+Integer.toHexString(data[5]&0xff)+Integer.toHexString(data[4]&0xff)+"000",16);
			images.add(image);
		}while(true);
		for(Image image:images){
			skip+=dis.skip(image.offset-skip);
			Arrays.readFully(dis,image.header);
			skip+=image.header.length;
			int size=(image.header[2]&0xff)|((image.header[3]&0xff)<<8)|((image.header[4]&0xff)<<16)|((image.header[5]&0xff)<<24);
			Arrays.readFully(dis,image.info);
			skip+=image.info.length;
			image.body=new byte[size];
			Arrays.readFully(dis,image.body);
			skip+=size;
		}
	}
	public static class Image{
		public int offset,size;
		public byte[] header=new byte[14],info=new byte[40],body;
		private Bitmap bitmap;
		public synchronized Bitmap getBitmap(){
			if(bitmap==null){
				byte[] buff=new byte[header.length+info.length+body.length];
				System.arraycopy(header,0,buff,0,header.length);
				System.arraycopy(info,0,buff,header.length,info.length);
				System.arraycopy(body,0,buff,header.length+info.length,body.length);
				bitmap=BitmapFactory.decodeByteArray(buff,0,buff.length);
				buff=null;
			}
			return bitmap;
		}
	}
}
