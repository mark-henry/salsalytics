package Salsalytics;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

class AsyncTaskSender extends AsyncTask<URL, Integer, Long> {
	private Event event;
	
	AsyncTaskSender(Event event) {
		this.event = event;
	}
	
	@Override
	protected Long doInBackground(URL... params) {

		Long status;
		try {
			status = Long.valueOf(event.get());
			Log.i("web request return status", ""+status);
			
			return status;
		} catch (MalformedURLException e) {
			Log.e("Malformed String", e.getMessage());
		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
		}
		
		return null;
	}
}
