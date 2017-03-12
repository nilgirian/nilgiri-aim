package net.nilgiri.aim;

import java.io.IOException;

final class ConnectionMUD extends AbstractConnection
{
	public final static String REVISION = "$Revision: 1.8 $";
	//private StringQueue _in = new StringQueue();
	private StringQueue _out = new StringQueue();
	private final Buddy buddy;

	ConnectionMUD(Buddy buddy) throws IOException
	{
		super(buddy.screenname, MUD.HOST, MUD.PORT, buddy);
		this.buddy = buddy;
	}

	public final String name()
	{
		return buddy.screenname;
	}

	@Override
	public final void connect() throws IOException
	{
		super.connect();
		send((MUD.LOGIN+" sin"));
	}

	private byte[] _buffer = new byte[MUD.READ_BUF_LEN];
	private int _offset = 0;
	private int _endpos = 0;
	//for receiving raw data from MUD.
	@Override
	public final void push(byte[] array)
	{
		//Since this is a connection to localhost, we can pass directly
		buddy.recvmud(array);
		/* Since this is localhost, probably don't need to do this below
		for (int i = 0; i < array.length; i++)
		{
			//move everything into the buffer
			_buffer[_endpos] = array[i];
			_endpos++;
			if (_endpos == _buffer.length)
			{
				_endpos = 0;
			}
		}
		int i = _offset;
		StringBuilder buf = new StringBuilder();
		do
		{
			char c = (char)_buffer[i];
			i++;
			if (c == '\r')
			{
			}
			else if (c == '\n')
			{
				String mesg = buf.toString();
				buddy.sendim(mesg);
				buf.setLength(0);
				_offset = i;
			}
			else
			{
				buf.append(c);
			}
			if (i == MUD.READ_BUF_LEN)
			{
				i = 0;
			}
		}
		while (i != _endpos);
		*/
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
		return MUD.READ_BUF_LEN;
	}

	@Override
	public final int writeBufLen()
	{
		return MUD.WRITE_BUF_LEN;
	}

	@Override
	public final void clear()
	{
		//_in.clear();
		_out.clear();
	}

	public final void send(String mesg)
	{
		assert mesg != null;
		int len = mesg.length();
		assert len > 0;
		byte[] array = new byte[len+2];
		int i = 0;
		for (; i < len; i++)
		{
			array[i] = (byte)mesg.charAt(i);
		}
		array[i++] = '\n';
		array[i++] = 0;
		_out.push(array);
	}
}
