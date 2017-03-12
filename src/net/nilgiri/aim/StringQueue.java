package net.nilgiri.aim;

final class StringQueue extends AbstractQueue<String>
{
	public final static String REVISION = "$Revision: 1.3 $";
	//we need to make sure that we have enough in the buffer for two frames
	final static int READ_BUF_LEN = TOC2.CLIENT_TO_TOC_MAX * 2;
	final static int WRITE_BUF_LEN = TOC2.CLIENT_TO_TOC_MAX * 2;
	private byte[] _buffer = new byte[READ_BUF_LEN];
	private int _offset = 0;

	//for queuing the bytes from the channel, and chopping into standard form:
	// <text>\n
	StringQueue()
	{
	}

	@Override
	public final void push(byte array[])
	{
		enqueue(new String(array));
		/*
		int strlen = 0;
		for (int i = 0; i < array.length; i++)
		{
			char c = (char)array[i];
			if (c == '\n')
			{
				assert strlen < TOC2.CLIENT_TO_TOC_MAX;
				enqueue(new String(_buffer, 0, _offset));
				_offset = 0;
			}
			else
			{
				_buffer[_offset++] = array[i];
				strlen++;
			}
		}
		*/
	}

	@Override
	public final byte[] poll()
	{
		String string = dequeue();
		if (string == null)
		{
			return null;
		}
		return string.getBytes();
	}

	@Override
	public final int readBufLen()
	{
		return READ_BUF_LEN;
	}

	@Override
	public final int writeBufLen()
	{
		return WRITE_BUF_LEN;
	}
}
