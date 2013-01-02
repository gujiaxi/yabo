package com.github.gujiaxi;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

private List<WeiBoInfo> wbList;

//微博列表Adapater
  public class WeiBoAdapater extends BaseAdapter{

      private AsyncImageLoader asyncImageLoader;
      
      @Override
      public int getCount() {
          return wbList.size();
      }

      @Override
      public Object getItem(int position) {
          return wbList.get(position);
      }

      @Override
      public long getItemId(int position) {
          return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
          asyncImageLoader = new AsyncImageLoader();
          convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.weibo, null);
          WeiBoHolder wh = new WeiBoHolder();
          wh.wbicon = (ImageView) convertView.findViewById(R.id.wbicon);
          wh.wbtext = (TextView) convertView.findViewById(R.id.wbtext);
          wh.wbtime = (TextView) convertView.findViewById(R.id.wbtime);
          wh.wbuser = (TextView) convertView.findViewById(R.id.wbuser);
          wh.wbimage=(ImageView) convertView.findViewById(R.id.wbimage);
          WeiBoInfo wb = wbList.get(position);
          if(wb!=null){
              convertView.setTag(wb.getId());
              wh.wbuser.setText(wb.getUserName());
              wh.wbtime.setText(wb.getTime());
              wh.wbtext.setText(wb.getText(), TextView.BufferType.SPANNABLE);
              textHighlight(wh.wbtext,new char[]{'#'},new char[]{'#'});
              textHighlight(wh.wbtext,new char[]{'@'},new char[]{':',' '});
              textHighlight2(wh.wbtext,"http://"," ");
              
              if(wb.getHaveImage()){
                  wh.wbimage.setImageResource(R.drawable.ic_launcher);
              }
              Drawable cachedImage = asyncImageLoader.loadDrawable(wb.getUserIcon(),wh.wbicon, new ImageCallback(){

                  public void imageLoaded(Drawable imageDrawable,ImageView imageView, String imageUrl) {
                      imageView.setImageDrawable(imageDrawable);
                  }
                  
              });
               if (cachedImage == null) {
                   wh.wbicon.setImageResource(R.drawable.ic_launcher);
                  }else{
                      wh.wbicon.setImageDrawable(cachedImage);
                  }
          }
          
          return convertView;
      }