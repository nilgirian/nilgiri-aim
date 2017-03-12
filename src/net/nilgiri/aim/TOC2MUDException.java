package net.nilgiri.aim;

class TOC2MUDException extends RuntimeException
{
	public final static String REVISION = "$Revision: 1.4 $";
	TOC2MUDException()
	{
		super("Oops..");
	}

	TOC2MUDException(String str)
	{
		super("Oops.. "+str);
	}

	TOC2MUDException(Exception e)
	{
		super("Oops..", e);
	}

	TOC2MUDException(byte[] bytes)
	{
		System.err.println("Oops..");
		dump(bytes);
	}

	TOC2MUDException(String err, byte[] bytes)
	{
		dump(err, bytes);
	}

	public final static boolean isPrint(int c)
	{
		if (c >= '0' && c <= '9')
		{
			return true;
		}
		if (c >= 'a' && c <= 'z')
		{
			return true;
		}
		if (c >= 'A' && c <= 'Z')
		{
			return true;
		}
		if (c == '~' || c == '!' || c == '@' || c == '#' || c == '$' || c == '$'
		|| c == '%' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')'
		|| c == '-' || c == '_' || c == '=' || c == '+' || c == '[' || c == '{'
		|| c == ']' || c == '}' || c == '\\' || c == '|' || c == ';' || c == ':'
		|| c == '\'' || c == '"' || c == ',' || c == '<' || c == '.' || c == '>'
		|| c == '/' || c == '?' || c == ' ')
		{
			return true;
		}
		return false;
	}

	public final static int MAX_ROWS = 20;

	public final static void dump(byte[] bytes)
	{
		dump(null, bytes);
	}

	public final static void dump(String str)
	{
		dump(null, str.getBytes());
	}

	public final static void dump(String err, byte[] bytes)
	{
		StringBuilder buf = new StringBuilder();
		if (err != null)
		{
			buf.append(err);
			buf.append('\n');
		}
		hexdump(buf, bytes);
		System.err.println(buf);
	}

	public final static void hexdump(StringBuilder buf, byte[] bytes)
	{
		int n = 0;
		int r = 0;
		boolean first = true;
		int i = 0;
		boolean fin = false;
		while (true)
		{
			if (first)
			{
				if (fin)
				{
					buf.append(' ');
				}
				else
				{
					if (isPrint(bytes[i]))
					{
						buf.append((char)bytes[i]);
					}
					else
					{
						buf.append('.');
					}
				}
			}
			else
			{
				if (i < bytes.length)
				{
					buf.append(String.format(" %02X", bytes[i]));
				}
			}
			n++;
			if ((n % 16) == 0)
			{
				if (first)
				{
					buf.append(" |");
					first = false;
					i -= 16;
				}
				else
				{
					buf.append('\n');
					if (fin)
					{
						break;
					}
					first = true;
					r++;
					if (r == MAX_ROWS)
					{
						break;
					}
				}
			}
			else if ((n % 8) == 0)
			{
				buf.append(' ');
			}
			i++;
			if (i == bytes.length)
			{
				fin = true;
			}
		}
		buf.append(bytes.length);
		buf.append(" bytes");
		if (r == MAX_ROWS)
		{
			buf.append(" (");
			buf.append(MAX_ROWS);
			buf.append(" max rows cut-off)");
		}
		buf.append('\n');
	}

	public final static void main(String args[])
	{
		String mesg = "Welcome to the land of Nilgiri. May your visit here be... interesting.\\r\\nThe Chat Room [1200] {NO_WEATHER GOD NO_TELEPORT_IN}\\r\\n   This quiet, cozy parlor is warmed by a gentle magical fire which twinkles\nhappily in a warm fireplace.  There are several exits out of this room,\\nclearly the owner needs them.\\nA large bulletin board is mounted on a wall here..It has a soft glowing aura!\\r\\nThe faithful Nilgiri Servant awaits commands.\\r\\n\\r\\n<10101h> ";
		byte[] array = mesg.getBytes();
		System.err.println("Len:"+mesg.length());
		dump(array);
	}
}

