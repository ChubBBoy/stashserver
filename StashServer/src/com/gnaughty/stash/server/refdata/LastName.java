package com.gnaughty.stash.server.refdata;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gnaughty.stash.server.persistence.PMF;

@PersistenceCapable
public class LastName{
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
	public static List<LastName> list() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		if(!ReferenceDataFactory.lastNamesInitialised){
			ReferenceDataFactory.initLastNames(pm);
		}
		Query query = pm.newQuery(LastName.class);
		query.setOrdering("id asc");
		List<LastName> lastNames = (List<LastName>) pm.newQuery(query).execute();
		pm.close();
		return lastNames;
	  }
}