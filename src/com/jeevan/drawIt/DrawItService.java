package com.jeevan.drawIt;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

public class DrawItService extends Service implements IOCallback {
	private SocketIO socket;
	private Messenger messenger = null;
	public boolean socketConnected = false;
		
	
	  
	private final BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {
	    @Override
	        public void onReceive(Context context, Intent intent) {
	    	NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

	    	if (networkInfo==null||!networkInfo.isConnected()) {
	    		sendConnectionStatus(false);
	    		socket.disconnect();
	    		socketConnected=false;
	    	}
	    	else {
	    		if (!socketConnected) {
	    			socketConnected=true;
	    			connectWebsocket();
	    			}
	        } 
	    }
	};


	
	public void sendMessage (String event, JSONObject jsonObject2) {
		socket.emit(event, jsonObject2);
	}
	
	
	
	public void addMessenger (Messenger messenger) {
		this.messenger = messenger;
	}
	

	
	public void connectWebsocket () {
		socket = new SocketIO();
		try {
			socket.connect(
					"http://ec2-46-137-155-119.eu-west-1.compute.amazonaws.com:80",
					this);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void onCreate() {
		IntentFilter mNetworkStateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetworkStateReceiver , mNetworkStateFilter);
		super.onCreate();
	}

	public class LocalBinder extends Binder {
		DrawItService getService() {
			return DrawItService.this;
		}
	}

	public void sendConnectionStatus (Boolean status) {
		Message msg = Message.obtain();
		Bundle bdl = new Bundle();
		bdl.putString("event", "connection");
		bdl.putBoolean("value", status);
		msg.setData(bdl);
		try {
			messenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public void onDestroy() {
		socket.disconnect();
		super.onDestroy();
	}

	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {
		try {
			Log.d("2","Server said:" + json.toString(2));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(String data, IOAcknowledge ack) {
		System.out.println("Server said: " + data);
	}

	@Override
	public void onError(SocketIOException socketIOException) {
		System.out.println("an Error occured");
		socketIOException.printStackTrace();
	}

	@Override
	public void onDisconnect() {
	}

	@Override
	public void onConnect() {
		sendConnectionStatus(true);
	}

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
			Message msg = Message.obtain();
			Bundle bdl = new Bundle();
			bdl.putString("event", event);
			for (Object arg : args)
			bdl.putString("value", arg.toString());
			msg.setData(bdl);
			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		


}


