package com.moe.splashlogo.entity;

import java.io.*;

import com.moe.splashlogo.util.Arrays;

public class Logo2 extends Logo
{
	public Logo2(InputStream dis,int offset) throws IOException{
		dis.skip(offset);
		int skip=HEADER_OFFSET;
		byte[] data=new byte[8];
		Arrays.readFully(dis,data);
		skip+=data.length;
		if(!Arrays.startsWith(data,HEADER))throw new IllegalStateException("it's not logo.img");
		data=new byte[4];
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		do{
			Arrays.readFully(dis,data);
			skip+=data.length;
			if(Arrays.startsWith(data,new byte[4]))break;
			baos.write(data);
		}while(true);
		baos.flush();
		data=baos.toByteArray();
		baos.close();
		int count=data.length/8;
		for(int i=0,j;i<count;i++){
			j=4*(count+i);
			Image image=new Image();
			image.offset=((data[i*4+3]&0xff)<<24|(data[i*4+2]&0xff)<<16|(data[i*4+1]&0xff)<<8|(data[i*4]&0xff))<<12;
			image.size=((data[j+3]&0xff)<<24|(data[j+2]&0xff)<<16|(data[j+1]&0xff)<<8|(data[j]&0xff))<<12;
			addImage(image);
		}
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
			for(int i=0;i<getCount();i++){
				Image image=getImage(i);
				save.write(getBytes(image.offset));
				long point=save.getFilePointer();
				save.seek(point+4*(getCount()-1));
				save.write(getBytes(image.size));
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
