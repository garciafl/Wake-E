package com.wake_e;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.wake_e.adapt.LocationAdapter;
import com.wake_e.constants.WakeEConstants;
import com.wake_e.fragment.station.PageAgendaFragment;
import com.wake_e.fragment.station.PageMailFragment;
import com.wake_e.fragment.station.PageMeteoFragment;
import com.wake_e.model.Credentials;
import com.wake_e.model.Location;
import com.wake_e.model.Slide;
import com.wake_e.tools.TokenRequester;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	private ImageView [] slides;
	private TextView cancel;
	private TextView save;
	private int size;
	public static SettingsActivity that;
	private List<Slide> dbSlide;
	private TextView addAccount;
	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		that = this;
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.settings);

		this.bundle = savedInstanceState;

		Display ecran = getWindowManager().getDefaultDisplay(); 
		int largeur= ecran.getWidth();
		
		dbSlide = Controller.getInstance(this).getSlides();

		slides = new ImageView[dbSlide.size()];
		
		size = getWindowManager().getDefaultDisplay().getWidth() / dbSlide.size();

		
		int i = 0;
		for (Slide s: dbSlide) {
			if (s.getSlideName().equals("Agenda")) {
				slides[i] = (ImageView)findViewById(R.id.agenda);
			} else if (s.getSlideName().equals("Mail")) {
				slides[i] = (ImageView)findViewById(R.id.mail);
			} else if (s.getSlideName().equals("Météo")) {
				slides[i] = (ImageView)findViewById(R.id.meteo);
			} else {
				slides[i] = (ImageView)findViewById(R.id.traffic);
			}
			slides[i].setVisibility(0);
			slides[i].setOnTouchListener(touchListenerBouton2);
			i++;
		}


		save = (TextView) this.findViewById(R.id.save);
		cancel = (TextView) this.findViewById(R.id.cancel);
		save.setOnClickListener(onSaveClick);
		cancel.setOnClickListener(onCancelClick);

		// ######## ACCOUNTS #########
		
		addAccount = (TextView)findViewById(R.id.addAccount);
		if (Controller.getInstance(this).getCredentials() != null) {
			addAccount.setCompoundDrawables(null, null, null, null);
			addAccount.setText("Gmail");
			addAccount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gmail, 0, 0, 0);
		}
		addAccount.setOnClickListener(credentialStart);

		// ####### LOCATIONS ########
		ListView locations	= (ListView)findViewById(R.id.l_locations);
		locations.setAdapter(new LocationAdapter(this, Controller.getInstance(this).getLocations()));

		TextView addLocation = (TextView)findViewById(R.id.Addlocalisation);
		addLocation.setOnClickListener(locationStart);

		// ####### SOUNDS ########
		ListView sounds = (ListView)findViewById(R.id.l_sounds);
	}

	@Override
	public void onWindowFocusChanged (boolean hasFocus) {

		for (int i = 0; i < dbSlide.size(); i++){
			slides[i].setX(size * i);
		}
	}

	private OnTouchListener touchListenerBouton2 = new View.OnTouchListener() {
		/**
		 * Old Value
		 */
		private float xx = 0 ;
		private int selection = 0;
		private ImageView tmp;


		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				xx = event.getX();
				selection = (int) Math.floor((xx + v.getX()) / size);
				for (int i = 0; i < dbSlide.size(); i++){
					slides[i].setX(size * i);
				}
				v.bringToFront();
				break;
			case MotionEvent.ACTION_MOVE:
				//Plus
				if (event.getX() + v.getX() > (selection+1) * size){
					if (selection != slides.length){
						tmp = slides[selection];
						slides[selection] = slides[selection+1];
						slides[selection+1] = tmp;
						selection ++;
					}
				}
				//Moins
				if (event.getX() + v.getX() < selection * size){
					if (selection != 0)
						tmp = slides[selection];
					slides[selection] = slides[selection-1];
					slides[selection-1] = tmp;
					selection --;
				}

				v.setX(v.getX() - xx + event.getX());
				if (v.getX() < 0){v.setY(0);}
				break;
			case MotionEvent.ACTION_UP:

				for (int i = 0; i < dbSlide.size(); i++){
					slides[i].setX(size * i);
				}
				break;
			}
			return true;
		}
	};

	private void pickUserAccount() {
	    String[] accountTypes = new String[]{"com.google"};
	    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
	            accountTypes, false, null, null, null, null);
	    startActivityForResult(intent, WakeEConstants.WakeEAPICalls.ACCOUNT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == WakeEConstants.WakeEAPICalls.ACCOUNT_CODE) {
				String user = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				TokenRequester.initiateRequest(that, user);
			}
		}
	}

	private OnClickListener credentialStart = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Credentials cr = Controller.getInstance(
					that.getApplicationContext()).getCredentials("gmail");
			if (cr != null) {
				TokenRequester.initiateRequest(that, cr.getUser());
			} else {
				pickUserAccount();
			}
		}
	};

	private OnClickListener locationStart = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final Dialog dialog = new Dialog(SettingsActivity.this);
			dialog.setContentView(R.layout.location);
			dialog.setTitle("Localisation");

			final EditText locationName = (EditText)dialog.findViewById(R.id.locationName);
			final EditText locationAddress = (EditText)dialog.findViewById(R.id.locationAddress);
			TextView createLoc = (TextView)dialog.findViewById(R.id.create);

			createLoc.setOnClickListener(new OnClickListener() {
				Location l = null;
				boolean hasError = false;

				@Override
				public void onClick(View v) {
					try {
						l = Controller.getInstance(v.getContext()).createLocation(
								locationName.getText().toString(),
								locationAddress.getText().toString());
					} catch (IOException e) {
						hasError = true;
					}
					if (hasError || l == null) {
						Toast.makeText(
								that,
								"Erreur lors de la création de la localisation",
								Toast.LENGTH_LONG).show();
					}
					dialog.cancel();
				}
			});

			dialog.show();

		}
	};

	private OnClickListener onSaveClick = new OnClickListener(){
		@Override
		public void onClick(View v) {
			//SAVE
			List<Slide> slideList = new ArrayList<Slide>();
			int order;
			String className;
			String name;
			boolean visible = true;

			int position = 0;

			for(ImageView view: slides) {
				name = null;
				className = null;
				order = position;
				visible = true;
				switch(view.getId()) {
				case(R.id.meteo) :
					name = "Météo";
				className = PageMeteoFragment.class.getName();
				break;
				case(R.id.mail) :
					name = "Mail";
				className = PageMailFragment.class.getName();
				break;
				case(R.id.agenda):
					name = "Agenda";
				className = PageAgendaFragment.class.getName();
				break;
				}
				if (name != null) {
					slideList.add(new Slide(name, className, order, visible));
					position++;
				}
			}
			Controller.getInstance(that).updateSlides(slideList);
			finish();
		}
	};


	private OnClickListener onCancelClick = new OnClickListener(){
		@Override
		public void onClick(View v) {
			//CANCEL
			finish();
		}
	};

}
