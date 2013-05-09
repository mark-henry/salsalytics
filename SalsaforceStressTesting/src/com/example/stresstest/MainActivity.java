package com.example.stresstest;

import java.sql.Date;
import java.util.TreeMap;

import Salsalytics.EventSender;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		Button send100Button, send1000Button, send4000Button, send5000Button;
		
		EventSender.setURL("http://salsaforcetests.appspot.com/ReceiveEvents");
		
		send100Button = (Button) findViewById(R.id.send100);
		send1000Button = (Button) findViewById(R.id.send1000);
		send4000Button = (Button) findViewById(R.id.send4000);
		send5000Button = (Button) findViewById(R.id.send5000);
		
        send100Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeMap<String, String> map = new TreeMap<String, String>();
                map.put("TestItem", "1");
                Date d1 = new Date(System.currentTimeMillis());
                for (int i = 0; i < 100; i++) {
	                EventSender.sendData("StressTesting", map);
                }
                long interval = System.currentTimeMillis() - d1.getTime();
                
                Toast.makeText(getApplicationContext(), "Time: " + interval,
                		Toast.LENGTH_LONG).show();
                map.clear();
                showPopUp();
            }
        });
		
		return true;
	}
	
	private void showPopUp() {
		final Intent j = new Intent(this, MainActivity.class);
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle("Sent Events!");
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

}
