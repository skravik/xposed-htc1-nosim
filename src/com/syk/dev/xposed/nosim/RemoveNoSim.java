package com.syk.dev.xposed.nosim;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * Xposed module that disables the persistent "No SIM" notification when lte sim is removed on 4.1.2 Sprint HTC One
 * 
 * Created by Steven on 7/17/13.
 */
public class RemoveNoSim implements IXposedHookLoadPackage{
	
	/**
	 * Hook the phone package which creates the notifications
	 */
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
    	//XposedBridge.log("Loaded app: " + lpparam.packageName);
    	
        if (!lpparam.packageName.equals("com.android.phone"))
            return;

        XposedBridge.log("Adding hooks for package " + lpparam.packageName);

        findAndHookMethod("com.android.internal.telephony.uicc.IccCardProxy", lpparam.classLoader, "notifySIMState", "com.android.internal.telephony.uicc.IccCardProxy.SIMStateNotification", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

            	/*
            	 * From decompiling HTC framework, IccCard notifications are run through
            	 * 		private void notifySIMState(SIMStateNotification paramSIMStateNotification)
            	 * with argument  
            	 * 		public static enum SIMStateNotification
            	 * 
            	 * We care about SIMStateNotification.CARD_ABSENT_NOTIFICATION
            	 */

                if (param.args[0].toString().equals("CARD_ABSENT_NOTIFICATION")){
                	XposedBridge.log("Intercepted CARD_ABSENT_NOTIFICATION");
                	//Prevent original method call
                    param.setResult(null);
                }
                return;
            }
        });
    }
}
