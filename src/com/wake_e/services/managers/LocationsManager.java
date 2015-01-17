package com.wake_e.services.managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;

import com.wake_e.model.Location;
import com.wake_e.model.sqlite.WakeEDBHelper;
import com.wake_e.utils.Point;

/**
 * @brief Management of all locations
 * @author Wake-E team
 */

public class LocationsManager {
	//Locations
	private List<Location> locations;

	//tools
	private Geocoder geocoder;
	private LocationManager locationManager;



	/**
	 * 
	 */
	public LocationsManager(Context context, WakeEDBHelper db) {
		super();
		this.locations = new ArrayList<Location>();
		this.geocoder = new Geocoder(context);
		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.loadLocations(db);
	}

	/**
	 * @brief add a location
	 * @param location the location to add
	 */
	public void addLocation(Location location) {
		this.locations.add(location);
	}


	/**
	 * @brief remove a location
	 * @param name the name of the location to remove
	 */
	public void removeLocation(String name) {
		for(Location l : locations){
			if(l.getName().equals(name)){
				locations.remove(l);
				return;
			}
		}
	}

	/**
	 * @brief retrieve a location
	 * @param name the name of the location
	 * @return the location or null
	 */

	public Location getLocation(String name) {
		for(Location l : locations){
			if(l.getName().equals(name)){
				return l;
			}
		}
		return null;
	}

	/**
	 * @brief create a new Location
	 * @param address
	 * @return a new address or null
	 * @throws IOException
	 */
	public Location createLocation(String name, String address, WakeEDBHelper db) throws IOException {

		//Get addresses
		List<Address> addresses = this.geocoder.getFromLocationName(address, 1);
		Location l;
		//If we got an address
		if(addresses.size() > 0) {
			double latitude= addresses.get(0).getLatitude();
			double longitude= addresses.get(0).getLongitude();
			Point p = new Point(latitude, longitude);

			String city = addresses.get(0).getLocality();
			String cp = addresses.get(0).getPostalCode();

			String address_line = addresses.get(0).getAddressLine(0);

			//If this Location already exists
			if((l=this.getLocation(p)) == null){
				l = new Location(name, p, address, city, cp, address_line);
				this.addLocation(l);
			} else {
				db.createLocation(l);
				loadLocations(db);
			}
			return l;
		}
		return null;
	}

	/**
	 * @brief get a Location from a Point
	 * @param p a point
	 * @return a Location or null
	 */
	public Location getLocation(Point p) {
		for(Location l : this.locations){
			if(l.getGps().equals(p)){
				return l;
			}
		}
		return null;
	}

	/**
	 * @brief load all locations
	 * @param db
	 */
	private void loadLocations(WakeEDBHelper db) {
		this.locations.addAll(db.getLocations());
	}

	public boolean exists(String name){
		for(Location l : this.locations){
			if(l.getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public List<Location> getLocations() {
		return this.locations;
	}

}
