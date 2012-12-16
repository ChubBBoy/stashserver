package com.gnaughty.stash.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.gnaughty.stash.server.persistence.PMF;

@SuppressWarnings("serial")
abstract public class StashServerServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(StashServerServlet.class.getName());
	public final static String JSON_PARAMETERS = "jsonParameters";

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JSONObject jsonRequest = null;
		JSONObject jsonResponse = null;

		resp.setContentType("application/json");
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.setDetachAllOnCommit(true);

		try {
			jsonRequest = retrieveJSONObject(req);
			jsonResponse = processRequest(pm, req.getPathInfo(), jsonRequest);
		} catch (Exception e) {
	        jsonResponse = new JSONObject();
			try {
				jsonResponse.accumulate(Error.ERROR,Error.UNKNOWN_SERVER_ERROR);
				jsonResponse.accumulate(Error.ERROR_TEXT, "Due to an error on the server, we are unable to register your account at this time");
			} catch (JSONException e1) {
				log.severe("Several unhandled exception caught ["+e.getMessage()+"]");
				e.printStackTrace();
			}
			log.severe("Unhandled exception caught ["+e.getMessage()+"]");
			e.printStackTrace();
			pm.currentTransaction().rollback();
		} finally {
		    if (pm.currentTransaction().isActive()){
		    	pm.currentTransaction().rollback();
		    }
		    pm.close();
		}
		resp.getWriter().println(jsonResponse);
	}
	
	private JSONObject retrieveJSONObject(HttpServletRequest request) throws IOException, JSONException{
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = request.getReader().readLine()) != null) {
            sb.append(s);
        }
		
        JSONObject jsonObject = new JSONObject(sb.toString());
        return jsonObject;
	}

	abstract protected JSONObject processRequest(PersistenceManager pm, String reqPathInfo, JSONObject jsonObject) throws JSONException;
}
