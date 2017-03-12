package net.nilgiri.aim;

interface QueueInterface
{
	public final static String REVISION = "$Revision: 1.4 $";
	/** Where to place bytes read from the connection. */
	abstract void push(byte[] bytes);
	/** Where to get bytes ot push onto the connection. */
	abstract byte[] poll();
	/** Check if we can poll this queue. */
	abstract boolean canPoll();
	/** How big should our incoming read buffer be. */
	abstract int readBufLen();
	/** How big should our outgoing write buffer be. */
	abstract int writeBufLen();
	/** Clear the queue */
	abstract void clear();
}
