package com.basava.mail.imapimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import com.basava.impl.AutomationJob;
import com.basava.interfaces.IJob;
import com.basava.interfaces.Pollable;

/**
 * @author basavar
 *
 */
public class ImapMailReader extends Pollable
{
	private String userMailID;
	private String password;
	private String lineSepartor = System.getProperty("line.separator");
	private Folder inbox; 
	
	public ImapMailReader(String un, String pwd) 
	{
		userMailID = un;
		password = pwd;
	}

	public void loginAndOpenFolder(String userMailID, String password, String folder )
	{
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
			try {
				Session session = Session.getDefaultInstance(props, null);
				Store store = session.getStore("imaps");
				if ( userMailID.endsWith("gmail.com"))
				{
					store.connect("imap.gmail.com", userMailID, password);
				}
				else 
				{
					store.connect("imap.mail.yahoo.com", userMailID, password);
				}
				System.out.println(store);

				inbox = store.getFolder(folder);
//				inbox.open(Folder.READ_WRITE);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("Failed to connect and open mail folder " + folder);
			}
	}
	
	public static void main(String[] args) 
	{
		ImapMailReader mr = new ImapMailReader("mifivetester@gmail.com", "mi5tester");
		mr.loginAndOpenFolder("mifivetester@gmail.com", "mi5tester", "Inbox");
		mr.pollMessages();
	}

	public void pollMessages() 
	{
//		while ( true )
		{
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			Message messages[] = null;
			try {
				inbox.open(Folder.READ_WRITE);
				messages = inbox.search(ft);
			} catch (MessagingException e) {
				System.err.println("Messaging exception caught : " + e.getMessage() );
				e.printStackTrace();
				loginAndOpenFolder(userMailID, password, "Inbox" );
				try {
					messages = inbox.search(ft);
				} catch (MessagingException e1) {
					e1.printStackTrace();
					System.err.println("Messaging exception while first retry : " + e.getMessage() );
					try {
						Thread.sleep(2000);
						loginAndOpenFolder(userMailID, password, "Inbox" );
						messages = inbox.search(ft);
					} catch (Exception e2) {
						e2.printStackTrace();
						System.err.println("Messaging exception while second retry : " + e.getMessage() );
					}
				}
			}
			
			IJob job = new AutomationJob();
			try {
					for( Message message:messages ) 
					{
						Address[] from = message.getFrom();
						System.out.println("From: ");
						for ( Address a : from )
						{
							job.setRequestor(a.toString());
						}
						System.out.println( "Subject: " + message.getSubject() );
						job.setCommand(message.getSubject());
						
//						Object msgContent = message.getContent();
//						Multipart mp = null;
						MimeMessage mmsg = new MimeMessage((MimeMessage) message );
						Object content2 = mmsg.getContent();
						if ( content2 instanceof MimeMultipart )
						{
							MimeMultipart mmsgMpart = (MimeMultipart) content2;
							int count = mmsgMpart.getCount();
							for ( int k = 0 ; k < count ; k++ )
							{
								BodyPart bodyPart = mmsgMpart.getBodyPart(k);
								String contentType = bodyPart.getContentType();
								System.out.println(contentType);
								if ( contentType.contains("text/html") )
								{
									System.out.println("Skipping html mail part printing !");
									continue;
								}
								String content = (String) bodyPart.getContent();
								System.out.println(content);
								String[] bodyLines = content.split(lineSepartor);
								List<String> commandParaList = new ArrayList<String>();
								for ( String s : bodyLines )
								{
									commandParaList.add(s);
								}
								job.setCommandRequestParameters(commandParaList);
							}
						}
						Pollable.add(job);
					} // end of for
					
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void _main(String args[]) throws IOException 
	{
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
			try {
				Session session = Session.getDefaultInstance(props, null);
				Store store = session.getStore("imaps");
				store.connect("imap.gmail.com", "mifivetester@gmail.com", "mi5tester");
				System.out.println(store);

				Folder inbox = store.getFolder("Inbox");
				inbox.open(Folder.READ_WRITE);
				FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
				Message messages[] = inbox.search(ft);
				
				int i=0;
				for( Message message:messages ) 
				{
					Address[] from = messages[i].getFrom();
					System.out.println("From: ");
					for ( Address a : from )
					{
						System.out.println(a.toString());
					}
					System.out.println( "Subject: " + message.getSubject() + i );
//					Object msgContent = message.getContent();
//					Multipart mp = null;
					MimeMessage mmsg = new MimeMessage((MimeMessage) message );
					Object content2 = mmsg.getContent();
					if ( content2 instanceof MimeMultipart )
					{
						MimeMultipart mmsgMpart = (MimeMultipart) content2;
						int count = mmsgMpart.getCount();
						for ( int k = 0 ; k < count ; k++ )
						{
							BodyPart bodyPart = mmsgMpart.getBodyPart(k);
							String contentType = bodyPart.getContentType();
							System.out.println(contentType);
							if ( contentType.contains("text/html") )
							{
								System.out.println("Skipping html mail part printing !");
								continue;
							}
							String content = (String) bodyPart.getContent();
							System.out.println(content);
						}
					}
//					if ( msgContent instanceof Multipart )
//					{
//						mp = (Multipart)message.getContent();
//					}
//					else
//					{
//						System.out.println(message.getContent().toString());
//					}
//					if ( null != mp )
//					{
//						String contentType = mp.getContentType();
//						if ( contentType.contains("multipart") )
//						{
//							int count = mp.getCount();
//							for ( int j = 0 ; j< count ; j++ )
//							{
//								Object content = mp.getBodyPart(j).getContent();
//								System.out.println(content.getClass());
//								if ( content instanceof String )
//								{
//									System.out.println(content.toString());
//								}
//								else
//								{
//									System.out.println(content.toString());
//								}
//							}
//						}
//						i++;
//						FlagTerm ftt = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
						message.setFlag(Flags.Flag.SEEN, false);
//						}
					}
				} catch (NoSuchProviderException e) 
				{
					e.printStackTrace();
					System.exit(1);
				} catch (MessagingException e) {
					e.printStackTrace();
			System.exit(2);
		}

	}

	@Override
	public void preCondition() 
	{
		loginAndOpenFolder(userMailID, password, "Inbox");
	}

	@Override
	public void execute() 
	{
		FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
		Message messages[] = null;
		try {
				if ( ! inbox.isOpen() )
				{
					inbox.open(Folder.READ_WRITE);
				}
				messages = inbox.search(ft);
			} 
			catch (MessagingException e) 
			{
				System.err.println("Messaging exception caught : " + e.getMessage() );
				e.printStackTrace();
				loginAndOpenFolder(userMailID, password, "Inbox" );
				try 
				{
					if ( ! inbox.isOpen() )
					{
						inbox.open(Folder.READ_WRITE);
					}
					messages = inbox.search(ft);
				} catch (MessagingException e1) {
					e1.printStackTrace();
					System.err.println("Messaging exception while first retry : " + e.getMessage() );
					try {
						Thread.sleep(2000);
						loginAndOpenFolder(userMailID, password, "Inbox" );
						if ( ! inbox.isOpen() )
						{
							inbox.open(Folder.READ_WRITE);
						}
						messages = inbox.search(ft);
					} catch (Exception e2) {
						e2.printStackTrace();
						System.err.println("Messaging exception while second retry : " + e.getMessage() );
					}
				}
		}
		
		IJob job = new AutomationJob();
		try {
				for( Message message:messages ) 
				{
					Address[] from = message.getFrom();
					System.out.println("From: ");
					for ( Address a : from )
					{
						job.setRequestor(a.toString());
						System.out.println(a.toString());
					}
					System.out.println( "Subject: " + message.getSubject() );
					job.setCommand(message.getSubject());
					
//						Object msgContent = message.getContent();
//						Multipart mp = null;
					MimeMessage mmsg = new MimeMessage((MimeMessage) message );
					Object content2 = mmsg.getContent();
					String mimeMsgType = mmsg.getContentType();
					System.out.println("Message type : " + mimeMsgType);
					if ( content2 instanceof MimeMultipart )
					{
						MimeMultipart mmsgMpart = (MimeMultipart) content2;
						int count = mmsgMpart.getCount();
						for ( int k = 0 ; k < count ; k++ )
						{
							BodyPart bodyPart = mmsgMpart.getBodyPart(k);
							String contentType = bodyPart.getContentType();
							System.out.println(contentType);
							if ( contentType.contains("text/html") )
							{
								System.out.println("Skipping html mail part printing !");
								continue;
							}
							String content = (String) bodyPart.getContent();
							System.out.println(content);
							String[] bodyLines = content.split(lineSepartor);
							List<String> commandParaList = new ArrayList<String>();
							for ( String s : bodyLines )
							{
								commandParaList.add(s);
							}
							job.setCommandRequestParameters(commandParaList);
						}
					}
					else if( content2 instanceof String )
					{
						String bodyAsString = (String) content2;
						String[] bodyLines = bodyAsString.split(lineSepartor);
						List<String> commandParaList = new ArrayList<String>();
						for ( String s : bodyLines )
						{
							commandParaList.add(s);
						}
						job.setCommandRequestParameters(commandParaList);
					}
					Pollable.add(job);
				} // end of for
				
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if ( inbox.isOpen() )
				{
					inbox.close(true);
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

}