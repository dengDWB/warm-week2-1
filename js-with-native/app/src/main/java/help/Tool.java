package help;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by 40284 on 2016/6/17.
 */
public class Tool {


    public static boolean isBackstage(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appPracesses : list) {
            if (appPracesses.processName.equals(context.getPackageName())) {
                if (appPracesses.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return true;
                }
            }
        }
        return false;
    }
}
