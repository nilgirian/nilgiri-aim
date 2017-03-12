package net.nilgiri.aim;

import java.util.LinkedList;

abstract class AbstractQueue<T> implements QueueInterface
{
	public final static String REVISION = "$Revision: 1.4 $";
	final private LinkedList<T> _queue = new LinkedList<T>();
	private int _size;

	AbstractQueue()
	{
		_size = 0;
	}

	/** Returns message from the queue and takes them off in the order that they
		were put on.
		@return The head of the queue.If there are no other elements return null.
		*/
	final T dequeue()
	{
		if (_queue.isEmpty())
		{
			return null;
		}
		else
		{
			_size--;
			return _queue.remove();
		}
	}

	public final boolean canPoll()
	{
		return _queue.isEmpty() == false;
	}

	/** Put data at the end of the queue.
		@param t The data to enqueue.
		*/
	final void enqueue(T t)
	{
		_queue.addLast(t);
		_size++;
	}

	final void head(T t)
	{
		_queue.addFirst(t);
		_size++;
	}

	int size()
	{
		return _size;
	}

	@Override
	public final void clear()
	{
		_queue.clear();
	}
}
