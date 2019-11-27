package com.moe.splashlogo.entity;

import java.io.*;

import android.media.Image;
import com.moe.splashlogo.util.Arrays;

public class Logo1 extends Logo
{
	public Logo1(InputStream dis,int offset) throws IOException{
		dis.skip(offset);
		int skip=HEADER_OFFSET;
		byte[] data=new byte[8];
		Arrays.readFully(dis,data);
		skip+=data.length;
		if(!Arrays.startsWith(data,HEADER))throw new IllegalStateException("it's not logo.img");
		int oldoffset=0;
		do{
			Arrays.readFully(dis,data);
			skip+=data.length;
			if(Arrays.startsWith(data,new byte[8]))break;
			Image image=new Image();
			image.offset=((data[3]&0xff)<<24|(data[2]&0xff)<<16|(data[1]&0xff)<<8|(data[0]&0xff))<<12;
			if(image.offset<oldoffset)throw new IllegalStateException();
			oldoffset=image.offset;
			image.size=((data[7]&0xff)<<24|(data[6]&0xff)<<16|(data[5]&0xff)<<8|(data[4]&0xff))<<12;
			addImage(image);
		}while(true);
		for(Image image:images()){
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
	public boolean save(File file){
		RandomAccessFile save=null;
		try
		{
			save=new RandomAccessFile(file.getAbsolutePath(), "rw");
			save.seek(HEADER_OFFSET);
			save.write(HEADER);
			for(Image image:images()){
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
}
