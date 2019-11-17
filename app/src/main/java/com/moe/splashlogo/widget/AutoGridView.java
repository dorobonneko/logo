package com.moe.splashlogo.widget;

import android.util.*;
import android.widget.*;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

public class AutoGridView extends GridView
 {

	private static final String TAG = "AutoGridView";
	private int numColumnsID;
	private int previousFirstVisible;
	private int numColumns = 1;

	public AutoGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AutoGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoGridView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int max=0,i=0,height = 0;
		for(;i<getChildCount();i++){
			View child=getChildAt(i);
			max=Math.max(max,child.getMeasuredHeight());
			if((i+1)%getNumColumns()==0){
				for(int n=i-getNumColumns()+1;n<i+1;n++){
					View c=getChildAt(n);
					c.measure(c.getMeasuredWidth(),MeasureSpec.makeMeasureSpec(max,MeasureSpec.EXACTLY));
					c.setMinimumHeight(max);
				}
				height+=(max+getVerticalSpacing());
				max=0;
			}
		}
		if(getChildCount()%getNumColumns()!=0){
			height+=max;
			for(int n=getChildCount()-getChildCount()/3;n<getChildCount();n++){
				View c=getChildAt(n);
				c.measure(c.getMeasuredWidth(),MeasureSpec.makeMeasureSpec(max,MeasureSpec.EXACTLY));
				c.setMinimumHeight(max);
			}
		}else{
			height-=getVerticalSpacing();
		}
		if(height!=0)
		setMeasuredDimension(getMeasuredWidth(),height==0?heightMeasureSpec:MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		// TODO: Implement this method
		super.onScrollChanged(l, t, oldl, oldt);
		int max=0,i=0,height = 0;
		for(;i<getChildCount();i++){
			View child=getChildAt(i);
			max=Math.max(max,child.getMeasuredHeight());
			if((i+1)%getNumColumns()==0){
				for(int n=i-getNumColumns()+1;n<i+1;n++){
					View c=getChildAt(n);
					//c.measure(c.getMeasuredWidth(),MeasureSpec.makeMeasureSpec(max,MeasureSpec.EXACTLY));
					c.setMinimumHeight(max);
				}
				height+=max;
				max=0;
			}
		}
		if(max!=0){
			height+=max;
			for(int n=getChildCount()-getChildCount()/3;n<getChildCount();n++){
				View c=getChildAt(n);
				//c.measure(c.getMeasuredWidth(),MeasureSpec.makeMeasureSpec(max,MeasureSpec.EXACTLY));
				c.setMinimumHeight(max);
			
			}
		}
	}
	

}
