package com.wake_e;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wake_e.adapt.MyPagerAdapter;

public class MainActivity extends FragmentActivity {

	LinearLayout ll;
	RelativeLayout relative;
	float positionSlider;
	float sizeSlider;
	private PagerAdapter mPagerAdapter;
	public static MainActivity that;
	public static ViewPager pager;
	private ImageView active;
	private TextView heureProg;
	private TextView textHeure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		that = this;

		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.home_page);
		// Creation de la liste de Fragments que fera defiler le PagerAdapter
		List<Fragment> fragments = Controller.getInstance(this.getApplicationContext()).getVisibleFragments();

		// Creation de l'adapter qui s'occupera de l'affichage de la liste de
		// Fragments
		this.mPagerAdapter = new MyPagerAdapter(
				super.getSupportFragmentManager(), fragments);

		heureProg = (TextView) this.findViewById(R.id.id_heure_estimee);
		textHeure = (TextView) this.findViewById(R.id.textView2);
		pager = (ViewPager) super.findViewById(R.id.pager);
		pager.setAdapter(this.mPagerAdapter);

		ll = (LinearLayout) findViewById(R.id.id_station);
		ll.setOnTouchListener(touchListenerBouton1);
		relative = (RelativeLayout) findViewById(R.id.id_fullStation);

		this.setVisible(false);
		ll.setY(0);
		pager.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ImageView settings = (ImageView) findViewById(R.id.parametre);
		settings.setOnClickListener(switchToSettings);

		ImageView config = (ImageView) findViewById(R.id.reveil);
		config.setOnClickListener(switchToConfig);
		
		active = (ImageView) findViewById(R.id.id_active);
		if (Controller.getInstance(that).getAlarm() != null &&
				Controller.getInstance(that).getAlarm().isEnabled()){
			active.setImageResource(R.drawable.w_active);
		}
		else {
			active.setImageResource(R.drawable.w_inactive);
		}
		active.setOnClickListener(activeDesactiveReveil);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		positionSlider = pager.getHeight();
		ll.setY(positionSlider);
		this.setVisible(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ss, menu);
		return true;
	}


	public void onClick(View v) {
	    Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
	    startActivity(i);
	}

	private OnTouchListener touchListenerBouton1 = new View.OnTouchListener() {
		/**
		 * Old Value
		 */
		private float yy = 0;

		@Override
		public boolean onTouch(final View v, MotionEvent event) {
	    	switch(event.getAction())
	    	{
	    		case MotionEvent.ACTION_DOWN:
	    	    	yy = event.getY();
	    			break;
	    		case MotionEvent.ACTION_MOVE:
	    			v.setY(v.getY() - yy + event.getY());
	    			if (v.getY() > positionSlider) v.setY(positionSlider);
	    			if (v.getY() < 0){v.setY(0);}
	    			break;
	    		case MotionEvent.ACTION_UP:
	    			v.setY(v.getY() - yy + event.getY());
	    			
	    			if (v.getY() > positionSlider){ v.setY(positionSlider);}
	    			else if (v.getY() < 0){v.setY(0);}
	    			else if (v.getY() < positionSlider/2){
			          while(v.getY() <= 0){
			        	v.setY(v.getY() - 3);
			        	try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			          }
			          v.setY(0);
	    			}
	    			else{
				          while(v.getY() >= positionSlider){
					        	v.setY(v.getY() - 3);
					        	try {
									Thread.sleep(400);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					          }
					          v.setY(positionSlider);
					          
	    			}
	    			break;
	    	}
	    	relative.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) v.getY()));
		    return true;
		}
	};

	private OnClickListener switchToSettings = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(i);
		}
	};
	private OnClickListener switchToConfig = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(getApplicationContext(), ConfigActivity.class);
			startActivity(i);
		}
	};
	
	private OnClickListener activeDesactiveReveil = new OnClickListener(){
		
		@Override
		public void onClick(View v) {
			if (Controller.getInstance(that).getAlarm() != null){
				if (Controller.getInstance(that).getAlarm().isEnabled()){
					Controller.getInstance(that).enableAlarm(false, that);
					active.setImageResource(R.drawable.w_inactive);
					heureProg.setText("__:__");
					Toast.makeText(that, "L'alarme a été désactivé", Toast.LENGTH_LONG).show();
				}
				else{
					Controller.getInstance(that).enableAlarm(true, that);
					active.setImageResource(R.drawable.w_active);
					heureProg.setText(Controller.getInstance(that).getAlarm().);
					Toast.makeText(that, "L'alarme a été activé", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(that, "Vous devez paramétrer l'alarme avant de l'activer", Toast.LENGTH_LONG).show();
			}
		}
	};
}