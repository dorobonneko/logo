package com.moe.splashlogo;
import java.io.InputStream;
import java.util.zip.ZipFile;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import android.text.TextUtils;
import java.util.List;
import java.util.ArrayList;

public class LogoConfig
{
	public String product,block;
	public String[] input;
	public int size,width,height;
	public Offset offset[];
	private List<Offset> offsets=new ArrayList<>();
	private ZipFile zf;
	public LogoConfig(String file) throws IOException{
		zf=new ZipFile(file);
		BufferedReader br=new BufferedReader(new InputStreamReader(zf.getInputStream(zf.getEntry("logo.ini"))));
		String line=null;
		while((line=br.readLine())!=null)
			if(!TextUtils.isEmpty(line))
			readLine(line);
		br.close();
	}
	public InputStream getOffset(Offset offset) throws IOException{
		switch(offset.type){
			case BYTE:
				String[] hex=offset.data.split(",");
				byte[] data=new byte[hex.length];
				for(int i=0;i<hex.length;i++){
					data[i]=Byte.parseByte(hex.toString().substring(2),16);
				}
				return new ByteArrayInputStream(data);
			case REF:
				return null;
			case FILE:
				return zf.getInputStream(zf.getEntry(offset.data));
		}
		return null;
	}
	private void readLine(String line){
		int offset=line.indexOf("=");
		if(offset==-1)return;
		switch(line.substring(0,offset)){
			case "product":
				product=line.substring(offset+1).trim();
				break;
			case "size":
				size=Integer.parseUnsignedInt(line.substring(offset+1).trim().substring(2),16);
				break;
			case "pixel":
				String[] pixel=line.substring(offset+1).trim().split(",");
				width=Integer.parseInt(pixel[0]);
				height=Integer.parseInt(pixel[1]);
				break;
			case "block":
				block=line.substring(offset+1).trim();
				break;
			case "input":
				input=line.substring(offset+1).trim().split(",");
				break;
			case "offset":
				int sp=line.indexOf(";");
				if(sp==-1)throw new IllegalStateException("no offset");
				Offset offset_=new Offset();
				offset_.offset=Integer.parseUnsignedInt(line.substring(offset+1,sp).trim().substring(2),16);
				offset=line.indexOf("=",sp);
				if(offset==-1)throw new IllegalStateException("offset no data");
				offset_.type=Offset.Type.parse(line.substring(sp+1,offset).trim());
				offset_.data=line.substring(offset+1).trim();
				offsets.add(offset_);
				break;
		}
	}
	public static class Offset{
		public int offset;
		public String data;
		public Type type;
		public enum Type{
			BYTE,REF,FILE;
			public static Type parse(String type){
				switch(type){
					case "file":
						return FILE;
					case "ref":
						return REF;
					case "byte":
						return BYTE;
				}
				throw new IllegalStateException("unknow offset type"); 
			}
		}
	}
}
