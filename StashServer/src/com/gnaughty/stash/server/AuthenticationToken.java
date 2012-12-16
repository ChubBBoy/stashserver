package com.gnaughty.stash.server;

public class AuthenticationToken {
	private String accountID = null;
	private String authKey = null;
	
	public AuthenticationToken(String accountID, String authKey){
		setAccountID(accountID);
		setAuthKey(authKey);
	}
	
	public String getAccountID() {
		return accountID;
	}
	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}
	public String getAuthKey() {
		return authKey;
	}
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

}
