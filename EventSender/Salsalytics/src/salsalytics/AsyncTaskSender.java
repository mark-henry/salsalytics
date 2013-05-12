package salsalytics;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsynTaskSender is the channel in which the Salsalytics 
 * wrapper sends data to the users Google App Engine site.   
 * 
 * @author Brandon Page, brpage@calpoly.edu
 */
class AsyncTaskSender extends AsyncTask<URL, Integer, Long> {
        private Event event;

        AsyncTaskSender(Event event) {
        	this.event = event;
        }

        /**
         * This method executes the get request in the Event 
         * class of the Salsalytics wrapper.  This method is
         * executed asynchronously from the calling thread.
         * 
         * @param params the URL's to send the data to
         * 
         * @return the return status of the get request
         *  if successful, null otherwise.  
         */
        @Override
        protected Long doInBackground(URL... params) {

                Long status = null;
                try {
                        status = Long.valueOf(event.send());
                        Log.i("Salsalytics", "Web request return status: " + status);

                        return status;
                } catch (MalformedURLException e) {
                        Log.e("Malformed String", e.getMessage());
                } catch (IOException e) {
                        Log.e("IOException", e.getMessage());
                }
                
                Log.e("Salsalytics", " Error: Failure to send data, error code: " + 
                 status);
                
                return null;
        }
}
