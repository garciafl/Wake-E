package com.wake_e;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.datatype.Duration;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.directions.route.Routing.TravelMode;
import com.wake_e.model.Credentials;
import com.wake_e.model.Location;
import com.wake_e.model.Meteo;
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

    // the db helper
    private WakeEDBHelper db;

    // the app context
    private Context context;

    // all our managers
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
	//this.agendaDeliverer = new AgendaDeliverer(null);
	this.mailDeliverer = new MailDeliverer();
	this.meteoDeliverer = new MeteoDeliverer();

	this.context = context;
	this.db = new WakeEDBHelper(context);
	this.slidesManager = new SlidesManager(context, db);
	this.credentialsManager = new CredentialsManager(db);
	this.alarmsManager = new AlarmsManager();
	this.locationsManager = new LocationsManager(context, db);
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

    // ########### SLIDES ###########
    /**
     * @brief retrieve visible fragments
     * @return the visible fragments
     */
    public List<Fragment> getVisibleFragments() {
	return this.slidesManager.getVisibleFragments(this.context);
    }

    /**
     * @brief update slides
     * @param slides
     *            the slides
     */
    public void updateSlides() {
	this.slidesManager.updateSlides(this.db);
    }

    /**
     * @brief get all slides
     * @return all slides
     */
    public List<Slide> getSlides() {
	return this.slidesManager.getSlides();
    }

    // ########### CREDENTIALS ###########

    /**
     * @brief update credentials
     * @param c
     *            the credentials
     */
    public void updateCredentials(Credentials c) {
	this.credentialsManager.updateCredentials(this.db, c);
    }

    /**
     * @brief get credentials
     * @return credentials
     */
    public List<Credentials> getCredentials() {
	return this.credentialsManager.getCredentials();
    }

    /**
     * @param type
     *            the needed type.
     * @return a credential or null.
     */
    public Credentials getCredentials(String type) {
	return this.credentialsManager.getCredentials(type);
    }

    public void deleteCredentials(String type) {
	this.credentialsManager.deleteCredentials(this.db, type);
    }

    // ########### ALARMS ###########

    /**
     * @brief create a new alarm
     * @param context
     *            the app context
     * @param depart
     *            the start location
     * @param arrivee
     *            the end location
     * @param preparationDuration
     *            the preparation duration
     * @param ringtone
     *            the ringtone
     */
    public void createAlarm(Context context, Location depart, Location arrivee,
	    Duration preparationDuration, String ringtone, TravelMode transport) {
	this.alarmsManager.createAlarm(context, depart, arrivee,
		preparationDuration, ringtone, transport);
    }

    /**
     * @brief delete an alarm
     * @param alarmId
     */
    public void deleteAlarm(UUID alarmId) {
	this.alarmsManager.removeAlarm(alarmId);
    }

    /**
     * @brief enable or disable an alarm
     * @param alarmId
     * @param enabled
     *            TRUE=enabled FALSE=disabled
     * @param context
     */
    public void enableAlarm(UUID alarmId, boolean enabled, Context context) {
	this.alarmsManager.enableAlarm(alarmId, enabled, context);
    }

    /**
     * @brief get the enabled alarm
     * @return the enabled alarm
     */
    public AlarmIntentService getEnabledAlarm() {
	return this.alarmsManager.getEnabledAlarm();
    }

    /**
     * @brief get all alarms
     * @return all alarms
     */
    public Set<AlarmIntentService> getAlarms() {
	return this.alarmsManager.getAlarms();
    }

    /**
     * @brief get the alarm synchro
     * @return the alarm synchro
     */
    public AlarmSynchroIntentService getAlarmSynchro() {
	return this.alarmsManager.getAlarmSynchro();
    }

    /**
     * @brief enable the alarm synchro
     * @param enabled
     *            TRUE=enabled FALSE=disabled
     * @param context
     */
    public void enabledAlarmSynchro(boolean enabled, Context context) {
	this.alarmsManager.enableAlarmSynchro(enabled, context);
    }

    // ########### LOCATIONS ###########

    public Location createLocation(String address) throws IOException {
	return this.locationsManager.createLocation(address, this.db);
    }

    /**
     * @brief get the AgendaDeliverer
     * @return the AgendaDeliverer
     */
    public AgendaDeliverer getAgendaDeliverer() {
	return this.agendaDeliverer;
    }

    /**
     * @brief get the MeteoDeliverer
     * @return the MeteoDeliverer
     */
    public MeteoDeliverer getMeteoDeliverer() {
	return null;
    }


	public Set<Location> getLocations() {
		// TODO Auto-generated method stub
		return this.locationsManager.getLocations();
	}
}
