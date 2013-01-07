package com.github.yabo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class NetworkUtils {
	public final static int NONE = 0; 	// None
	public final static int WIFI = 1; 	// Wi-Fi
	public final static int MOBILE = 2; // 3G,GPRS

	public static int getNetworkState(Context context) 
	{
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Mobile Network
		State state = connManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState();
		if (state == State.CONNECTED || state == State.CONNECTING)
		{
			return MOBILE;
		}

		// Wifi
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING)
		{
			return WIFI;
		}
		return NONE;
	}
}