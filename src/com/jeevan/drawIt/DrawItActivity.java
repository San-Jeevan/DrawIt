package com.jeevan.drawIt;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class DrawItActivity extends Activity {
	
	private DrawItService mBoundService;
	final Messenger activityMessenger = new Messenger(new IncomingHandler());
	private JSONObject jsonObject;
	DrawView drawViewglobal;

	
 
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	    	
	    	JSONObject jsonObject = null ;
	    	try {
	    		String event = msg.getData().getString("event");
	    		if (event.equals("draw")) {
	    			Point point = new Point();
	    			jsonObject = new JSONObject( msg.getData().getString("value"));
        	        point.x = jsonObject.getLong("x");
        	        point.y = jsonObject.getLong("y");
        	        point.state = jsonObject.getInt("type");
        	        drawViewglobal.addpoint(point);    
	    		}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
	                super.handleMessage(msg);
	    }
	}
	
	
	public void ClearCanvas (View v) {
		  try {
				mBoundService.sendMessage("drawClick",jsonObject.put("x", 2).put("y", 4).put("type", "clear"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			drawViewglobal.points.clear();
			drawViewglobal.CustomInvalidate();
			
			
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        jsonObject = new JSONObject();
        startService(new Intent(DrawItActivity.this, DrawItService.class));
        doBindService();
        drawViewglobal = (DrawView) findViewById(R.id.DrawView);
        final DrawView drawView = drawViewglobal;
          
         OnTouchListener l = new OnTouchListener() {
        	 
        		@Override
        		public boolean onTouch(View v, MotionEvent event) {
        			Point point = new Point();
        	        point.x = event.getX();
        	        point.y = event.getY();
        	        point.state = event.getActionMasked();
        	        drawView.addpoint(point);       	        
        	        try {
						mBoundService.sendMessage("drawClick",jsonObject.put("x", event.getX()).put("y", event.getY()).put("type", event.getActionMasked()));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        	        return true;
        		} };
         
         drawView.setOnTouchListener(l);
    }
    
    @Override
	protected void onDestroy() {
    	doUnbindService();
		stopService(new Intent(this, DrawItService.class));
		super.onDestroy();
	}
    
    
    private ServiceConnection mConnection = new ServiceConnection() {
    	
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((DrawItService.LocalBinder)service).getService();
            mBoundService.connectWebsocket();
            Log.d("J", "Binded successfully!");
            mBoundService.addMessenger(activityMessenger);
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    }; 
    
    void doBindService() {
        bindService(new Intent(DrawItActivity.this, 
                DrawItService.class), mConnection, Context.BIND_AUTO_CREATE);
     
    }
    
    void doUnbindService() {
    	unbindService(mConnection);
        }
    

  
}