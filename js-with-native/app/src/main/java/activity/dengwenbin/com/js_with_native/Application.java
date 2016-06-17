package activity.dengwenbin.com.js_with_native;

import android.util.Log;

import listener.ScreenListener;

/**
 * Created by 40284 on 2016/6/17.
 */
public class Application extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ScreenListener l = new ScreenListener(this);
        l.begin(new ScreenListener.ScreenStateListener() {

            @Override
            public void onUserPresent() {
                Log.e("State", "解锁");
            }

            @Override
            public void onScreenOn() {
                Log.e("State", "开屏");
            }

            @Override
            public void onScreenOff() {
                Log.e("State", "锁屏");
            }
        });
    }
}
