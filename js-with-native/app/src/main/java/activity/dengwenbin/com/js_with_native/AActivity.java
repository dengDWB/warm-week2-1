package activity.dengwenbin.com.js_with_native;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import help.Tool;
import listener.ScreenListener;

public class AActivity extends AppCompatActivity {

    private WebView webView;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsInterface(),"JsInterface");
        webView.setWebChromeClient(new WebChromeClient() {
        });
        webView.loadUrl("file:///android_asset/a.html");

    }

    public class JsInterface{
        @JavascriptInterface
        public void toBActivity(){
            Intent intent = new Intent(AActivity.this,BActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("AActivity销毁");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!Tool.isBackstage(this)){
            Log.i("State","切换前台");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!Tool.isBackstage(this)){
            Log.i("State","切换后台");
        }
    }
}
