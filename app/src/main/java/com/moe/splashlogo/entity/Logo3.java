package com.moe.splashlogo.entity;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import com.moe.splashlogo.util.Arrays;
import java.io.ByteArrayInputStream;
import com.moe.splashlogo.io.ArrayInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.moe.splashlogo.io.ArrayOutputStream;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import com.moe.splashlogo.util.BmpUtil;

public class Logo3 extends Logo
{
	private byte[] magic;
	private ArrayInputStream ais;
	private int fileSize;
	public Logo3(InputStream input) throws IOException{
		ByteArrayOutputStream baos=new ArrayOutputStream();
		int len=-1;
		byte[] buff=new byte[128*1024];
		while((len=input.read(buff))!=-1){
			baos.write(buff,0,len);
		}
		fileSize=baos.size();
		ais=new ArrayInputStream(((ArrayOutputStream)baos).getBuff());
		baos=new ByteArrayOutputStream();
		System.gc();
		int index=ais.indexOf(HEADER,0x4000);
		if(index==-1)throw new IllegalArgumentException("it's not logo img");
		baos.write(HEADER);
		buff=new byte[8];
		ais.seek(0x4000+8);
		do{
			ais.readFully(buff);
			if(Arrays.startsWith(buff,new byte[8]))
				break;
			baos.write(buff);
		}while(true);
		magic=baos.toByteArray();
		index+=magic.length;
		buff=new byte[4];
		while((index=ais.indexOf(new byte[]{0x42,0x4D},index))!=-1){
			//find BM
			//判断是否是图片
			ais.seek(index+2);
			ais.readFully(buff);
			int size=(buff[3]&0xff)<<24|(buff[2]&0xff)<<16|(buff[1]&0xff)<<8|(buff[0]&0xff);
			if(size<0||size+54+index>ais.size())continue;
			BitmapFactory.Options bo=new BitmapFactory.Options();
			bo.inJustDecodeBounds=true;
			BitmapFactory.decodeByteArray(ais.sourceBytes(),index,54+size,bo);
			if(bo.outWidth==0||bo.outHeight==0)
				continue;
				//保存数据
				Image image=new Logo3.Image();
				image.offset=index;
				image.length=size+54;
				addImage(image);
				index+=image.length;
			if(getCount()-1>0){
				Logo.Image old=getImage(getCount()-2);
				old.size=image.offset-old.offset;
			}
		}
		Logo.Image old=getImage(getCount()-1);
		old.size=fileSize-old.offset;
		/*long time=System.currentTimeMillis();
		Hex hex=Arrays.formatToHex(input);
		time=System.currentTimeMillis()-time;
		int index=hex.indexOf(Arrays.bytes2Hex(HEADER).toString());
		if(index==-1){
			throw new IllegalStateException("it's not logo file");
		}
		index+=HEADER.length*2;
		StringBuilder magic=new StringBuilder();
		do{
		String str=hex.substring(index,index+=16);
		if(str.equals("0000000000000000"))break;
		magic.append(str);
		}while(true);
		this.magic=Arrays.hexToBytes(magic.toString());
		while(true){//BM
		index=hex.indexOf("424D",index);
		}*/
	}
	@Override
	public boolean save(File file)
	{
		RandomAccessFile save=null;
		try
		{
			save = new RandomAccessFile(file.getAbsolutePath(), "rw");
			save.setLength(fileSize);
			save.seek(HEADER_OFFSET);
			save.write(magic);
			for(Logo.Image image:images()){
				save.seek(image.offset);
				image.write(save);
			}
			return true;
		}
		catch (Exception e)
		{
			file.delete();
		}finally{
			try
			{
				if(save!=null)
				save.close();
			}
			catch (IOException e)
			{}
		}
		return false;
	}
	class Image extends Logo.Image
	{
		public int length;
		@Override
		public synchronized Bitmap getBitmap()
		{
			//重写获取图片方法，改为从ArrayInputStream中获取
			if(bitmap==null){
				bitmap=BitmapFactory.decodeByteArray(ais.sourceBytes(),offset,length);
			}
			return bitmap;
		}

		@Override
		void write(RandomAccessFile save) throws IOException
		{
			save.write(ais.sourceBytes(),offset,length);
		}
		
	}
}
