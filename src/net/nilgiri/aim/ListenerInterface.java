package net.nilgiri.aim;

interface ListenerInterface
{
	public final static String REVISION = "$Revision: 1.2 $";
	//sending messages
	abstract void send(String mesg);
}
