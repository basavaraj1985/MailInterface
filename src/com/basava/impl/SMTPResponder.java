package com.basava.impl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import com.basava.interfaces.IJob;
import com.basava.interfaces.Respondable;

/**
 * @author basavar
 *
 */
public class SMTPResponder extends Respondable 
{
	private String userName, password;
	
	public SMTPResponder(String un, String pwd) 
	{
		userName = un;
		password = pwd;
	}

	@Override
	public void sendReply() 
	{
		IJob job = getJobToProcess();
	    String to = job.getRequestor();
	    String from = "AutomationAssistant@yahoo-inc.com";
	    String subject = "Re: " + job.getCommand() ;
	    String text = job.getResponse().toString() ;
	    
	    Properties props = new Properties();
	    props.put("mail.smtp.user", userName);
	    if ( userName.endsWith("gmail.com"))
	    {
	    	props.put("mail.smtp.host", "smtp.gmail.com");
	    }
	    else if ( userName.contains("yahoo"))
	    {
	    	props.put("mail.smtp.host", "smtp.mail.yahoo.com");
	    	from = userName;
	    }
	    props.put("mail.smtp.socketFactory.port", "465");
	    props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.port", "465");
	    Session mailSession = Session.getDefaultInstance(props);
	    Message simpleMessage = new MimeMessage(mailSession);

	    InternetAddress fromAddress = null;
	    InternetAddress toAddress = null;

	    try
	    {
	        fromAddress = new InternetAddress(from,"Automation Assistant");
	        toAddress = new InternetAddress(to);
	    } 
	    catch(Exception e) {
	        e.printStackTrace();
	    }

	    try
	    {
	        simpleMessage.setFrom(fromAddress);
	        simpleMessage.setRecipient(RecipientType.TO,toAddress);
	        simpleMessage.setSubject(subject);
	        simpleMessage.setText(text);

	        Transport transport = mailSession.getTransport("smtps");
	        if ( userName.endsWith("gmail.com"))
	        {
	        	transport.connect("smtp.gmail.com",465, userName, password);
	        }
	        else if( userName.contains("yahoo"))
	        {
	        	transport.connect("smtp.mail.yahoo.com",465, userName, password);
	        }
	        transport.sendMessage(simpleMessage, simpleMessage.getAllRecipients());
	        transport.close();  

	    }
	    catch(MessagingException e){
	        e.printStackTrace();
	    }
	}
	
	public static void main(String[] args) 
	{
		send("mifivetester@gmail.com");
	}
	
	public static  void send(String key){
	    String to = key;
	    String from = "CIAssistant@yahoo-inc.com";
	    String subject = "wassp";
	    String text = "Hello";
	    Properties props = new Properties();

	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.user", "mifivetester@gmail.com");
	    props.put("mail.smtp.socketFactory.port", "465");
	    props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.port", "465");
	    Session mailSession = Session.getDefaultInstance(props);
	    Message simpleMessage = new MimeMessage(mailSession);

	    InternetAddress fromAddress = null;
	    InternetAddress toAddress = null;

	    try
	    {
	        fromAddress = new InternetAddress(from,"Automation Assistant");
	        toAddress = new InternetAddress(to);
	    } 
	    catch(Exception e) {
	        e.printStackTrace();
	    }

	    try
	    {
	        simpleMessage.setFrom(fromAddress);
	        simpleMessage.setRecipient(RecipientType.TO,toAddress);
	        simpleMessage.setSubject(subject);
	        simpleMessage.setText(text);

	        Transport transport = mailSession.getTransport("smtps");
	        transport.connect("smtp.gmail.com",465, "mifivetester@gmail.com", "mi5tester");
	        transport.sendMessage(simpleMessage, simpleMessage.getAllRecipients());
	        transport.close();  

	    }
	    catch(MessagingException e){
	        e.printStackTrace();
	    }
	}

}
