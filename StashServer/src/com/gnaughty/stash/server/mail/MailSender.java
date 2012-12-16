package com.gnaughty.stash.server.mail;

import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.gnaughty.stash.server.Account;
import com.gnaughty.stash.server.StashServerServlet;

public class MailSender {

	private static final Logger log = Logger.getLogger(StashServerServlet.class.getName());

	public static final String fromName = "Stash! Admin";
	public static final String fromAddress = "stash.gnaughtygnomes@gmail.com";
	public static final String fromPassword = "hobgoblin";

	public static final String toName = "Stash! Player";
	
	public static void sendValidationEmail(Account currentAccount){
		Properties props = new Properties();
//		props.put("mail.smtp.host", "smtp.gmail.com");
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.port", "465");
//		props.put("mail.smtp.socketFactory.port", "465");
//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		props.put("mail.smtp.socketFactory.fallback", "false");
//		props.put("mail.debug", "false");
		
//		Session session = Session.getInstance(props);
        Session session = Session.getDefaultInstance(props, null);
//		Multipart multipart = new MimeMultipart("related");
//		BodyPart htmlPart = new MimeBodyPart();

		
		Message msg = new MimeMessage(session);
		try {
			msg.setSubject("Testing embedded HTML message");
			
			InternetAddress from = new InternetAddress(fromAddress, fromName);
			InternetAddress to = new InternetAddress(currentAccount.getEmail(), toName);
			msg.addRecipient(Message.RecipientType.TO, to);
			msg.setFrom(from);
            msg.setSubject("Please activate your Stash! account");

//			htmlPart.setContent(
            msg.setText(
			"<html>" + 
				"<body>" +
					"<img src=\"./stash_logo.bmp\"/><br/>" +
					"<h2>Stash! Activation</h2>" +
					"<br/>" +
					"<br/>" +
					"Thank you for registering a new Stash! account.<br/>" +
					"<br/>" +
					"To complete your registration and activate your new account, please click on the following link:<br/>" +
					"" +
					"<br/>" +		
					"If you havn't just registered a new Stash! account then please ignore this email and accept our apologies.<br/>" +
					"<br/>" +
					"See you in the game...<br/>" +
					"The Stash! Admin Team<br/>" +
				"</body>" +
			"</html>");
//			multipart.addBodyPart(htmlPart);
			
//	        Transport transport = session.getTransport("smtp");
//	        transport.connect(fromAddress, fromPassword);
//	        transport.sendMessage(msg, msg.getAllRecipients());
//	        transport.close();
			Transport.send(msg);
		} catch (Exception e) {
			log.severe("Unable to send validation email ["+e.getMessage()+"]");
			e.printStackTrace();
		}
	}	
}
