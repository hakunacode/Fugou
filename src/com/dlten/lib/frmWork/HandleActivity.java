package com.dlten.lib.frmWork;

import com.dlten.lib.Common;
import com.dlten.lib.STD;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;

public abstract class HandleActivity extends Activity implements MainThreadListner {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // requestKillProcess();
        firstInit();
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		clearApplicationCache(null);
		
// disabled by hrh 2011-0726-1548
//		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//		am.restartPackage(getPackageName());
		
		System.runFinalizersOnExit(true); 
		System.exit(0);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	// override keyproc functions.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch( keyCode ) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			mediaVolumeDown();
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			mediaVolumeUp();
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}
	
	private void firstInit() {
		Display d = ((WindowManager) getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay(); 
		int width  = d.getWidth();
		int height = d.getHeight();
		STD.logout("width=" + width + ", height=" + height);
		Common.Initialize(this, width, height);
	}
	
    private void mediaVolumeDown(){
    	AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	am.adjustStreamVolume(
    			AudioManager.STREAM_MUSIC, 
    			AudioManager.ADJUST_LOWER, 
    			AudioManager.FLAG_SHOW_UI);
    }
    private void mediaVolumeUp(){
    	AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	am.adjustStreamVolume(
    			AudioManager.STREAM_MUSIC, 
    			AudioManager.ADJUST_RAISE, 
    			AudioManager.FLAG_SHOW_UI);
    }

    private int prevState = 0;
    
// disabled by hrh 2011-0726-1548
//    public void requestKillProcess() {
////    	int sdkVersion = Integer.parseInt(android.os.Build.VERSION.SDK);
////	   	if (false && sdkVersion < 8)
////	   	{
////		    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
////		    am.restartPackage(getPackageName());
////	    }
////	   	else
//	   	{
//	    	new Thread(new Runnable() {
//	    		@Override
//	    		public void run() {
//	    			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//	    			String name = getApplicationInfo().processName;
//	    			while (true) {
//	    				java.util.List<RunningAppProcessInfo> list = am
//	    				.getRunningAppProcesses();
//	    				for (RunningAppProcessInfo i : list) {
//	    					if (i.processName.equals(name) == true) {
//	    						if( prevState != i.importance ) {
//	    							STD.logout("process '" + name + "',s state is " + i.importance);
//	    							prevState = i.importance;
//	    						}
//	    						if (i.importance >= RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
//	    							clearApplicationCache(null);
//	    							// kill process
//	    							// am.killBackgroundProcesses(getPackageName());
//	    							am.restartPackage(getPackageName());
//	    						}
//	    						break;
//	    					}
//    						Thread.yield();
//	    				}
//	    				STD.sleep(30);
//	    			}
//	    		}
//	    	}, "Process Killer").start();
//	    }
//    }
	private void clearApplicationCache(java.io.File dir){
        if(dir==null)
            dir = getCacheDir();
        else
        	;
        
        if(dir==null)
            return;
        else
        	;
        
        java.io.File[] children = dir.listFiles();
        try{
            for(int i=0;i<children.length;i++)
                if(children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else children[i].delete();
        }
        catch(Exception e){}
    }
}
