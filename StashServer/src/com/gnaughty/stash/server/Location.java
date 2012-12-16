package com.gnaughty.stash.server;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gnaughty.stash.server.exception.InvalidLocationDataException;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable="true")
public class Location {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent(mappedBy = "location")
	private Account owner = null;
	@Persistent
	private Double longitude_west = null;
	@Persistent
	private Double longitude_east = null;
	@Persistent
	private Double latitude_south = null;
	@Persistent
	private Double latitude_north = null;
	@Persistent
	private Long timeout = null;
	@Persistent
	private Long coins = null;
	@Persistent
	private List<Location> adjacentLocations = null;
	
	public final static long MIN_COINS = 0;
	public final static long MAX_COINS = 5;
	public final static String COINS = "coins";
	public final static String LONGITUDE = "longitude";
	public final static String LATITUDE = "latitude";
	public final static String TIMEOUT = "timeout";
	public final static String RADIUS = "radius";

	public Location(double centreLongitude, double centreLatitude, int radius, long timeout) throws InvalidLocationDataException{
		if(centreLongitude < -180 || centreLongitude > 180 ||
				centreLatitude < -90 || centreLatitude > 90 ||
				radius < 0 || timeout < 0){
			throw new InvalidLocationDataException();
		}
		
		Double new_longitude_west = new Double(centreLongitude-radius);
		Double new_longitude_east = new Double(centreLongitude+radius);
		Double new_latitude_south = new Double(centreLatitude-radius);
		Double new_latitude_north = new Double(centreLatitude+radius);
		
		/* The antimeridian is the 180deg meridian that is opposite to the prime meridian in Greenwich
		 * To simplify the logic in creating the start and north longitude, Locations can not span
		 * across the antimeridian.   Sucks if you live in Russia, Fiji or Antartica!  Let's hope
		 * they are not our target audiences ;)
		 */
		if(new_longitude_west<-180) new_longitude_west=new Double(-180);
		if(new_longitude_east>180) new_longitude_east=new Double(180);
		/* As above, sucks if you live at the north or south poles!
		 */
		if(new_latitude_south<-90) new_latitude_south=new Double(-90);
		if(new_latitude_north>90) new_latitude_north=new Double(90);
		
		setLongitude_east(new_longitude_east);
		setLongitude_west(new_longitude_west);
		setLatitude_north(new_latitude_north);
		setLatitude_south(new_latitude_south);
		
		setTimeout(System.currentTimeMillis()+timeout);
		setCoins(new Long(MIN_COINS + (long)(Math.random() * ((MAX_COINS - MIN_COINS) + 1))));
	}

	public boolean contains(double longitude, double latitude){
		if( (getLongitude_east().compareTo(longitude) > 0) &&
			(getLongitude_west().compareTo(longitude) < 0) &&
			(getLatitude_north().compareTo(latitude) > 0) &&
			(getLatitude_south().compareTo(latitude) < 0)){
				return true;
			}
		return false;
	}
	
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	public Double getLongitude_west() {
		return longitude_west;
	}

	public void setLongitude_west(Double longitude_west) {
		this.longitude_west = longitude_west;
	}

	public Double getLongitude_east() {
		return longitude_east;
	}

	public void setLongitude_east(Double longitude_east) {
		this.longitude_east = longitude_east;
	}

	public Double getLatitude_south() {
		return latitude_south;
	}

	public void setLatitude_south(Double latitude_south) {
		this.latitude_south = latitude_south;
	}

	public Double getLatitude_north() {
		return latitude_north;
	}

	public void setLatitude_north(Double latitude_north) {
		this.latitude_north = latitude_north;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public Long getCoins() {
		return coins;
	}

	public void setCoins(Long coins) {
		this.coins = coins;
	}

	public List<Location> getAdjacentLocations() {
		return adjacentLocations;
	}

	public void setAdjacentLocations(List<Location> adjacentLocations) {
		this.adjacentLocations = adjacentLocations;
	}
}
