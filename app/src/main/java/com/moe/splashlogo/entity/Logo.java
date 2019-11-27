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
import java.util.Iterator;
import java.util.List;

public abstract  class Logo
{
	public static final byte[] HEADER=new byte[]{'L','O','G','O','!','!','!','!'};
	public static final int HEADER_OFFSET=0x4000;
	private ArrayList<Image> images=new ArrayList<>();
	public static Logo decode1(InputStream input,int offset) throws IOException{
		return new Logo1(input,offset);
	}
	public static Logo decode2(InputStream input,int offset) throws IOException{
		return new Logo2(input,offset);
		}
	protected void addImage(Image i){
		images.add(i);
	}
	protected List<Image> images(){
		return images;
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
	public abstract boolean save(File file);
	public void recycle(){
		Iterator<Image> iterator=images.iterator();
		while(iterator.hasNext()){
			Image image=iterator.next();
			iterator.remove();
			if(image.bitmap!=null)
				image.bitmap.recycle();
			image.header=null;
			image.info=null;
			image.body=null;
		}
	}
	protected byte[] getBytes(int value){
		value=value>>>12;
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
		protected Bitmap bitmap;
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
		void write(RandomAccessFile save) throws IOException{
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
