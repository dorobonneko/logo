package com.moe.splashlogo.entity;
import java.util.List;
import java.util.ArrayList;
import com.moe.splashlogo.util.Arrays;

public class Hex
{
	private List<StringBuilder> list=new ArrayList<>();
	private int point;
	public void add(StringBuilder sb){
		list.add(sb);
	}
	public void seek(int pos){
		this.point=pos;
	}
	public void skip(int bytes){
		this.point+=bytes;
	}
	public int indexOf(String hex){
		return indexOf(hex,0);
	}
	public int indexOf(byte[] bytes){
		return indexOf(bytes,0);
	}
	public int indexOf(String hex,int fromIndex){
		return -1;
	}
	public int indexOf(byte[] bytes,int fromIndex){
		return indexOf(Arrays.bytes2Hex(bytes).toString(),fromIndex);
	}
	public String substring(int start,int end){
		return null;
	}
}
