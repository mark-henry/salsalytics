package com.salsaforce.happyllamafarmer2;
import java.util.Map;
import java.util.TreeMap;

import com.salsaforce.happyllamafarmer2.R;

import salsalytics.EventSender;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;


public class EpicLlamaFarmer extends Activity {
	TreeMap<String, String> attributesMap = new TreeMap<String, String>();
	TreeMap<String, String> constantData = new TreeMap<String, String>();
	Button sendButton, setAppNameButton, failButton;
	EditText appName, key1, value1, key2, value2, key3, value3;
	CheckBox cBox;
	Switch nameSwitch, maufactureSwitch, carrierSwitch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_features_layout);
		
		EventSender.setURL("http://freshsalsaforce.appspot.com/ReceiveEvents");
		EventSender.setAppName("HappyLlamaFarmer2");
		
		sendButton = (Button) findViewById(R.id.sendButton);
		setAppNameButton = (Button) findViewById(R.id.setAppNameButton);
		failButton = (Button) findViewById(R.id.failButton);
		appName = (EditText) findViewById(R.id.appName);
		key1 = (EditText) findViewById(R.id.key1);
		key2 = (EditText) findViewById(R.id.key2);
		key3 = (EditText) findViewById(R.id.key3);
		value1 = (EditText) findViewById(R.id.value1);
		value2 = (EditText) findViewById(R.id.value2);
		value3 = (EditText) findViewById(R.id.value3);
		cBox = (CheckBox) findViewById(R.id.checkBox1);
		nameSwitch = (Switch) findViewById(R.id.deviceNameSwitch);
		maufactureSwitch = (Switch) findViewById(R.id.manufactureSwitch);
		carrierSwitch = (Switch) findViewById(R.id.CarrierSwitch);
		
		constantData.put("BuildStage", "Beta");
		constantData.put("BestManagerEver", "Gene Rivera");
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String sKey1 = key1.getText().toString();
				String sValue1 = value1.getText().toString();
				String sKey2 = key2.getText().toString();
				String sValue2= value2.getText().toString();
				String sKey3 = key3.getText().toString();
				String sValue3 = value3.getText().toString();
				
				if(!sKey1.equals("") && !sValue1.equals(""))
					attributesMap.put(sKey1, sValue1);
				if(!sKey2.equals("") && !sValue2.equals(""))
					attributesMap.put(sKey2, sValue2);
				if(!sKey3.equals("") && !sValue3.equals(""))
					attributesMap.put(sKey3, sValue3);
				
				EventSender.sendData(getBaseContext(), "EpicDemo", attributesMap);
			}
		});
		
		failButton.setOnClickListener(new View.OnClickListener(){
			
			@Override
			public void onClick(View v) {
				String sKey1 = key1.getText().toString();
				String sValue1 = value1.getText().toString();
				String sKey2 = key2.getText().toString();
				String sValue2= value2.getText().toString();
				String sKey3 = key3.getText().toString();
				String sValue3 = value3.getText().toString();
				
				if(!sKey1.equals("") && !sValue1.equals(""))
					attributesMap.put(sKey1, sValue1);
				if(!sKey2.equals("") && !sValue2.equals(""))
					attributesMap.put(sKey2, sValue2);
				if(!sKey3.equals("") && !sValue3.equals(""))
					attributesMap.put(sKey3, sValue3);
				
				EventSender.sendData(null, "EpicDemo", attributesMap);
			}
		});
		
		setAppNameButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EventSender.setAppName(appName.getText().toString());
			}
		});
		
		cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if(isChecked) 
					EventSender.setConstantData(constantData);
				else
					EventSender.setConstantData(null);
			}
		});
		
		nameSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			
				EventSender.changeDeviceInformationCollected().setDeviceNameCollected(isChecked);
			}
		});
		
		this.maufactureSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			
				EventSender.changeDeviceInformationCollected().setManufactureCollected(isChecked);
			}
		});
		
		this.carrierSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			
				EventSender.changeDeviceInformationCollected().setWirelessServiceProviderCollected(isChecked);
			}
		});
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		EventSender.onResume(getBaseContext());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		EventSender.onPause(getBaseContext());
		
		
		/*
		 * This was used to test that the Salsalytics prefs are truly private
		 * 
		 * SharedPreferences llamaPrefs = getSharedPreferences("llama", Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = llamaPrefs.edit();
		
		editor.putString("This key", "should not be in SalsaPrefs!");
		editor.commit();
		
		/*
		 * salsalytics was listing all constatn data colleced with:
		 * 
			for (Map.Entry<String, String> entry : constantMap.entrySet()) {
				Log.v("Salsalytics", "constMap entry: " + entry.getKey() + ", "
						+ entry.getValue());
			}
		 * 
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

