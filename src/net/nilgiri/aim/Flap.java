package net.nilgiri.aim;

import java.util.Random;

class Flap
{
	public final static String REVISION = "$Revision: 1.5 $";
	private static short _sequence = (short)new Random().nextInt();
	public static int version = 0;

	final FrameType type;
	final DataFrame frame;
	final short sequence;

	public final String toString()
	{
		StringBuilder buf = new StringBuilder()
			.append("* type:")
			.append(type)
			.append(" sequence:")
			.append(sequence)
			.append(" length: ")
			.append((int)frame.data.length & 0xffffffff)
			.append('\n');
		TOC2MUDException.hexdump(buf, frame.data);
		return buf.toString();
	}

	int datalen;

	final byte[] bytes; //What we pop out of the queue
	// This doubles the size of our flap, but since this is a transitory message
	// and makes things simple, we'll use it

	/** For writing DATA String messages. */
	Flap(String mesg)
	{
		this(FrameType.DATA, new DataFrame(mesg));
	}

	/** For writing String messages (append 0 byte). Presumably not DATA type. */
	Flap(FrameType type, String mesg)
	{
		this(type, new DataFrame(mesg));
	}
	
	/** For writing byte messages (no 0 byte append). */
	Flap(FrameType type, byte[] data)
	{
		this(type, new DataFrame(data));
	}

	/** For writing frames. */
	Flap(FrameType type, DataFrame frame)
	{
		assert type != null;
		this.type = type;
		this.frame = frame;
		this.datalen = frame.data.length;
		if (type == FrameType._RAW_)
		{
			this.sequence = _sequence++;
			this.bytes = frame.data;
		}
		else
		{
			this.sequence = _sequence++;
			this.bytes = TOC2.writeFlap(type, sequence, frame.data);
		}
	}

	/** For reading flap types from TOC server. */
	Flap(byte[] bytes)
	{
		this.bytes = bytes;
		int i = 0;
		if (bytes[i++] != (byte) '*')
		{
			throw new TOC2MUDException();
		}
		type = FrameType.get(bytes[i++]);
		sequence = (short) (0xff & bytes[i++] << 8 | 0xff & bytes[i++]);
		if (type == FrameType.SIGNON)
		{
			//_sequence = sequence; //TODO decide to keep this or not
		}
		datalen = 0xff & bytes[i++] << 8 | 0xff & bytes[i++];
		assert i == TOC2.HEADER_FIELD_LEN;
		byte[] data = new byte[datalen];
		for (int j = 0; j < datalen; j++)
		{
			data[j] = bytes[i++];
		}
		switch (type)
		{
			case SIGNON:
				i = 0;
				version = 0xff & data[i++] << 24;
				version |= 0xff & data[i++] << 16;
				version |= 0xff & data[i++] << 8;
				version |= 0xff & data[i];
				assert version == TOC2.FLAP_VERSION;
				break;
		}
		frame = new DataFrame(data);
	}

	public final static int readLength(byte[] bytes)
	{
		int i = 0;
		if (bytes[i++] != (byte) '*')
		{
			throw new TOC2MUDException(bytes);
		}
		int len = 0xff & bytes[TOC2.HEADER_FIELD_LEN-2] << 8
			| (0xff & bytes[TOC2.HEADER_FIELD_LEN-1]);
		if (len < 0)
		{
			System.err.println("len is negative: "+len);
			System.exit(1);
		}
		return len;
	}
}
