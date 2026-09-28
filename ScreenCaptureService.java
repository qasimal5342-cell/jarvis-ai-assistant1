package com.jarvis.android;

import android.app.*;import android.content.*;import android.graphics.PixelFormat;import android.hardware.display.*;import android.media.*;import android.os.*;import java.io.*;

public class ScreenCaptureService extends Service {
 static MediaProjection projection; static ImageReader reader; static int width,height,density;
 public static void startCapture(Context c,int resultCode,Intent data){
  Intent i=new Intent(c,ScreenCaptureService.class); i.putExtra("code",resultCode); i.putExtra("data",data); c.startForegroundService(i);
 }
 @Override public int onStartCommand(Intent intent,int flags,int id){
  createChannel(); startForeground(77,notification());
  if(projection==null){ MediaProjectionManagerHolder m=new MediaProjectionManagerHolder(this); projection=m.get(intent.getIntExtra("code",0),(Intent)intent.getParcelableExtra("data")); }
  if(projection!=null){ DisplayMetricsHolder.init(this); width=DisplayMetricsHolder.w;height=DisplayMetricsHolder.h;density=DisplayMetricsHolder.d; reader=ImageReader.newInstance(width,height,PixelFormat.RGBA_8888,2); projection.createVirtualDisplay("JARVIS",width,height,density,DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,reader.getSurface(),null,null); }
  return START_STICKY;
 }
 public static BitmapFrame grab(){ if(reader==null)return null; Image im=reader.acquireLatestImage(); if(im==null)return null; Image.Plane p=im.getPlanes()[0]; int row=p.getRowStride(), pixel=p.getPixelStride(); android.graphics.Bitmap b=android.graphics.Bitmap.createBitmap(width+(row-pixel*width)/pixel,height,android.graphics.Bitmap.Config.ARGB_8888); b.copyPixelsFromBuffer(p.getBuffer()); im.close(); return new BitmapFrame(b); }
 static class BitmapFrame { public final android.graphics.Bitmap bitmap; BitmapFrame(android.graphics.Bitmap b){bitmap=b;} }
 private void createChannel(){if(Build.VERSION.SDK_INT>=26)getSystemService(NotificationManager.class).createNotificationChannel(new NotificationChannel("jarvis","JARVIS",NotificationManager.IMPORTANCE_LOW));}
 private Notification notification(){return new Notification.Builder(this,"jarvis").setContentTitle("JARVIS Screen Vision").setContentText("Screen capture is active").setSmallIcon(android.R.drawable.ic_menu_view).build();}
 @Override public IBinder onBind(Intent i){return null;}
 static class DisplayMetricsHolder{static int w,h,d;static void init(Context c){android.util.DisplayMetrics m=c.getResources().getDisplayMetrics();w=m.widthPixels;h=m.heightPixels;d=m.densityDpi;}}
 static class MediaProjectionManagerHolder{android.media.projection.MediaProjectionManager x;MediaProjectionManagerHolder(Context c){x=(android.media.projection.MediaProjectionManager)c.getSystemService(Context.MEDIA_PROJECTION_SERVICE);}MediaProjection get(int code,Intent data){return x.getMediaProjection(code,data);}}
}
