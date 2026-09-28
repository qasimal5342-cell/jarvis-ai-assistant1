package com.jarvis.android;

import android.Manifest;import android.app.*;import android.content.*;import android.content.pm.PackageManager;import android.media.projection.MediaProjectionManager;import android.net.Uri;import android.os.*;import android.provider.Settings;import android.speech.*;import android.view.*;import android.widget.*;import java.util.*;

public class MainActivity extends Activity {
 TextView status,log; EditText command; SpeechRecognizer sr; android.speech.tts.TextToSpeech tts; final int AUDIO=10, CAPTURE=11;
 @Override public void onCreate(Bundle b){super.onCreate(b);setContentView(com.jarvis.android.R.layout.activity_main); status=findViewById(R.id.status);log=findViewById(R.id.log);command=findViewById(R.id.command);
  tts=new android.speech.tts.TextToSpeech(this,s->{if(s==0){tts.setLanguage(Locale.US);}});
  findViewById(R.id.voice).setOnClickListener(v->listen());
  findViewById(R.id.run).setOnClickListener(v->run(command.getText().toString()));
  findViewById(R.id.access).setOnClickListener(v->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
  findViewById(R.id.screen).setOnClickListener(v->requestCapture());
  if(Build.VERSION.SDK_INT>=23&&checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},AUDIO);
  say("JARVIS ready.");
 }
 void listen(){status.setText("Listening..."); if(sr!=null)sr.destroy(); sr=SpeechRecognizer.createSpeechRecognizer(this); sr.setRecognitionListener(new RecognitionListener(){public void onReadyForSpeech(Bundle b){}public void onBeginningOfSpeech(){}public void onRmsChanged(float r){}public void onBufferReceived(byte[] b){}public void onEndOfSpeech(){status.setText("Processing...");}public void onError(int e){status.setText("Voice error: "+e);}public void onResults(Bundle b){ArrayList<String>x=b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);if(x!=null&&!x.isEmpty()){command.setText(x.get(0));run(x.get(0));}}public void onPartialResults(Bundle b){}public void onEvent(int a,Bundle b){}}); Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-PK");i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,3);sr.startListening(i); }
 void run(String raw){String c=raw==null?"":raw.trim();if(c.isEmpty())return;log.append("\n> "+c);String l=c.toLowerCase(Locale.ROOT);JarvisAccessibilityService a=JarvisAccessibilityService.get();
  if(l.contains("time")){say(new java.text.SimpleDateFormat("hh:mm a",Locale.US).format(new Date()));return;}
  if(l.contains("date")){say(new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy",Locale.US).format(new Date()));return;}
  if(l.contains("open canva")){openUrl("https://www.canva.com/");say("Opening Canva.");return;}
  if(l.contains("open youtube")){openUrl("https://www.youtube.com/");say("Opening YouTube.");return;}
  if(l.contains("open google")){openUrl("https://www.google.com/");say("Opening Google.");return;}
  if(l.contains("open github")){openUrl("https://github.com/");say("Opening GitHub.");return;}
  if(l.equals("back")||l.contains("go back")){say(a!=null&&a.back()?"Going back.":"Screen control is not enabled.");return;}
  if(l.equals("home")||l.contains("go home")){say(a!=null&&a.home()?"Home.":"Screen control is not enabled.");return;}
  if(l.contains("undo")){if(a!=null&&a.clickContains("Undo"))say("Undo done.");else say("I could not find Undo on the current screen.");return;}
  if(l.contains("redo")){if(a!=null&&a.clickContains("Redo"))say("Redo done.");else say("I could not find Redo on the current screen.");return;}
  if(l.contains("remove")||l.contains("delete")){say("I need a selectable target or an app-specific action. I will not delete an unknown element automatically.");return;}
  if(l.contains("make")&&l.contains("professional")){say("I can analyze the screen when a vision AI endpoint is configured. The current offline engine cannot safely redesign an unknown interface by itself.");return;}
  if(l.startsWith("tap ")){try{String[]p=l.substring(4).split(",");say(a!=null&&a.tap(Float.parseFloat(p[0].trim()),Float.parseFloat(p[1].trim()))?"Tapped.":"Screen control is not enabled.");}catch(Exception e){say("Use: tap x,y");}return;}
  if(l.startsWith("type ")){String s=c.substring(5);say(a!=null&&a.typeText(s)?"Typed.":"No focused text field or accessibility permission.");return;}
  if(l.contains("screenshot")||l.contains("screen shot")){ScreenCaptureService.BitmapFrame f=ScreenCaptureService.grab();say(f!=null?"Screen captured.":"Enable Screen Vision first.");return;}
  if(l.startsWith("search ")){openUrl("https://www.google.com/search?q="+Uri.encode(c.substring(7)));say("Searching.");return;}
  say("Command received. For app control, enable Screen Control. For visual AI actions, connect a vision AI endpoint in the final configuration."); }
 void openUrl(String u){startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(u)));}
 void say(String s){status.setText(s);log.append("\nJARVIS: "+s);if(tts!=null)tts.speak(s,android.speech.tts.TextToSpeech.QUEUE_FLUSH,null,"jarvis");}
 void requestCapture(){MediaProjectionManager m=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);startActivityForResult(m.createScreenCaptureIntent(),CAPTURE);}
 @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==CAPTURE&&c==RESULT_OK&&d!=null){ScreenCaptureService.startCapture(this,c,d);say("Screen vision is active.");}}
 @Override protected void onDestroy(){if(sr!=null)sr.destroy();if(tts!=null)tts.shutdown();super.onDestroy();}
}
