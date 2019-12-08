package com.moe.splashlogo.util;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import com.moe.splashlogo.entity.Hex;

public class Arrays
{
	private static final char[] HEX=new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	public static boolean startsWith(byte[] src,byte... dst){
		if(src.length<dst.length)return false;
		for(int i=0;i<dst.length;i++){
			if(src[i]!=dst[i])return false;
		}
		return true;
	}
	public static int indexOf(byte[] src,byte[] dst,int fromIndex){
		out:
		for(;fromIndex<src.length;fromIndex++){
			if(src[fromIndex]==dst[0]){
				for(int n=1;n<dst.length;n++){
					if(src[fromIndex+n]!=dst[n])
						continue out;
				}
				return fromIndex;
			}
		}
		return -1;
	}
	public static void readFully(InputStream input,byte[] arr) throws IOException{
		int n=0;
		do{
			n+=input.read(arr,n,arr.length-n);
			}while(n<arr.length);
	}
	public static Hex formatToHex(InputStream input) throws IOException {
		Hex hex=new Hex();
		ArrayList<BytesThread> queue=new ArrayList<>();
		byte[] buff=new byte[128*1024];
		int len=-1;
		while((len=input.read(buff))!=-1){
			byte[] copy=new byte[len];
			System.arraycopy(buff,0,copy,0,len);
			queue.add(new BytesThread(copy));
		}
		buff=null;
		for(BytesThread bt:queue){
			while(!bt.isEnd());
			hex.add(bt.getStringBuffer());
			//bt.recycle();
		}
		queue.clear();
		input.close();
		System.gc();
		return hex;
	}
	public static StringBuilder bytes2Hex(byte[] data){
		return bytes2Hex(data,0,data.length);
	}
	public static StringBuilder bytes2Hex(byte[] data,int offset,int count){
		StringBuilder result = new StringBuilder(count*2);
		int end=offset+count;
		for (;offset<end;offset++) {
				result.append(HEX[(data[offset]&0xf0)>>>4]).append(HEX[data[offset]&0x0f]);
			}
		return result;
	}
	public static byte[] hexToBytes(String hex) {
		hex = hex.length() % 2 != 0 ? "0" + hex : hex;

		byte[] b = new byte[hex.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(hex.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}
	static class BytesThread extends Thread{
		private byte[] buff;
		private boolean end;
		private StringBuilder sb=null;
		public BytesThread(byte[] buff){
			this.buff=buff;
			sb=new StringBuilder(buff.length*2);
			start();
			}

		@Override
		public void run()
		{
			sb=(Arrays.bytes2Hex(buff));
			buff=null;
			System.gc();
			end=true;
		}
		public boolean isEnd(){
			return end;
		}
		public StringBuilder getStringBuffer(){
			return sb;
			}
		public void recycle(){
			buff=null;
			sb.setLength(0);
			sb.trimToSize();
		}
	}
}
