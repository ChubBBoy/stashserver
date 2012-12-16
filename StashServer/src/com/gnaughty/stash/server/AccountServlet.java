package com.gnaughty.stash.server;

import javax.jdo.PersistenceManager;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.json.JSONException;
import org.json.JSONObject;

import com.gnaughty.stash.server.exception.DuplicateException;
import com.gnaughty.stash.server.exception.InvalidEmailException;
import com.gnaughty.stash.server.exception.InvalidLocationDataException;
import com.gnaughty.stash.server.exception.ServerException;
import com.gnaughty.stash.server.exception.UnauthenticatedException;
import com.gnaughty.stash.server.mail.MailSender;

@SuppressWarnings("serial")
public class AccountServlet extends StashServerServlet {

	@Override
	protected JSONObject processRequest(PersistenceManager pm, String reqPathInfo, JSONObject jsonRequest) throws JSONException {
		JSONObject jsonResult = new JSONObject();
		
		if("/register".compareToIgnoreCase(reqPathInfo) == 0){
			String currentElement = null;
			String currentElementType = null;
			String email = null;
			try {
				pm.currentTransaction().begin();
				
				// Validate jsonRequest
				currentElement = Account.AUTHENTICATION_KEY;
				currentElementType = "String";
				String authKey = jsonRequest.getString(Account.AUTHENTICATION_KEY);

				currentElement = Account.FIRST_NAME;
				currentElementType = "Long";
				Long firstName = jsonRequest.getLong(Account.FIRST_NAME);

				currentElement = Account.MIDDLE_NAME;
				currentElementType = "Long";
				Long middleName = jsonRequest.getLong(Account.MIDDLE_NAME);

				currentElement = Account.LAST_NAME;
				currentElementType = "Long";
				Long lastName = jsonRequest.getLong(Account.LAST_NAME);

				currentElement = Account.EMAIL;
				currentElementType = "String";
				email = jsonRequest.getString(Account.EMAIL);
				if(!isValidEmailAddress(email)){
					throw new InvalidEmailException();
				}
				
				Account currentAccount= Account.register(pm,
														authKey,
														firstName,
														middleName,
														lastName,
														email);
				jsonResult.accumulate(Account.ID, currentAccount.getKey().getName());
				jsonResult.accumulate(Account.COINS, currentAccount.getCoins());

				MailSender.sendValidationEmail(currentAccount);
				
				pm.currentTransaction().commit();

			} catch (DuplicateException dupEx) {
				jsonResult.accumulate(Error.ERROR,Error.DUPLICATE_NAME);
				jsonResult.accumulate(Error.ERROR_TEXT, "That combination of names already exists, please choose again");
				pm.currentTransaction().rollback();
			} catch (ServerException serverEx) {
				jsonResult.accumulate(Error.ERROR,Error.UNKNOWN_SERVER_ERROR);
				jsonResult.accumulate(Error.ERROR_TEXT, "Due to an error on the server, we are unable to register your account at this time");
				pm.currentTransaction().rollback();
			} catch (InvalidEmailException invalidEmailEx) {
				if(email == null || email.isEmpty()){
					jsonResult.accumulate(Error.ERROR,Error.JSON_MANDATORY_ELEMENT_MISSING);
					jsonResult.accumulate(Error.ERROR_TEXT, "Email address was not supplied");
				}
				else{
					jsonResult.accumulate(Error.ERROR,Error.JSON_MALFORMED);
					jsonResult.accumulate(Error.ERROR_TEXT, "Email address was not valid");
				}				
				pm.currentTransaction().rollback();
			} catch (JSONException e) {
				jsonResult.accumulate(Error.ERROR,Error.JSON_ERROR);
				jsonResult.accumulate(Error.ERROR_TEXT, currentElement+" should have been of type "+currentElementType);
				pm.currentTransaction().rollback();
			}
		}
		else if("/updateLocation".compareToIgnoreCase(reqPathInfo) == 0){
			String currentElement = null;
			String currentElementType = null;
			Account currentAccount = null;
			try {
				// Validate jsonRequest
				currentElement = Account.ID;
				currentElementType = "String";
				String accountID = jsonRequest.getString(Account.ID);

				currentElement = Account.AUTHENTICATION_KEY;
				currentElementType = "String";
				String authKey = jsonRequest.getString(Account.AUTHENTICATION_KEY);

				currentElement = Location.LONGITUDE;
				currentElementType = "double";
				double longitude = jsonRequest.getDouble(Location.LONGITUDE);

				currentElement = Location.LATITUDE;
				currentElementType = "double";
				double latitude = jsonRequest.getDouble(Location.LATITUDE);

				currentElement = Location.RADIUS;
				currentElementType = "int";
				int radius = jsonRequest.getInt(Location.RADIUS);

				currentElement = Location.TIMEOUT;
				currentElementType = "long";
				long timeout = jsonRequest.getLong(Location.TIMEOUT);

				currentAccount = Account.retrieveAccount(pm, accountID, authKey);
				
				pm.currentTransaction().begin();
				Location newLocation = currentAccount.updateLocation(pm, longitude, latitude, radius, timeout);
				pm.currentTransaction().commit();
				
				jsonResult.accumulate(Location.COINS, newLocation.getCoins());


			} catch (UnauthenticatedException unauthEx) {
				jsonResult.accumulate(Error.ERROR,Error.UNAUTHENTICATED);
				jsonResult.accumulate(Error.ERROR_TEXT, "The authentication key does not match that stored on the account");
				pm.currentTransaction().rollback();
			} catch (InvalidLocationDataException InvalidLocationEx) {
				jsonResult.accumulate(Error.ERROR,Error.INVALID_LOCATION);
				jsonResult.accumulate(Error.ERROR_TEXT, "The supplied location was not valid");
				pm.currentTransaction().rollback();
			} catch (JSONException e) {
				jsonResult.accumulate(Error.ERROR,Error.JSON_ERROR);
				jsonResult.accumulate(Error.ERROR_TEXT, currentElement+" should have been of type "+currentElementType);
				pm.currentTransaction().rollback();
			} catch (ServerException e) {
				jsonResult.accumulate(Error.ERROR,Error.UNKNOWN_SERVER_ERROR);
				jsonResult.accumulate(Error.ERROR_TEXT, "An error occurred on the server");
				pm.currentTransaction().rollback();
			}
		}
		// retrieve should only ever be called internally by the server
		else if("/retrieve".compareToIgnoreCase(reqPathInfo) == 0){
			try {
				pm.currentTransaction().begin();

				Account currentAccount = Account.retrieveAccount(pm, jsonRequest.getString(Account.ID), jsonRequest.getString(Account.AUTHENTICATION_KEY));
				jsonResult = new JSONObject(currentAccount);

				pm.currentTransaction().commit();
				
			} catch (Exception e) {
				jsonResult.accumulate(Error.ERROR,Error.UNKNOWN_SERVER_ERROR);
				jsonResult.accumulate(Error.ERROR_TEXT, e.getMessage());
				pm.currentTransaction().rollback();
			}
		}
		else{
			jsonResult.accumulate(Error.ERROR,Error.UNKNOWN_SERVER_ERROR);
			jsonResult.accumulate(Error.ERROR_TEXT, "["+reqPathInfo+"] has not been implemented");
			pm.currentTransaction().rollback();
		}

		return jsonResult;
	}
	
	public static boolean isValidEmailAddress(String email) {
		   boolean result = true;
		   try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException ex) {
		      result = false;
		   }
		   return result;
		}
}
