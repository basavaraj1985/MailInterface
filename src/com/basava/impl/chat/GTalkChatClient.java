package com.basava.impl.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import com.basava.impl.AutomationJob;
import com.basava.interfaces.IJob;
import com.basava.interfaces.IJob.Type;
import com.basava.interfaces.Pollable;
import com.basava.lib.IOLib;


public class GTalkChatClient extends Pollable implements MessageListener
{
	String userName, password;
	XMPPConnection connection;
	boolean authorized;
	List<String> listenersPresent ;
	
	public GTalkChatClient( String un, String pwd)
	{
		userName = un;
		password = pwd;
		
		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		connection = new XMPPConnection(config);
		listenersPresent = new ArrayList<String>();
		
		try 
		{
			login(un, pwd);
		} catch (XMPPException e) 
		{
			e.printStackTrace();
		}
		addChatListener(this);
	}
	
	public void login(String userName, String password) throws XMPPException
	{
		connection.connect();
		connection.login(userName, password);
	}

	public void addChatListener(final GTalkChatClient clientObj) 
	{
		ChatManager chatManager = connection.getChatManager();
		chatManager.addChatListener(new ChatManagerListener() 
		{
			@Override
			public void chatCreated(Chat chat, boolean createdLocally) 
			{
				if (!createdLocally && chat.getListeners().isEmpty() && !listenersPresent.contains(chat.getParticipant()) )
				{
					chat.addMessageListener(new GTalkChatClient(userName, password));;
				}
				else
				{
					if ( !listenersPresent.contains(chat.getParticipant()) )
					{
						chat.addMessageListener(clientObj);
					}
					
					System.out.println("New Chat created from : " + chat.getParticipant());
					authorized = aaaRequestor(chat.getParticipant());
				}
			}
		});
	}

	public static void loginAndSendMessage(String un, String pwd, String message, String to)
	{
		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		XMPPConnection connxn = new XMPPConnection(config);
		
		try 
		{
			connxn.connect();
			connxn.login(un, pwd);
		} catch (XMPPException e) 
		{
			e.printStackTrace();
		}
	
		Chat chat = connxn.getChatManager().createChat(to, null);
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message, String to) throws XMPPException
	{
		Chat chat = connection.getChatManager().createChat(to, null);
		chat.sendMessage(message);
	}
	
	public void displayBuddyList()
	{
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		
		System.out.println("\n\n" + entries.size() + " buddy(ies):");
		for(RosterEntry r:entries)
		{
			System.out.println(r.getUser());
		}
	}

	public void disconnect()
	{
		connection.disconnect();
	}
	
	public void processMessage(Chat chat, Message message) 
	{
		String messageBody = message.getBody();
		authorized = aaaRequestor(chat.getParticipant());
		if( message.getType() == Message.Type.chat && messageBody != null && authorized )
		{
			System.out.println(chat.getParticipant() + " says: " + messageBody );
			IJob jobReq = new AutomationJob();
			jobReq.setType(Type.GTalkChatClient);
			String[] parameters = messageBody.split(",,");
			jobReq.setCommand(parameters[0]);
			List<String> paraList = new ArrayList<String>();
			if ( parameters.length > 1 )
			{
				for ( int j = 1; j < parameters.length ; j++ )
				{
					paraList.add(parameters[j]);
				}
			}
			jobReq.setCommandRequestParameters(paraList);
			jobReq.setRequestor(chat.getParticipant());
			add(jobReq);
			try {
				sendMessage("You said :" + messageBody, chat.getParticipant());
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
    }
	
	public static void main(String args[]) throws XMPPException, IOException
	{
		// declare variables
		GTalkChatClient c = new GTalkChatClient("mifivetester@gmail.com", "mi5tester");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String msg;
		String to = "to@to.com";
		
		// turn on the enhanced debugger
//		XMPPConnection.DEBUG_ENABLED = true;


		// provide your login information here
//		c.login("mifivetester@gmail.com", "mi5tester");


		c.displayBuddyList();
		System.out.println("-----");
		System.out.println("Enter your message in the console.");
		System.out.println("All messages will be sent to " + to );
		System.out.println("-----\n");

		while( !(msg=br.readLine()).equals("bye"))
		{
			// your buddy's gmail address goes here
			c.sendMessage(msg, to );
		}

//		c.disconnect();
//		System.exit(0);
	}

	@Override
	public void execute() 
	{
		// Create a new presence. Pass in false to indicate we're unavailable.
		Presence presence = new Presence(Presence.Type.unavailable);
		presence.setStatus("Waiting.." + IOLib.now() );
		connection.sendPacket(presence);
	}


	@Override
	public void preCondition() 
	{
		try {
			sendMessage("Started!", "to@to.com");
		} catch (XMPPException e) {
			System.err.println("Could not send message!");
			e.printStackTrace();
		}
	}
}