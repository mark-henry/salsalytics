package com.salsaforce.sampleapp;

//import com.salsalytics.apiwrapper.EventSender;
import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import Salsalytics2.*;


public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Button maleButton;
    	Button femaleButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        EventSender.setURL("http://freshsalsaforce.appspot.com/ReceiveEvents");
        maleButton = (Button) findViewById(R.id.malebutton);
        maleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TreeMap<String, String> map = new TreeMap<String, String>();
				
				map.put("Gender", "Male");
				EventSender.sendData("test_login", map);
				map.clear();
				showPopUp();
			}
		});
        
        femaleButton = (Button) findViewById(R.id.femalebutton);
        femaleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TreeMap<String, String> map = new TreeMap<String, String>();				
				map.put("Gender", "Female");
				EventSender.sendData("test_login", map);
				map.clear();
				showPopUp();
			}
		});
    }

    private void showPopUp() {

    	 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
    	 helpBuilder.setTitle("API Call");
    	 helpBuilder.setMessage("Data will be sent to the Salesforce Database");
    	 helpBuilder.setPositiveButton("OK",
    	   new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	     // Do nothing but close the dialog
    	    }
    	 });
    	 AlertDialog helpDialog = helpBuilder.create();
    	 helpDialog.show();
    }    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
