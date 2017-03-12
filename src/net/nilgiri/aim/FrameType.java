package net.nilgiri.aim;

enum FrameType
{
	_RAW_(0), //Not in spec but used to send raw data
	SIGNON(1),
	DATA(2),
	ERROR(3), //Not used by TOC
	SIGNOFF(4), //Not used by TOC
	KEEP_ALIVE(5)
	;

	public final static String REVISION = "$Revision: 1.3 $";

	final byte ord;
	FrameType(int ord)
	{
		this.ord = (byte) ord;
	}

	final static FrameType get(int n)
	{
		for (FrameType type : FrameType.values())
		{
			if (n == type.ord)
			{
				return type;
			}
		}
		throw new TOC2MUDException();
	}
}
