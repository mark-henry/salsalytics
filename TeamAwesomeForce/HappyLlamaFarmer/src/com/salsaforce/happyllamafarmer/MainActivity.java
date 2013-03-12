package com.salsaforce.happyllamafarmer;

import java.util.TreeMap;

import com.salsaforce.salsalytics.*;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Button redLlamaButton;
		Button greenLlamaButton;
		Button blueLlamaButton;
		Button yellowLlamaButton;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		EventSender.setURL("http://freshsalsaforce.appspot.com/ReceiveEvents");
		redLlamaButton = (Button) findViewById(R.id.redLlama);
		greenLlamaButton = (Button) findViewById(R.id.greenLlama);
		blueLlamaButton = (Button) findViewById(R.id.blueLlama);
		yellowLlamaButton = (Button) findViewById(R.id.yellowLlama);
		
		redLlamaButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TreeMap<String, String> map = new TreeMap<String, String>();
				
				map.put("Llama", "Red");
				EventSender.sendData("HappyLlamaFarmer", map);
				map.clear();
				showPopUp();
			}
		});
		
		greenLlamaButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TreeMap<String, String> map = new TreeMap<String, String>();
				
				map.put("Llama", "Green");
				EventSender.sendData("HappyLlamaFarmer", map);
				map.clear();
				showPopUp();
			}
		});
		
		blueLlamaButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TreeMap<String, String> map = new TreeMap<String, String>();
				
				map.put("Llama", "Blue");
				EventSender.sendData("HappyLlamaFarmer", map);
				map.clear();
				showPopUp();
			}
		});
		
		yellowLlamaButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TreeMap<String, String> map = new TreeMap<String, String>();
				
				map.put("Llama", "Yellow");
				EventSender.sendData("HappyLlamaFarmer", map);
				map.clear();
				showPopUp();
			}
		});
	}
	
	private void showPopUp() {

   	 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
   	 helpBuilder.setTitle("API Call");
   	 helpBuilder.setMessage("Llama chosen!");
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
