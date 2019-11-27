package com.moe.splashlogo.io;
import java.io.InputStream;
import java.io.IOException;
import com.moe.splashlogo.util.Arrays;

public class ArrayInputStream extends InputStream
{
	private byte[] data;
	private int pos;
	public ArrayInputStream(byte[] data){
		this.data=data;
	}
	@Override
	public int read() throws IOException
	{
		// TODO: Implement this method
		return 0;
	}
	public void readFully(byte[] b) throws IOException{
		Arrays.readFully(this,b);
	}
	@Override
	public int read(byte[] b) throws IOException
	{
		// TODO: Implement this method
		return read(b,0,b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		System.arraycopy(data,pos,b,off,len);
		pos+=len;
		return len;
	}

	@Override
	public int available() throws IOException
	{
		return data.length-pos;
	}

	@Override
	public boolean markSupported()
	{
		// TODO: Implement this method
		return true;
	}
	public void seek(int offset){
		pos=offset;
	}

	@Override
	public long skip(long n) throws IOException
	{
		pos+=n;
		return n;
	}

	@Override
	public void reset() throws IOException
	{
		pos=0;
	}
	public int indexOf(byte[] dst,int fromIndex){
		return Arrays.indexOf(data,dst,fromIndex);
	}
	public int getPoint(){
		return pos;
	}
	public int size(){
		return data.length;
	}
	public void copy(byte[] dst,int fromIndex,int count){
		System.arraycopy(data,pos,dst,fromIndex,count);
	}
	public byte[] sourceBytes(){
		return data;
	}
}
