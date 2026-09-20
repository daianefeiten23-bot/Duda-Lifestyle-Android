package com.dudalifestyle.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {
 private static final int MIC=1001;
 private static final String TEST_REWARDED="ca-app-pub-3940256099942544/5224354917";
 private WebView webView;
 private PermissionRequest pending;
 private RewardedAd rewardedAd;

 @SuppressLint({"SetJavaScriptEnabled","JavascriptInterface"})
 @Override protected void onCreate(Bundle b){
  super.onCreate(b);
  setContentView(R.layout.activity_main);
  MobileAds.initialize(this, s -> loadAd());
  webView=findViewById(R.id.webView);
  webView.getSettings().setJavaScriptEnabled(true);
  webView.getSettings().setDomStorageEnabled(true);
  webView.getSettings().setAllowFileAccess(true);
  webView.getSettings().setAllowContentAccess(true);
  webView.setWebViewClient(new WebViewClient());
  webView.addJavascriptInterface(new Bridge(),"Android");
  webView.setWebChromeClient(new WebChromeClient(){
   @Override public void onPermissionRequest(PermissionRequest r){
    runOnUiThread(() -> {
     boolean mic=false;
     for(String x:r.getResources()) if(PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(x)) mic=true;
     if(!mic){r.deny();return;}
     if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED)
      r.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
     else { pending=r; ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},MIC); }
    });
   }
  });
  webView.loadUrl("file:///android_asset/index.html");
 }
 private void loadAd(){
  RewardedAd.load(this,TEST_REWARDED,new AdRequest.Builder().build(),new RewardedAdLoadCallback(){
   @Override public void onAdLoaded(@NonNull RewardedAd a){
    rewardedAd=a;
    rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback(){
     @Override public void onAdDismissedFullScreenContent(){rewardedAd=null;loadAd();}
    });
   }
   @Override public void onAdFailedToLoad(@NonNull LoadAdError e){rewardedAd=null;}
  });
 }
 private void showAd(){
  runOnUiThread(() -> {
   if(rewardedAd==null){
    loadAd();
    webView.evaluateJavascript("window.dispatchEvent(new CustomEvent('dudaAdUnavailable'));",null);
    return;
   }
   rewardedAd.show(this,r -> webView.evaluateJavascript(
    "window.dispatchEvent(new CustomEvent('dudaRewardEarned',{detail:{amount:"+r.getAmount()+"}}));",null));
  });
 }
 public class Bridge { @JavascriptInterface public void showRewardedAd(){showAd();} }
 @Override public void onRequestPermissionsResult(int code,@NonNull String[] p,@NonNull int[] g){
  super.onRequestPermissionsResult(code,p,g);
  if(code==MIC && pending!=null){
   if(g.length>0 && g[0]==PackageManager.PERMISSION_GRANTED)
    pending.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
   else pending.deny();
   pending=null;
  }
 }
}
