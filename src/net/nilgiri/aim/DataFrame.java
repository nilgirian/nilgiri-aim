package net.nilgiri.aim;

import java.util.regex.Matcher;

/** We may override data frame for other frame types */
class DataFrame
{
	public final static String REVISION = "$Revision: 1.3 $";
	final byte[] data;
	final DataType type;
	final String[] arg;

	/** Create DataFrame, appending byte 0. The Data Frame payload is a null
	 terminated string when traveling from client to host.
	 */
	DataFrame(String mesg)
	{
		assert mesg != null;
		assert mesg.length() > 0;
		StringBuilder buf = new StringBuilder(mesg).append((char)0);
		this.data = buf.toString().getBytes();
		type = DataType._MESG_;
		this.arg = null;
	}

	/** Create DataFrame, and do not append byte 0. Data Frame is NOT null
		terminated when traveling from host to client.
		*/
	DataFrame(byte[] data)
	{
		assert data != null;
		assert data.length > 0;
		this.type = DataType.get(data);
		this.data = data;
		if (type.pattern == null)
		{
			arg = null;
		}
		else
		{
			String message = new String(data);
			Matcher matcher = type.pattern.matcher(message);
			if (matcher.matches())
			{
				int count = matcher.groupCount();
				arg = new String[count+1];
				for (int i = 0; i <= count; i++)
				{
					arg[i] = matcher.group(i);
				}
			}
			else
			{
				throw new TOC2MUDException("mismatch "+type+"|"+type.pattern
						+"|"+message);
			}
		}
	}
}
