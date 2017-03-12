package net.nilgiri.aim;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Main
{
	public final static String REVISION = "$Revision: 1.5 $";
	public final static String PROJECT_REVISION;
	static
	{
		// big complicated mess just to figure out some coherent project revision
		int major = 0;
		int minor = 0;
		String rstr = "Revision:";
		Pattern pattern = Pattern.compile("^\\$"+rstr+" (\\d*)\\.(\\d*) \\$$",
				Pattern.DOTALL);
		Matcher matcher;

		(matcher = pattern.matcher(AbstractConnection.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(Buddy.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(ConnectionAIM.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(DataFrame.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(DataType.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(Flap.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(FlapQueue.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(FrameType.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(ListenerInterface.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(MUD.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(Main.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(QueueInterface.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(StringQueue.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(TOC2.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		(matcher = pattern.matcher(TOC2MUDException.REVISION)).matches();
		major += Integer.valueOf(matcher.group(1));
		minor += Integer.valueOf(matcher.group(2));

		PROJECT_REVISION = "v"+major+"."+minor;
	}

	public final static void main(String args[])
	{
		try
		{
			ConnectionAIM aim = new ConnectionAIM();
			System.err.println(TOC2.VERSION+" "+PROJECT_REVISION);
			aim.connect();
			if (aim.connected())
			{
				aim.start();
				System.err.println("connected to AIM.");
			}
			else
			{
				System.err.println("Could not connect to AIM.");
				System.exit(0);
			}
		}
		catch (IOException e)
		{
			throw new TOC2MUDException();
		}
	}
}
