package com.example.songyan.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context, AttributeSet attr){
        super(context,attr);
    }

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attr){
        return new MarginLayoutParams(getContext(),attr);
    }

    //根据子View获取容器的宽和高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取父View指定的高度宽度和测量模式
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);

        Log.e("songsong",widthSize+","+heightSize);

        //如果测量模式是AT_MOST,则根据子View得到宽度和高度
        int width=0;//宽度
        int height=0;//高度

        //每一行对应的宽度
        int lineWidth=0;
        //每一行对应的高度
        int lineHeight=0;

        int childCount=getChildCount();

        for(int i=0;i<childCount;i++){
            View childView=getChildAt(i);

            measureChild(childView,widthMeasureSpec,heightMeasureSpec);//测量子View的宽高

            MarginLayoutParams layoutParams=(MarginLayoutParams)childView.getLayoutParams();
            //当前子控件占据的实际宽度
            int childWidth=layoutParams.leftMargin+layoutParams.rightMargin+childView.getMeasuredWidth();
            //当前子控件占据的实际高度
            int childHeight=layoutParams.topMargin+layoutParams.bottomMargin+childView.getMeasuredHeight();

            //如果行宽度大于最大宽度
            if(lineWidth+childWidth>widthSize){
                width=Math.max(lineWidth,childWidth);
                //行宽度更新
                lineWidth=childWidth;
                height+=lineHeight;//换行前加上这一行的高度
                //行高度更新
                lineHeight=childHeight;
            }else{
                //如果行宽度没有大于最大宽度
                lineWidth+=childWidth;
                lineHeight=Math.max(lineHeight,childHeight);
            }
            if(i==childCount-1){
                width=Math.max(width,lineWidth);
                height+=lineHeight;
            }
        }

        setMeasuredDimension((widthMode==MeasureSpec.EXACTLY)? widthSize:width,
                (heightMode==MeasureSpec.EXACTLY)?heightSize:height);
    }

    //存储所有的View
    private List<List<View>> mAllViews=new ArrayList<List<View>>();
    //记录每一行的高度
    private List<Integer> mLineHeights=new ArrayList<Integer>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeights.clear();

        //存储每一行的Views
        List<View> lineViews=new ArrayList<View>();

        int width=getWidth();
        Log.e("songsong","width is "+width);

        int lineWidth=0;
        int lineHeight=0;

        int childCount=getChildCount();
        Log.e("songsong","childCount is "+childCount);
        for(int i=0;i<childCount;i++){

            View child=getChildAt(i);
            MarginLayoutParams layoutParams=(MarginLayoutParams)child.getLayoutParams();

            int childWidth=child.getMeasuredWidth();
            int childHeight=child.getMeasuredHeight();
            Log.e("songsong",child+"childWidth is "+childWidth+" childHeight is "+childHeight+
                    " leftMargin is "+layoutParams.leftMargin+" rightMargin is "+layoutParams.rightMargin);

            //如果当前宽度大于测量后的最大宽度
            if(childWidth+layoutParams.leftMargin+layoutParams.rightMargin+lineWidth>width){
                mAllViews.add(lineViews);
                mLineHeights.add(lineHeight);
                lineWidth=childWidth;
                lineViews=new ArrayList<View>();
                lineViews.add(child);
            }else{
                lineWidth+=childWidth+layoutParams.leftMargin+layoutParams.rightMargin;
                lineHeight=Math.max(lineHeight,childHeight+layoutParams.topMargin+layoutParams.bottomMargin);
                lineViews.add(child);
            }
        }
        //记录最后一行
        mAllViews.add(lineViews);
        mLineHeights.add(lineHeight);

        int left=0;
        int top=0;
        int lineNum=mAllViews.size();
        for(int i=0;i<lineNum;i++){
            lineViews=mAllViews.get(i);
            lineHeight=mLineHeights.get(i);
            Log.e("songsong", "第" + i + "行 ：" + lineViews.size() + " , " + lineViews);
            Log.e("songsong", "第" + i + "行， ：" + lineHeight);
            for(int j=0;j<lineViews.size();j++){
                View view=lineViews.get(j);//kaokaokao,明明是j写成i能对吗
                Log.e("songsong","view is "+view);
                if(view.getVisibility()==View.GONE){
                    continue;
                }
                MarginLayoutParams layoutParams=(MarginLayoutParams)view.getLayoutParams();
                int lc=left+layoutParams.leftMargin;
                int tc=top+layoutParams.topMargin;
                int rc=lc+view.getMeasuredWidth();
                int bc=tc+view.getMeasuredHeight();

                Log.e("songsong",view+"lc,tc,rc,bc "+lc+","+tc+","+rc+","+bc);

                view.layout(lc,tc,rc,bc);
                left+=layoutParams.leftMargin+view.getMeasuredWidth()+layoutParams.rightMargin;
            }
            left=0;
            top+=lineHeight;
        }
    }
}
