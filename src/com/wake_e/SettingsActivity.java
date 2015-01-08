package com.wake_e;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	public static SettingsActivity that;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		that = this;
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.settings);
		TextView agenda = (TextView) findViewById(R.id.agenda);
		TextView mail = (TextView) findViewById(R.id.mail);
		TextView home = (TextView) findViewById(R.id.home);
		TextView dest = (TextView) findViewById(R.id.destinations);
		TextView slides = (TextView) findViewById(R.id.slides);

		agenda.setOnClickListener(agendaSync);
		mail.setOnClickListener(mailSync);
		home.setOnClickListener(homeSettings);
		dest.setOnClickListener(destSettings);
		slides.setOnClickListener(slideSettings);
		
	    Log.i("Mail TOKEN", Controller.getInstance(getApplicationContext()).getCredentials().toString());
	}

	private OnClickListener agendaSync = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(that, CredentialActivity.class);
			intent.putExtra("type", "calendar");
		    startActivity(intent);
		}
	};

	private OnClickListener mailSync = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(that, CredentialActivity.class);
			intent.putExtra("type", "gmail");
		    startActivity(intent);
		}
	};

	private OnClickListener homeSettings = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	private OnClickListener destSettings = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	private OnClickListener slideSettings = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};
}
