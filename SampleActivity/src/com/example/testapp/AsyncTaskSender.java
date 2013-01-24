package com.example.testapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskSender extends AsyncTask<URL, Integer, Long> {
	private Event sender = new Event();
	
	@Override
	protected Long doInBackground(URL... params) {

		Long status;
		try {
			status = Long.valueOf(sender.get());
			Log.i("web request return status", ""+status);
			
			return status;
		} catch (MalformedURLException e) {
			Log.d("Malformed String", e.getMessage());
		} catch (IOException e) {
			Log.d("IOException", e.getMessage());
		}
		
		return null;
	}
	
	protected void addData(String name, String attributes){
		sender.addData(name, attributes);
	}
}
