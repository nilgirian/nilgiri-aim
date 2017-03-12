package net.nilgiri.aim;

final class TOC2
{
	public final static String REVISION = "$Revision: 1.9 $";
	//public final static String HOST = "toc.oscar.aol.com";

	/** Connect to aimexress.oscar.aol.com not toc.oscar.aol.com, otherwise
		retrieving profiles will not work without much refreshing. */

	//user assigned constants
	public final static String SCREENNAME = "nilgirian";
	public final static String PASSWORD = "d0N+5hAR3";
	public final static String LANGUAGE = "Eng0lish";
	public final static String VERSION = "TOC2MUD";
	public final static String REV = Main.PROJECT_REVISION;
	public final static String ME = "Frederick Nacino";
	public final static String MAILTO = "sin@nilgiri.net";
	public final static String URL = MUD.URL;

	//given by spec
	public final static String HOST = "toc.oscar.aol.com";
	//public final static String HOST = "localhost";
	public final static int PORT = 9898;
	public final static String AUTHORIZATION_HOST = "login.oscar.aol.com";
	public final static String AUTHORIZATION_PORT = "29999";
	public final static String FLAPON = "FLAPON\r\n\r\n";
	public final static int HEADER_FIELD_LEN = 6;
	public final static String ROASTING_STRING = "Tic/Toc";
	public final static int FLAP_VERSION = 1;
	public final static int FLAP_VERSION_FIELD_LEN = 4;
	public final static byte TLV_TAG = 1;
	public final static int TLV_TAG_FIELD_LEN = 2;
	public final static int NAME_LEN_FIELD_LEN = 2;
	public final static int TOC_TO_CLIENT_MAX = 8192;
	public final static int CLIENT_TO_TOC_MAX = 4096;
	public final static int SIMPLE_CODE_CONST = 7696;

	//derived from user
	public final static int SCREENNAME_LEN = SCREENNAME.length();

	//derived from spec
	public final static int SIGNON_HEADER_FIELD_LEN
		= FLAP_VERSION_FIELD_LEN + TLV_TAG_FIELD_LEN + NAME_LEN_FIELD_LEN;
	public final static int MAX_BUFFER_SIZE
		= Math.max(TOC_TO_CLIENT_MAX, CLIENT_TO_TOC_MAX);
	final static byte[] ROASTING_BYTES = ROASTING_STRING.getBytes();

	/** This is a simple code created with the first letter of the screen name
		and password. Here is some generic code:
		sn = ascii value of the first letter of the screen name
		pw = ascii value of the first charcter of the password
		return 7696 * sn * pw
		For example if the screenname was "test" and the password was "x5435" the
		result would be 107128320
		*/
	public final static int simpleCode(String screenname, String password)
	{
		int sn = screenname.charAt(0);
		int pw = password.charAt(0);
		return sn * pw * SIMPLE_CODE_CONST;
	}


	public final static String toc2_login()
	{
		StringBuilder buf = new StringBuilder()
			.append("toc2_login ")
			.append(AUTHORIZATION_HOST)
			.append(' ')
			.append(AUTHORIZATION_PORT)
			.append(' ')
			.append(SCREENNAME)
			.append(' ')
			.append(roast(PASSWORD))
			.append(' ')
			.append(LANGUAGE)
			.append(" TIC:")
			.append(VERSION)
			.append(" 160 ")
			.append("US \"\" \"\" 3 0 30303 -kentucky -utf8 ")
			.append(simpleCode(SCREENNAME, PASSWORD));
		/*
		int len = buf.length();
		byte[] bytes = new byte[len+1];
		int i = 0;
		for (; i < len; i++)
		{
			bytes[i] = (byte)buf.charAt(i);
		}
		bytes[i] = 0;
		*/
		return buf.toString();
	}

	public final static String toc_set_info()
	{
		StringBuilder buf = new StringBuilder()
			.append("toc_set_info \"")
			.append(VERSION)
			.append(' ')
			.append(REV)
			.append(" AIMBot by ")
			.append(' ')
			.append(htmlHref(MAILTO, ME))
			.append(' ')
			.append(htmlHref(URL, URL))
			.append('\"');
		return buf.toString();
	}

	public final static String encode(CharSequence mesg)
	{
		StringBuilder buf = new StringBuilder();
		int len = mesg.length();
		for (int i = 0; i < len; i++)
		{
			char c = mesg.charAt(i);
			if (c == '"')
			{
				buf.append('\\');
				buf.append('"');
			}
			else if (c == '\r')
			{
				buf.append('\\');
				buf.append('r');
			}
			else if (c == '\n')
			{
				buf.append('\\');
				buf.append('n');
			}
			else if (c == '[')
			{
				buf.append('\\');
				buf.append('[');
			}
			else if (c == ']')
			{
				buf.append('\\');
				buf.append(']');
			}
			else
			{
				buf.append(c);
			}
		}
		return buf.toString();
	}

	public final static String toc_init_done()
	{
		return "toc_init_done";
	}

	public final static String toc2_send_im(String user, String message)
	{
		StringBuilder buf = new StringBuilder()
			.append("toc2_send_im ")
			.append(user)
			.append(' ')
			.append('"')
			.append(encode(message))
			.append('"');
		return buf.toString();
	}

	private final static CharSequence htmlHref(String url, String link)
	{
		StringBuilder buf = new StringBuilder()
			.append("<a href=\\\"")
			.append(url)
			.append("\\\">")
			.append(link)
			.append("</a>");
		return buf;
	}

	/** Passwords are roasted when sent to the host. This is done so they aren't
		sent in "clear text" over the wire, although they are still trivial to
		decode. Roasting is performed by first xoring each byte in the password
		with the equivalent modulo byte in the roasting string. The result is
		then converted to ascii hex, and prepended with "0x". So for example the
		password "password" roasts to "0x2408105c23001130"
		*/
	public final static String roast(String pw)
	{
		StringBuilder buf = new StringBuilder("0x");
		byte[] pwbytes = pw.getBytes();
		for (int i = 0; i < pwbytes.length; i++)
		{
			String hex = Integer.toHexString(pwbytes[i]
					^ ROASTING_BYTES[i % ROASTING_BYTES.length]);
			if (hex.length() == 1)
			{
				buf.append('0');
			}
			buf.append(hex);
		}
		return buf.toString();
	}

	/** All user names from clients to TOC should be normalized (spaces removed
		and lowercased).
		*/
	private final static String normalize(CharSequence str)
	{
		StringBuilder buf = new StringBuilder();
		int len = str.length();
		for (int i = 0; i < len; i++)
		{
			char c = str.charAt(i);
			if (c == ' ')
			{
				continue; //spaces removed
			}
			if (c >= 'A' && c <= 'Z')
			{
				c = (char) ('a' + (int)c - 'A'); //lowercase
			}
			buf.append(c);
		}
		return buf.toString();
	}

	private final static byte[] writeFlapHeader(FrameType type,
			int sequence, int datalen)
	{
		byte[] bytes = new byte[datalen+HEADER_FIELD_LEN];
		int i = 0;
		bytes[i++] = (byte) '*'; //ASTERISK (literal ASCII '*')
		bytes[i++] = type.ord; //Frame Type
		bytes[i++] = (byte) (sequence >> 8); //Sequence Number (byte 1)
		bytes[i++] = (byte) (sequence & 0xff);//Sequence Number (byte 2)
		bytes[i++] = (byte) (datalen >> 8); //Data Length (byte 1)
		bytes[i++] = (byte) (datalen & 0xff);//Data Length (byte 2)
		assert i == HEADER_FIELD_LEN;
		return bytes;
	}

	private static class Frame
	{
		final FrameType type;
		final int sequence;
		final byte[] data;

		Frame(FrameType type, int sequence, byte[] data)
		{
			assert(type != null);
			this.type = type;
			assert(sequence != 0);
			this.sequence = sequence;
			assert(data != null);
			this.data = data;
		}
	}

	final static byte[] writeFlap(FrameType type,
			int sequence, byte[] data)
	{
		byte[] bytes = writeFlapHeader(type, sequence, data.length);
		return writeFrame(bytes, data);
	}

	final static byte[] flap_signon()
	{
		String normalName = normalize(SCREENNAME);
		byte[] nameBytes = normalName.getBytes();
		int length = nameBytes.length+SIGNON_HEADER_FIELD_LEN;
		byte bytes[] = new byte[length];
		int i = 0;
		bytes[i++] = (byte) (FLAP_VERSION >> 24);
		bytes[i++] = (byte) (FLAP_VERSION >> 16);
		bytes[i++] = (byte) (FLAP_VERSION >> 8);
		bytes[i++] = (byte) (FLAP_VERSION & 0xff);
		bytes[i++] = (byte) (TLV_TAG >> 8);
		bytes[i++] = (byte) (TLV_TAG & 0xff);
		bytes[i++] = (byte) (nameBytes.length >> 8);
		bytes[i++] = (byte) (nameBytes.length & 0xff);
		for (int j = 0; j < nameBytes.length; j++)
		{
			bytes[i++] = nameBytes[j];
		}
		assert i == nameBytes.length + SIGNON_HEADER_FIELD_LEN;
		return bytes;
	}

	private final static byte[] writeFrame(byte[] bytes, byte[] data)
	{
		int i = 0;
		for (i = 0; i < data.length; i++)
		{
			bytes[i+HEADER_FIELD_LEN] = data[i];
		}
		assert i+HEADER_FIELD_LEN == bytes.length;
		return bytes;
	}

	public final static int readFlapLength(byte[] bytes)
	{
		int i = 0;
		if (bytes[i++] != (byte) '*')
		{
			throw new TOC2MUDException();
		}
		return bytes[HEADER_FIELD_LEN-2] << 8 | bytes[HEADER_FIELD_LEN-1];
	}

	private final static Frame readFlap(byte[] bytes)
	{
		int i = 0;
		if (bytes[i++] != (byte) '*')
		{
			throw new TOC2MUDException();
		}
		FrameType type = FrameType.get(bytes[i++]);
		int sequence = bytes[i++] << 8 | bytes[i++];
		int datalen = bytes[i++] << 8 | bytes[i++];
		assert i == HEADER_FIELD_LEN;
		byte[] data = new byte[datalen];
		for (int j = 0; j < datalen; j++)
		{
			data[j] = bytes[++i];
		}
		return new Frame(type, sequence, data);
	}

	public final static void main(String args[])
	{
	}

	static
	{
		assert 107128320 == simpleCode("test", "x5435");
		assert "0x2408105c23001130".equals(roast("password"));
	}
}
