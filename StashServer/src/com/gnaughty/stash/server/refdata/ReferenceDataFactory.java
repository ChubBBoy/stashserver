package com.gnaughty.stash.server.refdata;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

public class ReferenceDataFactory {

	public static boolean firstNamesInitialised = false;
	public static boolean middleNamesInitialised = false;
	public static boolean lastNamesInitialised = false;
	
	public synchronized final static void initFirstNames(PersistenceManager pm){
		while(firstNamesInitialised == false) {
			FirstName currentFirstName = null;
			for(long l=1; l<FirstNameEnum.values().length+1; l++){
				try {
					currentFirstName = pm.getObjectById(FirstName.class, new Long(l));
				} catch (JDOObjectNotFoundException e) {
					currentFirstName = new FirstName();
					currentFirstName.setId(new Long(l));
					currentFirstName.setText(FirstNameEnum.values()[((int)l-1)].toString());
					pm.makePersistent(currentFirstName);
				}
			}
			firstNamesInitialised=true;
		}
	}

	public synchronized final static void initMiddleNames(PersistenceManager pm){
		while(middleNamesInitialised == false) {
			MiddleName currentMiddleName = null;
			for(long l=1; l<MiddleNameEnum.values().length+1; l++){
				try {
					currentMiddleName = pm.getObjectById(MiddleName.class, new Long(l));
				} catch (JDOObjectNotFoundException e) {
					currentMiddleName = new MiddleName();
					currentMiddleName.setId(new Long(l));
					currentMiddleName.setText(MiddleNameEnum.values()[((int)l-1)].toString());
					pm.makePersistent(currentMiddleName);
				}
			}
			middleNamesInitialised=true;
		}
	}
	
	public synchronized final static void initLastNames(PersistenceManager pm){
		while(lastNamesInitialised == false) {
			LastName currentLastName = null;
			for(long l=1; l<LastNameEnum.values().length+1; l++){
				try {
					currentLastName = pm.getObjectById(LastName.class, new Long(l));
				} catch (JDOObjectNotFoundException e) {
					currentLastName = new LastName();
					currentLastName.setId(new Long(l));
					currentLastName.setText(LastNameEnum.values()[((int)l-1)].toString());
					pm.makePersistent(currentLastName);
				}
			}
			lastNamesInitialised=true;
		}
	}
}
