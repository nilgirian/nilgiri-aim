package net.nilgiri.aim;

import java.io.IOException;

final class Buddy implements ListenerInterface
{
	public final static String REVISION = "$Revision: 1.27 $";
	final String screenname;
	final boolean trusted;
	long _t = 0;
	final FlapQueue _imOut;
	ConnectionMUD mud = null;
	
	
	Buddy(String name, FlapQueue imOut)
	{
		this.screenname = name;
		long _t = System.currentTimeMillis();
		_imOut = imOut;
		trusted = MUD.trusted(name);
	}

	/** Recive a message from IM. The actually queuing is done in ConnectionAIM.
		This really means process the received IM.
		*/
	void recvim(CharSequence mesg)
	{
		boolean log = true;
		String message = mesg.toString();
		long t0 = System.currentTimeMillis();
		if (message.equalsIgnoreCase("#help"))
		{
			sendHelp();
		}
		else if (message.equalsIgnoreCase("#info"))
		{
			sendInfo();
		}
		else if (message.equalsIgnoreCase("#load"))
		{
			sendLoad();
		}
		else if (trusted && message.equalsIgnoreCase("#disconnect"))
		{
			sendDisconnect();
		}
		else if (trusted && message.equalsIgnoreCase("#connect"))
		{
			sendConnect();
		}
		else if (trusted && message.equalsIgnoreCase("#halt"))
		{
			MUD.halt();
		}
		else if (trusted && message.equalsIgnoreCase("#unhalt"))
		{
			MUD.unhalt();
		}
		else if (trusted && message.equalsIgnoreCase("#reboot"))
		{
			MUD.reboot();
		}
		else if (trusted && message.equalsIgnoreCase("#shutdown"))
		{
			System.err.println("Shutdown by "+screenname+".");
			System.exit(0);
		}
		else if (message.startsWith("#"))
		{
			sendim("I did not understand that # TOC2MUD command.");
		}
		else if (mud != null && mud.connected())
		{
			log = false;
			sendmud(message);
		}
		else
		{
			if (t0 - _t > TIME_RESEND_INTRO)
			{
				sendIntro();
			}
		}
		if (log)
		{
			StringBuilder buf = new StringBuilder(screenname)
				.append(">> ")
				.append(message);
			System.err.println(buf);
		}
		_t = t0;
	}

	/** Send a message to IM. Queue a message to send out.
		*/
	void sendim(CharSequence message)
	{
		if (message == null)
		{
			return;
		}
		assert _imOut != null;
		_imOut.enqueue(new Flap(TOC2.toc2_send_im(screenname, message.toString())));
	}

	private void sendHelp()
	{
		sendim(
				"#help - this menu \r\n"+
				"#info - MUD information \r\n"+
				"#load - get MUD load information"
				);
		if (trusted)
		{
			sendim(
					"#connect - Connect to MUD \r\n"+
					"#disconnect - Disconnect from Mud \r\n"+
					"#halt - halt the MUD \r\n"+
					"#reboot - reboot the MUD \r\n"+
					"#unhalt - clear the halt of the MUD \r\n"+
					"#shutdown - shutdown the TOC2MUD session "
					);
		}
	}

	private void sendInfo()
	{
		if (trusted)
		{
			if (mud == null || mud.connected() == false)
			{
				sendim("You are not connected.");
			}
			else
			{
				sendim("You are connected.");
			}
		}
		else
		{
			sendim(S_info);
		}
		String mesg = MUD.info();
		if (mesg == null)
		{
			sendim(S_muddown);
		}
		sendim(mesg);
	}

	private void sendLoad()
	{
		String mesg = MUD.load();
		if (mesg == null)
		{
			sendim(S_muddown);
		}
		sendim(mesg);
	}

	private void sendDisconnect()
	{
		assert trusted;
		if (mud == null)
		{
			sendim("You are already disconnected.");
			return;
		}
		mud.disconnect();
		mud = null;
		sendim(AbstractConnection.S_disconnect_msg);
	}

	private void sendConnect()
	{
		assert trusted;
		try
		{
			if (mud == null)
			{
				mud = new ConnectionMUD(this);
			}
			if (mud.connected())
			{
				sendim("You are already connected.");
				return;
			}
			mud.connect();
			mud.start();
			if (mud.connected() == false)
			{
				sendim("Could not connect to MUD.");
			}
		}
		catch (IOException e)
		{
			throw new TOC2MUDException(e);
		}
	}

	private void sendIntro()
	{
		sendim(S_intro);
	}

	/** What to do with messages received from mud. */
	void recvmud(byte[] array)
	{
		StringBuilder buf = new StringBuilder(MUD.READ_BUF_LEN);
		int p = 0;
		//remove white space
		while (p != array.length)
		{
			char c = (char)array[p];
			p++;
			if (c == ' ' || c == '\n' || c == '\r')
			{
				continue;
			}
			buf.append(c);
			break;
		}
		//copy over rest
		while (p != array.length)
		{
			char c = (char)array[p];
			buf.append(c);
			p++;
		}
		String message = buf.toString();
		if (message.startsWith("The Angel of Death has come and gone.")) //XXX
		{
			return;
		}
		if (message.contains("Angel of Death"))
		{
			TOC2MUDException.dump(array);
		}
		sendim(message);
	}

	/** For sending messages to mud. */
	void sendmud(String message)
	{
		assert mud.connected();
		mud.send(message);
	}

	@Override
	/** Assume for a disconnect message */
	public void send(String message)
	{
		sendim(message);
		mud = null;
	}

	final static long TIME_RESEND_INTRO = 60 * 60 * 24 * 1000;
	final static String S_url = MUD.URL;
	final static String S_intro = "I am the Forgotten World AIM bot. Type \"#help\" for more info, or visit us at "+MUD.URL;
	final static String S_info = "Visit us at: "+MUD.URL;
	final static String S_muddown = "The MUD is currently down.";
}
