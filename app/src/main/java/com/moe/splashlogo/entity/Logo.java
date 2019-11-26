package com.moe.splashlogo.entity;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import com.moe.splashlogo.util.Arrays;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import com.moe.splashlogo.util.BmpUtil;

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
	public Image[] getImages(){
		return images.toArray(new Image[0]);
	}
	public Image getImage(int index){
		return images.get(index);
	}
	public void setImage(int index,Image image){
		images.set(index,image);
	}
	public int getCount(){
		return images.size();
	}
	public boolean save(File file){
		RandomAccessFile save=null;
		try
		{
			save=new RandomAccessFile(file.getAbsolutePath(), "rw");
			save.seek(HEADER_OFFSET);
			save.write(HEADER);
			for(Image image:images){
				save.write(getBytes(image.offset));
				save.write(getBytes(image.size));
				long point=save.getFilePointer();
				save.seek(image.offset);
				image.write(save);
				save.seek(point);
			}
		}
		catch (Exception e)
		{
			return false;
		}finally{
			try
			{
				save.close();
			}
			catch (IOException e)
			{}
		}
		return true;
	}
	private byte[] getBytes(int value){
		String str=Integer.toHexString(value);
		str=str.substring(0,str.length()-3);
		value=Integer.parseInt(str,16);
		byte[] data=new byte[]{(byte)value,(byte)(value>>8),(byte)(value>>16),(byte)(value>>24)};
//		if(str.length()%2!=0)
//			str="0"+str;
//		byte[] data=new byte[4];
//		for(int i=0,n=0;str.length()-(i+1)*2<0;i++,n++){
//			String hex=str.substring(str.length()-(i+1)*2,str.length()-i*2);
//			data[n]=Byte.parseByte(hex,16);
//		}
		return data;
	}
	public static class Image{
		public int offset,size;
		public byte[] header=new byte[14],info=new byte[40],body;
		private Bitmap bitmap;
		public Image(Image old,Bitmap bitmap){
			offset=old.offset;
			size=old.size;
			if(bitmap.getWidth()*bitmap.getHeight()*3+54>size)throw new IllegalArgumentException();
			this.bitmap=bitmap;
		}
		public Image(){}
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
		private void write(RandomAccessFile save) throws IOException{
			if(body!=null){
				save.write(header);
				save.write(info);
				save.write(body);
			}else if(bitmap!=null){
				body=new byte[bitmap.getWidth()*bitmap.getHeight()*3];
				if(BmpUtil.save2Bmp(bitmap,this)){
					save.write(header);
					save.write(info);
					save.write(body);
				}
			}
		}
	}
}
