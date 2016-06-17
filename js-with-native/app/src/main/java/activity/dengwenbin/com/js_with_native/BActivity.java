package activity.dengwenbin.com.js_with_native;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 40284 on 2016/6/17.
 */
public class BActivity extends AppCompatActivity{

    private WebView webView;
    private String str="";
    private OkHttpClient client;
    private String s="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        webView = (WebView) findViewById(R.id.bwebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.addJavascriptInterface(new JsInterfaceB(), "JsInterfaceB");
        webView.setWebChromeClient(new WebChromeClient() {
        });
        webView.loadUrl("file:///android_asset/b.html");
    }

    public class JsInterfaceB{
        @JavascriptInterface
        public void toAActivity(){
            Intent intent = new Intent(BActivity.this,AActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        @JavascriptInterface
        public void callApi(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getAPI();
                        System.out.println(str);
                        if(s.equals("")){
                            webView.loadUrl("javascript:addApi("+"'加载中'"+")");
                            System.out.println("javascript:addApi("+"'加载中'"+")");
                            Thread.sleep(1000);
                        }else if(!s.equals("")){
                            webView.loadUrl("javascript:addApi(\'" + s + "\')");
                            System.out.println("javascript:addApi(\'" + s + "\')");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            System.out.println(str);

        }
    }

    public void getAPI() throws IOException {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/square/okhttp/issues/2635")
                .addHeader("Accept", "application/json; q=0.5")
                .build();
        Response response = client.newCall(request).execute();
        str = response.body().string();
        System.out.println(str);
        String str1 ="'";
        Pattern pattern = Pattern.compile(str1);
        Matcher matcher = pattern.matcher(str);
        s= matcher.replaceAll("l");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("BAcvitity销毁");
    }
}
