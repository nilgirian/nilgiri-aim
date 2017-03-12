package net.nilgiri.aim;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;

final class MUD
{
	public final static String REVISION = "$Revision: 1.15 $";
	public final static String HOST = "localhost"; //should NOT be anything else
	public final static int PORT = 8888;
	public final static int READ_BUF_LEN = 2048;
	public final static int WRITE_BUF_LEN = 2048;
	public final static String LOGIN = "nTOC2MUD";
	public final static String URL = "http://nilgiri.net/";

	public final static String FILE_HALT = "stop";
	public final static String FILE_REBOOT = "disable";
	public final static String INFO_STATUS = "INF_TOC2MUD_STATUS";

	public final static void halt()
	{
		try
		{
			touch(FILE_HALT);
		}
		catch (IOException e)
		{
			throw new TOC2MUDException(e);
		}
	}

	public final static void unhalt()
	{
		try
		{
			rm(FILE_HALT);
		}
		catch (IOException e)
		{
			throw new TOC2MUDException(e);
		}
	}

	public final static void reboot()
	{
		try
		{
			touch(FILE_REBOOT);
		}
		catch (IOException e)
		{
			throw new TOC2MUDException(e);
		}
	}

	private final static void touch(String filename) throws IOException
	{
		FileWriter out = new FileWriter(filename, true);
		out.flush();
		out.close();
	}

	private final static void rm(String filename) throws  IOException
	{
	}

	public final static boolean trusted(String name)
	{
		if (name.equals("nacino"))
		{
			return true;
		}
		if (name.equals("ericokumura"))
		{
			return true;
		}
		if (name.equals("melmerp"))
		{
			return true;
		}
		if (name.equals("russwong"))
		{
			return true;
		}
		return false;
	}

	private final static String mudinfo(String command) throws IOException
	{
		Socket socket = new Socket(HOST, 8808/*XXX PORT*/);
		InputStream si = socket.getInputStream();
		OutputStream so = socket.getOutputStream();
		so.write(command.getBytes());
		byte[] bytes = new byte[READ_BUF_LEN];
		si.read(bytes); //throw away first line
		so.write('\n'); //write empty line
		int len = si.read(bytes); //read this line
		assert len != READ_BUF_LEN; //do not exceed!!
		String buf = new String(bytes, 0, len);
		socket.close();
		return buf;
	}

	public final static String info()
	{
		try
		{
			return mudinfo(CMD_STATUS);
		}
		catch (IOException e)
		{
			return null;
		}
	}

	public final static String load()
	{
		try
		{
			return mudinfo(CMD_LOAD);
		}
		catch (IOException e)
		{
			return null;
		}
	}

	public final static void main(String args[]) throws IOException
	{
		Socket socket = new Socket("nilgiri.net", 8808);
		InputStream si = socket.getInputStream();
		OutputStream so = socket.getOutputStream();
		so.write(CMD_STATUS.getBytes());
		byte[] bytes = new byte[READ_BUF_LEN];
		si.read(bytes); //throw away first line
		so.write('\n'); //write empty line
		int len = si.read(bytes); //read this line
		assert len != READ_BUF_LEN; //do not exceed!!
		String buf = new String(bytes, 0, len);
		socket.close();
		System.out.println(buf);
	}

	private final static String CMD_STATUS = "INF_TOC2MUD_STATUS";
	private final static String CMD_LOAD = "INF_TOC2MUD_UPTIME";
}
