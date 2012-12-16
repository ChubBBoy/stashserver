package com.gnaughty.stash.server.refdata;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.gnaughty.stash.server.persistence.PMF;

import java.util.List;

@PersistenceCapable
public class FirstName{
	@PrimaryKey
	@Persistent
	private Long id;
	@Persistent
	private String text;

	public Long getId() {
		return id;
	}

	public void setId(Long long1) {
		this.id = long1;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@SuppressWarnings("unchecked")
	public static List<FirstName> list() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		if(!ReferenceDataFactory.firstNamesInitialised){
			ReferenceDataFactory.initFirstNames(pm);
		}
		Query query = pm.newQuery(FirstName.class);
		query.setOrdering("id asc");
		List<FirstName> firstNames = (List<FirstName>) pm.newQuery(query).execute();
		pm.close();
		return firstNames;
	  }
}