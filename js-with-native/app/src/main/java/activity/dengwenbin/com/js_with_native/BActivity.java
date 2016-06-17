package activity.dengwenbin.com.js_with_native;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 40284 on 2016/6/17.
 */
public class BActivity extends AppCompatActivity{

    private WebView webView;
    private String str;
    private OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        webView = (WebView) findViewById(R.id.bwebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsInterfaceB(), "JsInterfaceB");
        webView.setWebChromeClient(new WebChromeClient() {
        });
        webView.loadUrl("file:///android_asset/b.html");
    }

    public class JsInterfaceB{
        @JavascriptInterface
        public void toAActivity(){
            Intent intent = new Intent(BActivity.this,AActivity.class);
            startActivity(intent);
            finish();
        }
        @JavascriptInterface
        public String callApi(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getAPI();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            System.out.println(str);
            return str;
        }
    }

    public void getAPI() throws IOException {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/square/okhttp/issues")
                .addHeader("Accept", "application/json; q=0.5")
                .build();
        Response response = client.newCall(request).execute();
        str = response.body().string();
    }
}
