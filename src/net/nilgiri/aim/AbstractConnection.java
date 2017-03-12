package net.nilgiri.aim;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Iterator;
import java.util.Set;

abstract class AbstractConnection extends Thread implements QueueInterface
{
	public final static String REVISION = "$Revision: 1.24 $";
	private final static boolean DEBUG = false;

	final int instance;
	final String name;
	final String host;
	final int port;
	final InetSocketAddress addr;
	final SocketChannel channel;
	final Selector selector;
	final SelectionKey clientKey;
	final boolean mud;
	private Boolean _connected = null;
	private boolean _disconnect = false;
	private boolean _localhost = false;
	final ListenerInterface listener;

	AbstractConnection(String name,
			String host, int port, ListenerInterface listener)
		throws IOException
	{
		super(name+_instance);
		this.instance = _instance++;
		this.name = name;
		this.host = host;
		this.port = port;
		this.listener = listener;

		channel = SocketChannel.open();
		if (host.equals("localhost"))
		{
			_localhost = true;
			addr = new InetSocketAddress(port);
		}
		else
		{
			addr = new InetSocketAddress(host, port);
		}
		selector = Selector.open();
		channel.configureBlocking(false);
		clientKey = channel.register(selector, channel.validOps());
		mud = this instanceof ConnectionMUD; //listener != null
	}

	final boolean connected()
	{
		if (addr.isUnresolved())
		{
			println("Could not resolve "+host+":"+port);
			return false;
		}
		if (_connected != null && _connected == false)
		{
			return false;
		}
		return channel.isConnectionPending() || channel.isConnected();
	}

	void connect() throws IOException
	{
		clear();
		try
		{
			channel.connect(addr);
		}
		catch (UnresolvedAddressException e)
		{
			println(e.getMessage());
			System.exit(1);
		}
	}

	void disconnect()
	{
		_disconnect = true;
	}

	public final void run()
	{
		debug("Running");
		while (true)
		{
			if (action() == false)
			{
				break;
			}
			if (_disconnect)
			{
				debug("Found disconnect");
				break;
			}
		}
		try
		{
			debug("Closing channel");
			channel.close();
		}
		catch (Exception e)
		{
			throw new TOC2MUDException(e);
		}
		debug("Done run");
	}

	private ByteBuffer _writeBuffer = ByteBuffer.allocate(writeBufLen());
	private ByteBuffer _readBuffer = ByteBuffer.allocate(readBufLen());
	private int _countReady0 = 0;
	boolean action()
	{
		try
		{
			int count;
			count = selector.select(POLL_TIMEOUT);
			if (count == 0)
			{
				println("connection not polled");
				return false;
			}
		}
		catch (IOException e)
		{
			throw new TOC2MUDException(e);
		}
		Set<SelectionKey> keys = selector.selectedKeys();
		Iterator<SelectionKey> iS = keys.iterator();
		while (iS.hasNext())
		{
			SelectionKey key = iS.next();
//debug("key size:"+keys.size()+" key:"+key.readyOps()+":"+clientKey.readyOps()+" i:"+key.interestOps()+" A:"+key.OP_ACCEPT+" C:"+key.OP_CONNECT+" R:"+key.OP_READ+" W:"+key.OP_WRITE);
			if (key.readyOps() == 0)
			{
				if (_countReady0 == 10)
				{
					println("zero ready limit reached");
					if (listener != null)
					{
						listener.send(S_disconnect_msg);
					}
					_connected = false;
					return false;
				}
				_countReady0++;
			}
			else
			{
				_countReady0 = 0;
			}

			iS.remove();

			if (key.isValid() == false)
			{
				debug("[invalid connection]");
				break;
			}
			if (key.isConnectable())
			{
				debug("[found connection]");
				if (channel.isConnectionPending())
				{
					try
					{
						debug("[connecting]");
						channel.finishConnect();
						_connected = true;
					}
					catch (IOException e)
					{
						throw new TOC2MUDException(e);
					}
				}
			}
			if (key.isReadable())
			{
				debug("[found readable] ");
				while (true)
				{
					int len = -1;
					try
					{
						len = channel.read(_readBuffer);
						if (len == -1) //not exception
						{
							debug("Read connection error len=-1");
							if (listener != null)
							{
								listener.send(S_disconnect_msg);
							}
							_connected = false;
							return false;
						}
						debug("[read "+len+" bytes]");
					}
					catch (IOException e)
					{
						debug("IOException read");
						len = 0;
					}
					if (len == 0)
					{
						break;
					}
					_readBuffer.flip();
					if (_readBuffer.hasRemaining())
					{
						byte[] array = new byte[_readBuffer.remaining()];
						//debug("[reading "+array.length+"]");
						_readBuffer.get(array);
						_readBuffer.flip();
						push(array);
					}
				}
				_readBuffer.clear();
			}
			if (canPoll() && key.isWritable())
			{
				debug("[found writable] ");
				while (canPoll())
				{
					byte[] array = poll();
					_writeBuffer = ByteBuffer.allocate(array.length);
					_writeBuffer.put(array);
					_writeBuffer.flip();
					//TOC2MUDException.dump("[Writing]", array); //XXX
					while (_writeBuffer.hasRemaining())
					{
						int len = -1;
						try
						{
							len = channel.write(_writeBuffer);
							if (len == -1) //not exception, something really wrong?
							{
								println("Write connection error len=-1");
								if (listener != null)
								{
									listener.send(S_disconnect_msg);
								}
								_connected = false;
								return false;
							}
							debug("[writing "+len+"]");
						}
						catch (IOException e)
						{
							println("IOException write");
							len = 0;
						}
						if (len == 0)
						{
							break;
						}
						//check to see if this probably doesn't always hold true
						//assert len == _writeBuffer.remaining(); //TODO
					}
					_writeBuffer.flip();
				}
				_writeBuffer.clear();
			}
		}
		try
		{
			Thread.sleep(SLEEP_TIME);
		}
		catch (InterruptedException e)
		{
			throw new TOC2MUDException(e);
		}
		return true;
	}

	public final void println(String mesg)
	{
		StringBuilder buf = new StringBuilder();
		if (mud)
		{
			buf.append("MUD-");
		}
		else
		{
			buf.append("TOC2-");
		}
		buf.append(name).append(instance).append(' ').append(mesg);
		System.err.println(buf);
	}

	public final void debug(String mesg)
	{
		if (DEBUG)
		{
			println(mesg);
		}
	}

	private static int _instance = 0;

	public final static int POLL_TIMEOUT = 500;
	public final static int SLEEP_TIME = 500;
	public final static String S_disconnect_msg = "### you have been disconnected";
}
