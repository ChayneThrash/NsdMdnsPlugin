package com.koalasafe.cordova.plugin.multicastdns;

import android.content.Context;
import android.util.Log;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import java.net.InetAddress;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class MDnsNsdPlugin extends CordovaPlugin {
	private static final String TAG = "MDnsNsdPlugin";

	// Network Service Discovery related members
	// This allows the app to discover the garagedoor.local
	// "service" on the local network.
	// Reference: http://developer.android.com/training/connect-devices-wirelessly/nsd.html
	private NsdManager mNsdManager;
	private NsdManager.DiscoveryListener mDiscoveryListener;
	private NsdManager.ResolveListener mResolveListener;
	private NsdServiceInfo mServiceInfo;
	public String mRPiAddress;

	private static final String SERVICE_TYPE = "_workstation._tcp.";
	private static final String HOST_NAME = "Chayne-PC";

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {

		super.initialize(cordova, webView);

		mRPiAddress = "";
		mNsdManager = (NsdManager)(this.cordova.getActivity().getApplicationContext().getSystemService(Context.NSD_SERVICE));
		initializeResolveListener();
		initializeDiscoveryListener();
		mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
	}

	@Override
	public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
		if (action.equals("Get Host Ip")) {
			if (!mRPiAddress.equals("")) {
				callbackContext.success(mRPiAddress);
			} else {
				callbackContext.error("Host never resolved.");
			}
		} else if (action.equals("initialize")) {

		} else {
			callbackContext.error("Invalid action supplied");
			return false;
		}
		return true;
	}

	private void initializeDiscoveryListener() {

			// Instantiate a new DiscoveryListener
			mDiscoveryListener = new NsdManager.DiscoveryListener() {

		    //  Called as soon as service discovery begins.
		    @Override
		    public void onDiscoveryStarted(String regType) {
		    }

		    @Override
		    public void onServiceFound(NsdServiceInfo service) {
		        // A service was found!  Do something with it.
		        String name = service.getServiceName();
		        String type = service.getServiceType();
		        Log.d("NSD", "Service Name=" + name);
		        Log.d("NSD", "Service Type=" + type);
		        if (type.equals(SERVICE_TYPE) && name.contains("HOST_NAME")) {
		            Log.d("NSD", "Service Found @ '" + name + "'");
		            mNsdManager.resolveService(service, mResolveListener);
		        }
		    }

		    @Override
		    public void onServiceLost(NsdServiceInfo service) {
		        // When the network service is no longer available.
		        // Internal bookkeeping code goes here.
		    }

		    @Override
		    public void onDiscoveryStopped(String serviceType) {
		    }

	     	@Override
		    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
		        mNsdManager.stopServiceDiscovery(this);
		    }

		    @Override
		    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
		        mNsdManager.stopServiceDiscovery(this);
		    }
     	};
 	}

 	private void initializeResolveListener() {
    	mResolveListener = new NsdManager.ResolveListener() {

	        @Override
	        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
	            // Called when the resolve fails.  Use the error code to debug.
	            Log.e("NSD", "Resolve failed" + errorCode);
	        }

	        @Override
	        public void onServiceResolved(NsdServiceInfo serviceInfo) {
	            mServiceInfo = serviceInfo;

	            // Port is being returned as 9. Not needed.
	            //int port = mServiceInfo.getPort();

	            InetAddress host = mServiceInfo.getHost();
	            String address = host.getHostAddress();
	            Log.d("NSD", "Resolved address = " + address);
	            mRPiAddress = address;
	        }
    	};
 	}
}