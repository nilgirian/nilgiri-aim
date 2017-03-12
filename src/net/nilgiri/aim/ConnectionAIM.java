package net.nilgiri.aim;

import java.io.IOException;
import java.util.HashMap;

final class ConnectionAIM extends AbstractConnection
{
	public final static String REVISION = "$Revision: 1.13 $";
	private final static boolean DEBUG = false;

	private final FlapQueue _out = new FlapQueue();
	private final HashMap<String,Buddy> _buddylist = new HashMap<String,Buddy>();

	ConnectionAIM() throws IOException
	{
		super("AIM", TOC2.HOST, TOC2.PORT, null);
	}

	@Override
	public final void connect() throws IOException
	{
		super.connect();
		_out.pushRaw(TOC2.FLAPON.getBytes());
	}

	private byte[] _buffer = new byte[FlapQueue.READ_BUF_LEN];
	private int _offset = 0;
	private int _endpos = 0;
	//when we receive raw data from a channel connected to AIM, we should assume
	//it can arrive broken or in multiples, so push as many whole flaps as
	//possible and then queueu the remaining data for next get to complete
	@Override
	public final void push(byte[] array)
	{
		//move everything into the buffer
		for (int i = 0; i < array.length; i++)
		{
			_buffer[_endpos] = array[i];
			_endpos++;
			if (_endpos == _buffer.length)
			{
				_endpos = 0;
			}
		}
		int i = _offset;
		byte[] buf = new byte[FlapQueue.READ_BUF_LEN];
		int pos = 0;
		int flaplen = -1;
		do
		{
			//move individual flaps from the buffer into buf to be queued
			buf[pos] = _buffer[i];
			pos++;
			i++;
			if (i == _buffer.length)
			{
				i = 0;
			}
			if (pos == TOC2.HEADER_FIELD_LEN)
			{
				flaplen = Flap.readLength(buf);
			}
			else if (flaplen != -1 && pos - TOC2.HEADER_FIELD_LEN == flaplen)
			{
				Flap flap = new Flap(buf);
				//System.err.println(flap.toString());
				switch (flap.type)
				{
					case SIGNON:
						if (DEBUG) System.err.println("[found signon]");
						send(FrameType.SIGNON, TOC2.flap_signon());
						send(TOC2.toc2_login());
						break;
					case DATA:
						switch (flap.frame.type)
						{
							case SIGN_ON:
								if (DEBUG) System.err.println("[signed on]");
								send(TOC2.toc_set_info());
								break;
							//case CONFIG:
							case CONFIG2:
								if (DEBUG) System.err.println("[received config]");
								send(TOC2.toc_init_done());
								break;
							case NICK:
								if (DEBUG) System.err.println("[received nick]");
								break;
							//case IM_IN:
							case IM_IN_ENC2:
								{
									String name = flap.frame.arg[1];
									String message = flap.frame.arg[3];
									StringBuilder mesg = new StringBuilder()
										.append(name)
										.append(':')
										.append(message);
									Buddy buddy = _buddylist.get(name);
									if (buddy == null)
									{
										buddy = new Buddy(name, _out);
										_buddylist.put(name, buddy);
									}
									buddy.recvim(htmldecode(message));
									if (DEBUG) System.err.println(mesg);
								}
								break;
							//case UPDATE_BUDDY:
							case UPDATE_BUDDY2:
								if (DEBUG) System.err.println("Recv'd "+flap.frame.type);
								if (DEBUG) System.err.println(flap.toString());
								break;
							case ERROR:
								System.err.println(DataType.errmsg(flap));
								System.exit(1);
								break;
							case EVILED:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case CHAT_JOIN:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							//case CHAT_IN:
							case CHAT_IN_ENC:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case CHAT_UPDATE_BUDDY:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case CHAT_INVITE:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case CHAT_LEFT:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case GOTO_URL:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case DIR_STATUS:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case ADMIN_NICK_STATUS:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case ADMIN_PASSWD_STATUS:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case PAUSE:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case RVOUS_PROPOSE:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case NEW_BUDDY_REPLY2:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case UPDATE2:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case INSERTED2:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case DELETED2:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case CLIENT_EVENT:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							case BUDDY_CAPS2:
								if (DEBUG) System.err.println("Recv'd "+flap.frame.type);
								if (DEBUG) System.err.println(flap.toString());
								break;
							case BART2:
								if (DEBUG) System.err.println("Recv'd "+flap.frame.type);
								if (DEBUG) System.err.println(flap.toString());
								break;
							case _MESG_:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								break;
							default:
								System.err.println("Recv'd "+flap.frame.type);
								System.err.println(flap.toString());
								throw new TOC2MUDException(flap.frame.type.str);
						}
						break;
					default:
				}
				buf = new byte[FlapQueue.READ_BUF_LEN];
				_offset = i;
				flaplen = -1;
				pos = 0;
			}
		}
		while (i != _endpos);
	}

	@Override
	public final byte[] poll()
	{
		return _out.poll();
	}

	@Override
	public final boolean canPoll()
	{
		return _out.canPoll();
	}

	@Override
	public final int readBufLen()
	{
		return FlapQueue.READ_BUF_LEN;
	}

	@Override
	public final int writeBufLen()
	{
		return FlapQueue.WRITE_BUF_LEN;
	}

	@Override
	public final void clear()
	{
		_out.clear();
	}

	/** For sending String messages, presumably needing 0 byte append. */
	public final void send(String mesg)
	{
		_out.enqueue(new Flap(mesg));
	}

	/** For sending data messages, presumably not DATA, not needing 0 byte. */
	public final void send(FrameType type, byte[] command)
	{
		_out.enqueue(new Flap(type, command));
	}

	public final static String htmldecode(String str)
	{
		StringBuilder buf = new StringBuilder(str.length());
		int i = 0;
		int len = str.length();
		int s = 0;
		while (i < len)
		{
			char c = str.charAt(i);
			if (s == 0) //none event
			{
				if (c == '<')
				{
					s = 1;
				}
				else if (c == '&')
				{
					s = 2;
				}
				else if (c == '>')
				{
				}
				else
				{
					buf.append(c);
				}
			}
			else if (s == 1)
			{
				if (c == '>')
				{
					s = 0;
				}
			}
			else if (s == 2)
			{
				if (c == 'a') //&amp;
				{
					s = 3;
				}
				else if (c == 'g') //&gt;
				{
					s = 6;
				}
				else if (c == 'l') //&lt;
				{
					s = 8;
				}
				else
				{
					s = 99;
				}
			}
			else if (s == 3)
			{
				if (c == 'm') //&amp;
				{
					s = 4;
				}
				else
				{
					s = 99;
				}
			}
			else if (s == 4)
			{
				if (c == 'p') //&amp;
				{
					s = 5;
				}
				else
				{
					s = 99;
				}
			}
			else if (s == 5)
			{
				s = 0;
				if (c == ';') //&amp;
				{
					buf.append('&');
				}
			}
			else if (s == 6)
			{
				if (c == 't') //&gt;
				{
					s = 7;
				}
				else
				{
					s = 99;
				}
			}
			else if (s == 7)
			{
				s = 0;
				if (c == ';') //&gt;
				{
					buf.append('>');
				}
			}
			else if (s == 8)
			{
				if (c == 't') //&lt;
				{
					s = 9;
				}
				else
				{
					s = 99;
				}
			}
			else if (s == 9)
			{
				s = 0;
				if (c == ';') //&lt;
				{
					buf.append('<');
				}
			}
			else if (s == 99)
			{
				//wait til end
			}
			i++;
		}
		return buf.toString();
	}
}
