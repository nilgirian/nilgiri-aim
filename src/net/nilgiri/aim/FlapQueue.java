package net.nilgiri.aim;

final class FlapQueue extends AbstractQueue<Flap>
{
	public final static String REVISION = "$Revision: 1.5 $";
	public final static boolean DEBUG = false;

	//we need to make sure that we have enough in the buffer for two frames
	public static final int READ_BUF_LEN = TOC2.MAX_BUFFER_SIZE * 2;
	public static final int WRITE_BUF_LEN = TOC2.MAX_BUFFER_SIZE * 2;
	private byte[] _buffer = new byte[READ_BUF_LEN];
	private int _offset = 0;
	private int _endpos = 0;

	//for queueing the bytes from the channel
	FlapQueue()
	{
	}

	public final void push(String mesg) //XXX for testing
	{
		enqueue(new Flap(mesg));
	}

	@Override
	public final void push(byte[] array)
	{
		enqueue(new Flap(array));
	}

	public final void pushRaw(byte[] array)
	{
		enqueue(new Flap(FrameType._RAW_, array));
	}

	@Override
	public final byte[] poll()
	{
		Flap flap = dequeue();
		if (flap == null)
		{
			return null;
		}
		if (DEBUG) System.err.println(flap);
		return flap.bytes;
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
