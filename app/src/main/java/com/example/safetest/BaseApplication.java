package com.example.safetest;

/**
 * 应用基类Application(继承于框架基类Application)
 * Created by lishilin on 2016/11/29.
 */
public class BaseApplication extends BaseFrameApplication {


    public static boolean isFirst = true;

    @Override
    protected Class getCrashLauncherActivity() {
        return RecordActivity.class;
    }

    @Override
    protected void uncaughtException(Thread thread, Throwable ex) {
        super.uncaughtException(thread, ex);
    }

    @Override
    protected void onInitData() {
        super.onInitData();
        LogUtil.i("BaseApplication onInitData");
    }

    @Override
    protected void onInitDataThread() {
        super.onInitDataThread();
        LogUtil.i("BaseApplication onInitDataThread");
    }
}
