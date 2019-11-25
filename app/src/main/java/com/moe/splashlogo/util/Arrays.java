package com.moe.splashlogo.util;
import java.io.InputStream;
import java.io.IOException;

public class Arrays
{
	public static boolean startsWith(byte[] src,byte... dst){
		for(int i=0;i<dst.length;i++){
			if(src[i]!=dst[i])return false;
		}
		return true;
	}
	public static void readFully(InputStream input,byte[] arr) throws IOException{
		int n=0;
		do{
			n+=input.read(arr,n,arr.length-n);
			}while(n<arr.length);
	}
}
