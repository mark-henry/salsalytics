package com.salsaforce.happyllamafarmer;

import java.util.TreeMap;

import com.salsaforce.salsalytics.EventSender;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class DifficultyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Button easyButton;
		Button mediumButton;
		Button hardButton;
		Button llamaButton;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_difficulty);
		
		EventSender.setURL("http://freshsalsaforce.appspot.com/ReceiveEvents");
		easyButton = (Button) findViewById(R.id.easyButton);
		mediumButton = (Button) findViewById(R.id.mediumButton);
		hardButton = (Button) findViewById(R.id.hardButton);
		llamaButton = (Button) findViewById(R.id.llamaButton);
		
		easyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TreeMap<String, String> map = new TreeMap<String, String>();
				
				map.put("LlamaDifficulty", "Easy");
				EventSender.sendData("HappyLlamaFarmer", map);
				map.clear();
				showPopUp();
			}
		});
		
		mediumButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TreeMap<String, String> map = new TreeMap<String, String>();
				
				map.put("LlamaDifficulty", "Medium");
				EventSender.sendData("HappyLlamaFarmer", map);
				map.clear();
				showPopUp();
			}
		});
		
		hardButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TreeMap<String, String> map = new TreeMap<String, String>();
				
				map.put("LlamaDifficulty", "Hard");
				EventSender.sendData("HappyLlamaFarmer", map);
				map.clear();
				showPopUp();
			}
		});
		
		llamaButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TreeMap<String, String> map = new TreeMap<String, String>();
				
				map.put("LlamaDifficulty", "Llama Hard");
				EventSender.sendData("HappyLlamaFarmer", map);
				map.clear();
				showPopUp();
			}
		});
	}
	
	private void showPopUp() {
		final Intent j = new Intent(this, MainActivity.class);
   	 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
   	 helpBuilder.setTitle("Happy Llama Farmer");
   	 helpBuilder.setMessage("Difficulty Chosen!");
   	 helpBuilder.setPositiveButton("OK",
   	   new DialogInterface.OnClickListener() {
   	    public void onClick(DialogInterface dialog, int which) {
   	     // Do nothing but close the dialog
   	    	startActivity(j);
   	    }
   	 });
   	 AlertDialog helpDialog = helpBuilder.create();
   	 helpDialog.show();
   }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.difficulty, menu);
		return true;
	}

}
