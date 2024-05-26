package com.example.nexgo_print;

import android.app.Application;
import com.nexgo.oaf.apiv3.APIProxy;
import com.nexgo.oaf.apiv3.DeviceEngine;


public class NexgoApplication extends Application {
    public DeviceEngine deviceEngine;

    @Override
    public void onCreate() {
        super.onCreate();
        deviceEngine = APIProxy.getDeviceEngine(this);
        deviceEngine.getEmvHandler2("app2");
    }
}
