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
import android.net.Uri;
import android.graphics.Bitmap;
import java.util.zip.ZipEntry;
import java.io.DataInputStream;
import com.moe.splashlogo.util.Arrays;

public class LogoConfig
{
	public static final byte[] header=new byte[]{0x4c,0x4f,0x47,0x4f,0x21,0x21,0x21,0x21};
	public String product,block;
	//public String[] input;
	public int size,width,height;
	public Offset offset[];
	private List<Offset> offsets=new ArrayList<>();
	private ZipFile zf;
	public LogoConfig(String file) throws IOException{
		zf=new ZipFile(file);
		ZipEntry ini=zf.getEntry("logo.ini");
		if(ini==null)throw new IOException();
		BufferedReader br=new BufferedReader(new InputStreamReader(zf.getInputStream(ini)));
		String line=null;
		while((line=br.readLine())!=null)
			if(!TextUtils.isEmpty(line))
			readLine(line);
		br.close();
		offset=offsets.toArray(new Offset[0]);
		offsets.clear();
	}
	public LogoConfig(InputStream img_input,int offset) throws IOException {
		img_input.skip(offset);
		DataInputStream input=new DataInputStream(img_input);
		byte[] header=new byte[56];
		input.readFully(header);
		if(!Arrays.startsWith(header,LogoConfig.header))
			throw new IllegalStateException("it's not logo.img");
		offsets.add(new Offset(0x4000,header));
		input.skip(4040);//offset 5000
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
				String[] pixel=line.substring(offset+1).trim().split("x");
				width=Integer.parseInt(pixel[0]);
				height=Integer.parseInt(pixel[1]);
				break;
			case "block":
				block=line.substring(offset+1).trim();
				break;
			/*case "input":
				input=line.substring(offset+1).trim().split(",");
				break;*/
			case "offset":
				int sp=line.indexOf(";");
				if(sp==-1)throw new IllegalStateException("no offset");
				Offset offset_=new Offset();
				offset_.offset=Integer.parseUnsignedInt(line.substring(offset+1,sp).trim().substring(2),16);
				offset=line.indexOf("=",sp);
				if(offset==-1)throw new IllegalStateException("offset no data");
				offset_.type=Offset.Type.parse(line.substring(sp+1,offset).trim());
				offset_.data=line.substring(offset+1).trim();
				offsets.add(offset_.type==Offset.Type.REF?new RefOffset(offset_):offset_);
				break;
		}
	}
	public static class Offset{
		public int offset;
		public String data;
		public Type type;
		public Offset(int offset,String data,Type type){
			this.offset=offset;
			this.data=data;
			this.type=type;
		}
		public Offset(int offset,byte... data){
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<data.length;i++){
				sb.append("0x".concat(Integer.toHexString(Byte.toUnsignedInt(data[i])))).append(",");
			}
			sb.setLength(sb.length()-1);
			this.offset=offset;
			this.data=sb.toString();
			sb.setLength(0);
			sb.trimToSize();
			this.type=Type.BYTE;
		}
		public Offset(){}
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
	public static class RefOffset extends Offset{
		public Uri uri;
		public Bitmap bitmap;
		public RefOffset(Offset offset){
			super.offset=offset.offset;
			super.data=offset.data;
			super.type=offset.type;
		}
	}
}
