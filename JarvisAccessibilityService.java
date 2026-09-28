package com.jarvis.android;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.*;

public class JarvisAccessibilityService extends AccessibilityService {
    private static JarvisAccessibilityService instance;
    public static JarvisAccessibilityService get(){ return instance; }
    @Override public void onServiceConnected(){ super.onServiceConnected(); instance=this; }
    @Override public void onAccessibilityEvent(AccessibilityEvent event){}
    @Override public void onInterrupt(){}
    @Override public void onDestroy(){ if(instance==this) instance=null; super.onDestroy(); }

    public boolean clickText(String text){
        AccessibilityNodeInfo root=getRootInActiveWindow(); if(root==null) return false;
        AccessibilityNodeInfo n=findText(root,text); if(n==null) return false;
        return clickNode(n);
    }
    private AccessibilityNodeInfo findText(AccessibilityNodeInfo n,String q){
        CharSequence t=n.getText(), d=n.getContentDescription();
        if((t!=null && t.toString().equalsIgnoreCase(q)) || (d!=null && d.toString().equalsIgnoreCase(q))) return n;
        for(int i=0;i<n.getChildCount();i++){ AccessibilityNodeInfo c=n.getChild(i); if(c!=null){ AccessibilityNodeInfo r=findText(c,q); if(r!=null)return r; }}
        return null;
    }
    private boolean clickNode(AccessibilityNodeInfo n){
        if(n.isClickable()) return n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        AccessibilityNodeInfo p=n.getParent(); if(p!=null) return clickNode(p); return false;
    }
    public boolean clickContains(String text){
        AccessibilityNodeInfo root=getRootInActiveWindow(); if(root==null)return false;
        AccessibilityNodeInfo n=findContains(root,text.toLowerCase()); return n!=null && clickNode(n);
    }
    private AccessibilityNodeInfo findContains(AccessibilityNodeInfo n,String q){
        CharSequence t=n.getText(),d=n.getContentDescription();
        if((t!=null&&t.toString().toLowerCase().contains(q))||(d!=null&&d.toString().toLowerCase().contains(q)))return n;
        for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo c=n.getChild(i);if(c!=null){AccessibilityNodeInfo r=findContains(c,q);if(r!=null)return r;}}
        return null;
    }
    public boolean typeText(String text){
        AccessibilityNodeInfo root=getRootInActiveWindow(); if(root==null)return false;
        AccessibilityNodeInfo f=root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT); if(f==null)return false;
        android.os.Bundle b=new android.os.Bundle(); b.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,text);
        return f.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,b);
    }
    public boolean back(){return performGlobalAction(GLOBAL_ACTION_BACK);}
    public boolean home(){return performGlobalAction(GLOBAL_ACTION_HOME);}
    public boolean recents(){return performGlobalAction(GLOBAL_ACTION_RECENTS);}
    public boolean tap(float x,float y){
        Path p=new Path();p.moveTo(x,y);p.lineTo(x,y);
        GestureDescription g=new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(p,0,80)).build();
        return dispatchGesture(g,null,null);
    }
    public boolean swipe(float x1,float y1,float x2,float y2,long ms){
        Path p=new Path();p.moveTo(x1,y1);p.lineTo(x2,y2);
        GestureDescription g=new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(p,0,ms)).build();
        return dispatchGesture(g,null,null);
    }
}
