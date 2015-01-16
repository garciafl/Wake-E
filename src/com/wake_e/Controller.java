package com.wake_e;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.wake_e.exceptions.NoRouteFoundException;

import com.wake_e.model.Credentials;
import com.wake_e.model.Location;
import com.wake_e.model.Mail;
import com.wake_e.model.Slide;
import com.wake_e.model.sqlite.WakeEDBHelper;
import com.wake_e.services.AlarmIntentService;
import com.wake_e.services.AlarmSynchroIntentService;
import com.wake_e.services.deliverers.AgendaDeliverer;
import com.wake_e.services.deliverers.MailDeliverer;
import com.wake_e.services.deliverers.MeteoDeliverer;
import com.wake_e.services.managers.AlarmsManager;
import com.wake_e.services.managers.CredentialsManager;
import com.wake_e.services.managers.LocationsManager;
import com.wake_e.services.managers.SlidesManager;

/**
 * @brief Singleton and main controller of the application
 * @author Wake-E team
 */
public class Controller {
    // all our deliverers
    private AgendaDeliverer agendaDeliverer;
    private MailDeliverer mailDeliverer;
    private MeteoDeliverer meteoDeliverer;

    //the db helper
    private WakeEDBHelper db;

    //the app context
    private static Context context;

    //all our managers
    private SlidesManager slidesManager;
    private CredentialsManager credentialsManager;
    private AlarmsManager alarmsManager;
    private LocationsManager locationsManager;

    private static Controller controller;

    /**
     * @param context
     *            le contexte de l'application
     */
    private Controller(Context context) {
	super();
	Controller.context = context;
	this.db = new WakeEDBHelper(context);

	this.agendaDeliverer = new AgendaDeliverer(context);
	this.mailDeliverer = new MailDeliverer(db);
	this.meteoDeliverer = new MeteoDeliverer();

	this.slidesManager = new SlidesManager(context, db);
	this.credentialsManager = new CredentialsManager(db);
	this.alarmsManager = new AlarmsManager();
	this.locationsManager= new LocationsManager(context, db); 
    }

    /**
     * @brief get the Controller instance
     * @param context
     *            the app context
     * @return the Controller instance
     */
    public static Controller getInstance(Context context) {
	if (Controller.controller == null) {
	    Controller.controller = new Controller(context);
	}
	return Controller.controller;
    }

    /**
     * @brief Static context getter
     * @return the context
     */
    public static Context getContext() {
	return Controller.context;
    }

    // ########### SLIDES ###########
    /**
     * @brief retrive the manager
     * @return slideManager object
     */
    public SlidesManager getSlideManager() {
	return this.slidesManager;
    }

    /**
     * @brief retrieve visible fragments
     * @return the visible fragments
     */
    public List<Fragment> getVisibleFragments() {
	return this.slidesManager.getVisibleFragments(Controller.context);
    }


    /**
     * @brief update slides
     * @param slides
     *            the slides
     */
    public void updateSlides(List<Slide> slides) {
	this.slidesManager.updateSlides(slides);
    }

    /**
     * @brief get all slides
     * @return all slides
     */
    public List<Slide> getSlides() {
	return this.slidesManager.getSlides();
    }

    //########### CREDENTIALS ###########

    /**
     * @brief update credentials
     * @param c the credentials
     */
    public void updateCredentials(Credentials c){
	this.credentialsManager.updateCredentials(c);
    }

    /**
     * @brief get credentials
     * @return credentials
     */
    public List<Credentials> getCredentials() {
	return this.credentialsManager.getCredentials();
    } 

    /**
     * @param type the needed type.
     * @return a credential or null.
     */
    public Credentials getCredentials(String type) {
	return this.credentialsManager.getCredentials(type);
    }

    public void deleteCredentials(String type) {
	this.credentialsManager.deleteCredentials(type);
    }

    // ########### ALARMS ###########

    /**
     * @brief create a new alarm
     * @param context the app context
     * @param depart the start location
     * @param arrivee the end location
     * @param preparationDuration the preparation duration
     * @param ringtone the ringtone
     */
    public void createAlarm(Context context, Location depart, Location arrivee,
	    long preparationDuration, String ringtone, String transport, long endHour) throws NoRouteFoundException{
	this.alarmsManager.createAlarm(context, depart, arrivee,
		preparationDuration, ringtone, transport, endHour);
    }


    /**
     * @brief enable or disable an alarm
     * @param alarmId
     * @param enabled TRUE=enabled FALSE=disabled
     * @param context 
     */
    public void enableAlarm(boolean enabled, Context context){
	this.alarmsManager.enableAlarm(enabled, context);
    }
    
    /**
     * @brief get all alarms
     * @return all alarms
     */
    public AlarmIntentService getAlarm(){
	return this.alarmsManager.getAlarm();
    }

    /**
     * @brief get the alarm synchro
     * @return the alarm synchro
     */
    public AlarmSynchroIntentService getAlarmSynchro(){
    	return this.alarmsManager.getAlarmSynchro();
    }

    /**
     * @brief enable the alarm synchro
     * @param enabled TRUE=enabled FALSE=disabled
     * @param context 
     */
    public void enabledAlarmSynchro(boolean enabled, Context context){
    	this.alarmsManager.enableAlarmSynchro(enabled, context);
    }
    
    /**
     * @brief get the estimated wake up hour
     * @return the estimated wake up hour
     */
    public String getWakeUpHour(){
	return this.alarmsManager.getWakeUpHour();
    }

    // ########### LOCATIONS ###########
    /**
     * @brief creer une Location
     * @param name le nom de la Location
     * @param address l'adresse de la Location
     * @return une Location
     * @throws IOException
     */
    public Location createLocation(String name, String address) throws IOException{
	return this.locationsManager.createLocation(name, address, this.db);
    }

    /**
     * @brief vérifier si une location porte un nom donné
     * @param name un nom donné 
     * @return TRUE ou FALSE
     */
    public boolean locationExists(String name){
	return this.locationsManager.exists(name);
    }

    // ########### MAILS ###########
    public List<Mail> getMails() {
	return this.db.getEmails();
    }
    /**
     * @brief get the MailDeliverer
     * @return the MailDeliverer
     */
    public MailDeliverer getMailDeliverer() {
	return this.mailDeliverer;
    }

    // ########### AGENDA ###########
    /**
     * @brief get the AgendaDeliverer
     * @return the AgendaDeliverer
     */
    public AgendaDeliverer getAgendaDeliverer(){
	return this.agendaDeliverer;
    }

    // ########### METEO ###########
    /**
     * @brief get the MeteoDeliverer
     * @return the MeteoDeliverer
     */
    public MeteoDeliverer getMeteoDeliverer(){
    	return this.meteoDeliverer;
    }


	public Set<Location> getLocations() {
		return this.locationsManager.getLocations();
	}
}
