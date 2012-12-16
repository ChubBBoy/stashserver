package com.gnaughty.stash.server;

public interface Error {

	public final static String ERROR = "error";
	public final static String ERROR_TEXT = "error_text";
	// JSON errors are from 100 - 199
	public final static int JSON_ERROR = 100;
	public final static int JSON_MALFORMED = 101;
	public final static int JSON_MANDATORY_ELEMENT_MISSING = 102;
	// Account errors are from 200 - 299
	public final static int UNAUTHENTICATED = 200;
	public final static int DUPLICATE_NAME = 201;
	// Location errors are from 300 - 399
	public final static int INVALID_LOCATION = 300;
	// Server errors are from 400 - 499
	public final static int UNKNOWN_SERVER_ERROR = 400;
}
