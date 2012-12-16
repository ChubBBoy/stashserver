package com.gnaughty.stash.server;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gnaughty.stash.server.exception.DuplicateException;
import com.gnaughty.stash.server.exception.InvalidLocationDataException;
import com.gnaughty.stash.server.exception.ServerException;
import com.gnaughty.stash.server.exception.UnauthenticatedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable="true")
public class Account {

	private static final Logger log = Logger.getLogger(StashServerServlet.class.getName());

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private String authenticationKey;
	@Persistent
	private String email = null;
	@Persistent
	private Long firstName = null;
	@Persistent
	private Long middleName = null;
	@Persistent
	private Long lastName = null;
	@Persistent
	private Long coins = null;
	@Persistent
	private Boolean active = false;
	@Persistent(dependent = "true")
	private Location location = null;
	
	public final static String ID = "id";
	public final static String AUTHENTICATION_KEY = "auth_key";
	public final static String EMAIL = "email";
	public final static String FIRST_NAME = "first_name";
	public final static String MIDDLE_NAME = "middle_name";
	public final static String LAST_NAME = "last_name";
	public final static String COINS = "coins";
	public final static String ACTIVE = "active";
	
	private Account(){
		// you should never create an Account object, it should be "registered" or "retrieved"
	}
	
	public static Account register(PersistenceManager pm, String authKey, Long firstName, Long middleName, Long lastName, String email) throws DuplicateException, ServerException{
		// check for duplicate name
		if(exists(pm, firstName, middleName, lastName)){
			throw new DuplicateException();
		}
		Account newAccount = new Account();
		newAccount.setFirstName(firstName);
		newAccount.setMiddleName(middleName);
		newAccount.setLastName(lastName);
		newAccount.setEmail(email);
		String accountID = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
		// It's unlikely that you will generate a duplicate UUID, but just in case it's best to check
		// and retry
		for(int i = 0; !isAccountIDUnique(pm, accountID); i++){
			accountID = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
			if(i>5){    // we don't want to get into an endless loop
				log.severe("Unable to create unique AccountID");
				throw new ServerException();
			}
		}
		newAccount.setAuthenticationKey(authKey);
		newAccount.setCoins(new Long(0));
		newAccount.setActive(true);
        Key newKey = KeyFactory.createKey(Account.class.getSimpleName(), accountID);
        newAccount.setKey(newKey);

		pm.makePersistent(newAccount);
		
		return newAccount;
	}

	public static Account retrieveAccount(PersistenceManager pm, String id, String authKey) throws ServerException, UnauthenticatedException{
		Account currentAccount = null;
        Key key = KeyFactory.createKey(Account.class.getSimpleName(), id);
		currentAccount = pm.getObjectById(Account.class, key);
		
		if(currentAccount == null){
			log.severe("Unable to find account with AccountID["+id+"]");
			throw new ServerException();
		}

		if(currentAccount.getAuthenticationKey() != null && currentAccount.getAuthenticationKey().compareToIgnoreCase(authKey) != 0){
			throw new UnauthenticatedException();
		}
		
		return currentAccount;
	}

	public static boolean isAuthenticated(PersistenceManager pm, String id, String authKey) throws Exception{
		Account currentAccount = null;

        Key key = KeyFactory.createKey(Account.class.getSimpleName(), id);
		currentAccount = pm.getObjectById(Account.class, key);

		if(currentAccount == null){
			throw new UnauthenticatedException();
		}
		
		return isAuthenticated(pm, currentAccount, authKey);
	}

	public static boolean isAuthenticated(PersistenceManager pm, Account currentAccount, String authKey){
		if(currentAccount == null || (currentAccount.getAuthenticationKey() != null && currentAccount.getAuthenticationKey().compareToIgnoreCase(authKey) != 0)){
			return false;
		}
		
		return true;
	}
	
	public Location updateLocation(PersistenceManager pm, double longitude, double latitude, int radius, long timeout) throws InvalidLocationDataException{
		if(getLocation() != null){
			// check to see if the previous location hasn't timed out and you are still there
			if((getLocation().getTimeout().compareTo(new Long(System.currentTimeMillis()))>0) &&
			   (getLocation().contains(longitude, latitude))){    // check to see if you are still in the same location
					getLocation().setTimeout(System.currentTimeMillis()+timeout);
					return getLocation();
			}
			pm.deletePersistent(getLocation());
		}
		Location newLocation = new Location(longitude, latitude, radius, timeout);
		setLocation(newLocation);
		return getLocation();
	}
	
	public static boolean exists(PersistenceManager pm, Long firstName, Long middleName, Long lastName) {
		Query query = pm.newQuery(Account.class);
		query.setFilter(
				"firstName == "+firstName.toString()+" && "+
				"middleName == "+middleName.toString()+" && "+
				"lastName == "+lastName.toString());
		try {
			@SuppressWarnings("unchecked")
			List<Account> accountList = (List<Account>) pm.newQuery(query).execute();
			if(accountList == null || accountList.isEmpty() || accountList.size() ==0){
				return false;
			}
			else {
				return true;
			}
		} catch (JDOObjectNotFoundException e) {
			return false;
		} catch (JDOUserException e2) { // just in case we have a new db and their are no Account entities
			return false;
		}
	}

	private static boolean isAccountIDUnique(PersistenceManager pm, String id){
		Account currentAccount = null;

		try {
	        Key key = KeyFactory.createKey(Account.class.getSimpleName(), id);
			currentAccount = pm.getObjectById(Account.class, key);
			if(currentAccount == null){
				return true;
			}
			else {
				return false;
			}
		} catch (JDOObjectNotFoundException e) {
			return true;
		}
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getAuthenticationKey() {
		return authenticationKey;
	}

	public void setAuthenticationKey(String authenticationKey) {
		this.authenticationKey = authenticationKey;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public Long getFirstName() {
		return firstName;
	}

	public void setFirstName(Long firstName) {
		this.firstName = firstName;
	}

	public Long getMiddleName() {
		return middleName;
	}

	public void setMiddleName(Long middleName) {
		this.middleName = middleName;
	}

	public Long getLastName() {
		return lastName;
	}

	public void setLastName(Long lastName) {
		this.lastName = lastName;
	}

	public Long getCoins() {
		return coins;
	}

	public void setCoins(Long coins) {
		this.coins = coins;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
