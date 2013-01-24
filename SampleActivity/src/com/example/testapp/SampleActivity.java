package com.example.testapp;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class SampleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Button maleButton;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		
		maleButton = (Button) findViewById(R.id.maleButton);
		maleButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AsyncTaskSender ats = new AsyncTaskSender();
				try {
					ats.addData("login", "Gender:Male");
					ats.execute(new URL("http://freshsalsaforce.appspot.com/freshsalsatothemax"));
				} catch (MalformedURLException e) {
					Log.e("Malformed String", e.getMessage());
				}
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sample, menu);
		return true;
	}

	
}
