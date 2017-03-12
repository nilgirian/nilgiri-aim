package net.nilgiri.aim;

import java.util.regex.Pattern;

enum DataType
{
	/** SIGN_ON: <Client Version Supported>
		This is sent after a successful toc_signon command is sent to TOC.
		If the command was successful either the FLAP conneciton will be dropped
		or you will receive an ERROR message.
		*/
	SIGN_ON("^SIGN_ON:(.*)$"),
	/** CONFIG:<config>
		(v1) A user's config. Config can be empty in which case the host was not
		able to retrieve it or a config didn't exist for the user. See
		toc_set_config for the format.
		*/
	//CONFIG("^CONFIG:(.*)$"),
	/** CONFIG2:<config>
		(v2) The only difference between CONFIG2 and CONFIG is that instead of
		"b buddy1", folor example it would be "b:buddy1".  Also the last item is
		always "done:<lf>".

		TOC2 has also added support for server-stored aliases. A user's
		server-stored alias, if they have one, is right after the username,
		seperated by a colon.

		A word about configs: in TOC2.0, everything is automatically saved to your
		config and your config is automatically loaded when you sign on. That is,
		you don't have to read the config and manually add all the buddies. If
		they show up in the config, they've already been added.
		*/
	CONFIG2("^CONFIG2:(.*)$"),

	/** NICK:<Nickname>
		Tells you yhour correct nickname (i.e. how it should be capitalized and
		spacing)
		*/
	NICK("^NICK:(.*?)$"),
	/** IM_IN:<Source User>:<Auto Response T/F?>:<Message>
		(v1) Receive an IM from some one. Everything after the third colon is the
		incoming message, including other colons.
		*/
	//IM_IN("IM_IN:(.*?):(.*?):(.*)$"),
	/** IM_IN_ENC:<user>:<auto>:<???>:<???>:<buddy status>:<???>:<???>:en:<message>
		(v2) This command received insead of IM_IN. It is similar to TOC1.0 except
		there are a few new parameters. One of them is language and another is the
		buddy status, but the rest are unknown.
		*/
	IM_IN_ENC2("^IM_IN_ENC2:(.*?):.*?:.*?:(.*?):.*?:.*?:en:(.*)$"),
	/** UPDATE_BUDDY:<Buddy User>:<Online? T/F>:<Evil Amount>:<Signon Time>:<Idle Time>:<UC>
		(v1) This one command handles arrival/depart/updates. Evil Amount is a
		percentage, Signon Time is a UNix epoc, idle time is in minutes, UC
		(User Classs) is two/three character string.
		uc[0]:
		' '  - Ignore
		'A'  - Oscar Admin
		'U'  - Oscar Unconfirmed
		'O'  - Oscar Normal
		Update:
		'C'  - User is on wireless
		'U'  - Oscar Unconfirmed
		'O'  - Oscar Normal
		Update:
		'C'  - User is on wireless
		uc[2]
		'\0' - Ignore
		' '  - Ignore
		'U'  - The user has set their unavailable flag.
		*/
	//UPDATE_BUDDY("UPDATE_BUDDY:(.*?):(.*?):(.*?):(.*?):(.*?):(.*)"),
	/** UPDATE_BUDDY2:<screenname>:<online>:<warning>:<signon time>:<idletime>:<userclass>:<???>
		(v2) Same as TOC1.0 except there is a myster parameter.
		*/
	UPDATE_BUDDY2("^UPDATE_BUDDY2:(.*?):(.*?):(.*?):(.*?):(.*?):(.*?):.*$"),
	/** ERROR:<Error Code>:Var args */
	ERROR("^ERROR:(.*?)(:.*)$"), //TODO this is wrong
	/** EVILED:<new evil>:<name of eviler, blank if anonymous>
		The user was just eviled.
		*/
	EVILED("^EVILED:(.*?):(.*)$"),
	/** CHAT_JOIN:<Chat Room Id>:<Chat Room Name>
		We were able to join this chat room. The Chat Room Id is internal to TOC.
		*/
	CHAT_JOIN("^CHAT_JOIN:(.*?):(.*)$"),
	/** CHAT_IN:<Chat Room Id>:<Source User>:<Whisper? T/F>:<Message>
		(v1) A chat message was sent in a chat room.
		*/
	//CHAT_IN("CHAT_IN:(.*?):(.*)$"),
	/** CHAT_IN_ENC:<Chat Room Id>:<user>:<Whisper T/F>:<???>:en:<message>
		(v2) This command received instead of CHAT_IN. It is similar to TOC 1.0
		except there are two new parameters. One of them is language, the other is
		unknown but usually "A".
		*/
	CHAT_IN_ENC("^CHAT_IN_ENC:(.*?):(.*?):(.*?):.*?:en:(.*)$"),
	/** CHAT_UPDATE_BUDDY:<Chat Room Id>:<Inside? T/F>:<User 1>:<User 2>:...
		This one command handles arrivals/departs from a chat room. The very first
		message of this type for each chat room contains the users already in the
		room.
		*/
	CHAT_UPDATE_BUDDY("^CHAT_UPDATE_BUDDY:(.*?):(.*?):(.*)$"),
	/** CHAT_INVITE:<Chat Room Name>:<Chat Room Id>:<Invite Sender>:<Message>
		We are being invited to a chat room.
		*/
	CHAT_INVITE("^CHAT_INVITE:(.*?):(.*?):(.*?):(.*)$"),
	/** CHAT_LEFT:<Chat Room Id>
		Tells TIC connection to chat room has been dropped.
		*/
	CHAT_LEFT("^CHAT_LEFT:(.*)$"),
	/** GOTO_URL:<Window Name>:<URL>
		Goto a URL. Window Name is the suggested internal name of the window to use.
		(Java supports this.)
		Note: the actual url is usally "http://<ip of toc server>:9898/"+URL_SENT
		*/
	GOTO_URL("^GOTO_URL:(.*?):(.*)$"),
	/** DIR_STATUS:<Return Code>:<Optional args>
		<Return Code> is always 0 for success status.
		*/
	DIR_STATUS("^DIR_STATUS:(.*?):(.*)$"),
	/** ADMIN_NICK_STATUS:<Return Coe>:<Optional args>
		 <Return Code> is always 0 for success status.
		 */
	ADMIN_NICK_STATUS("^ADMIN_NICK_STATUS:(.*?):(.*)$"),
	/** ADMIN_PASSWD_STATUS:<Return Code>:<Optional args>
		<Return Code> is always 0 for success status.
		*/
	ADMIN_PASSWD_STATUS("^ADMIN_PASSWD_STATUS:(.*?):(.*)$"),
	/** PAUSE
		Tells TIC to pause so we can do migration.
		*/
	PAUSE(null),
	/** RVOUS_PROPOSE:<user>:<uuid>:<cookie>:<seq>:<rip>:<pip>:<vip>:<port>
		[:tlv tag1:tlv value1[:tlv tag2:tlv value2[:...]]]
		Another user has proposed that we rendezvous with them to perform the
		service specified by <uuid>. They want us to connect to them, we have their
		rendezvous ip, their proposer_ip, and their verified_ip. The tlv values
		are base64 encoded.
		*/
	RVOUS_PROPOSE("^RVOUS_PROPOSE:(.*?):(.*?):(.*?):(.*?):(.*?):(.*?):(.*?):(.*?):(.*)$"),

	//other (v2) stuff below

	/** NEW_BUDDY_REPLY2:<buddy>:<action>
		This shows up after you add a buddy. The action can be either "added",
		which means that the buddy was added correctly, or "auth" which is used to
		signify that user has requested authorization to you to their buddy list.
		*/
	NEW_BUDDY_REPLY2("^NEW_BUDDY_REPLY2:(.*?):(.*)$"),
	/** UPDATE2:b:<username>:<???>:<alias>
		We receive this when somebody's server-stored alias is updated.
		*/
	UPDATE2("^UPDATE2:b:(.*?):.*?:(.*)$"),
	/** INSERTED2:g:<group name>
		A new group as been added to the buddy list.
		*/
	/** INSERTED2:b:<alias>:<username>:<group>
		A new screenname has been added.
		*/
	/** INSERTED2:d:<username>
		Somebody has been added to the deny list.
		*/
	/** INSERTED2:p:<username>
		Somebody has been added to the permit list.

		These will be sent whenver the buddy list is modified from a different
		location, which hapens when one is logged in two different places. It's a
		good idea to handle these, otherwise the buddy list displayed could become
		out of synch with what's on the server.
		*/
	INSERTED2("^INSERTED2:([gbdp]):(.*?)(:.*)$"),
	/** DELETED2:g:<group name>
		A group has been deleted from the buddy list.
		*/
	/** DELETED2:b:<username>:<group>
		A user has been deleted from the buddy list.
		*/
	/** DELETED2:d:<username>
		A user has been removed from the deny list.
		*/
	/** DELETED2:p:<username>
		A user has been removed from the permit list.

		These commands are similar to the INSERTED2 commands, in that they provide
		dynamic updates whenver the buddy list is modified from a different
		location.
		*/
	DELETED2("^DELETED2:(.*):(.*):$"),
	/** CLIENT_EVENT2:<username>:<typing status>
		These are typing notifications. 0 means stopped. 1 means text entered, and
		2 means typing.
		*/
	CLIENT_EVENT("^CLIENT_EVENT:(.*?):([012])$"),
	/** BUDDY_CAPS2:<username>:<capability1>,<capability2>,...
		This packet describes a partciular user's capabilities, such as file
		transfer, buddy icons, etc.
		*/
	BUDDY_CAPS2("^BUDDY_CAPS2:(.*?)(:.*)$"),
	/** BART2:<username>:<unknown>
		The structure of this message is not yet understood. It most likely provides
		buddy icon information about a user, such as whether they have a buddy icon
		or not and the hashcode necessary to request it from the server.
		*/
	BART2("^BART2:(.*?):(.*)$"),


	//Everything else bucket (not in spec)
	_MESG_(null)
	;

	public final static String REVISION = "$Revision: 1.7 $";

	final String str;
	final Pattern pattern;

	DataType(String regex)
	{
		this.str = this.name();
		if (regex == null)
		{
			pattern = null;
		}
		else
		{
			pattern = Pattern.compile(regex, Pattern.DOTALL);
		}
	}

	final static DataType get(byte[] bytes)
	{
		for (DataType type : DataType.values())
		{
			int i = 0;
			int len = type.str.length();
			String str = type.str;
			boolean found = true;
			while (i < len)
			{
				if (bytes[i] != str.charAt(i))
				{
					found = false;
					break;
				}
				i++;
			}
			if (found)
			{
				if (i >= bytes.length)
				{
					System.err.println("unexpected byte length:"+bytes.length+" "+i);
					return type;
				}
				if (bytes[i] == ':')
				{
					return type;
				}
				return _MESG_;
			}
		}
		return _MESG_;
	}

	private final static String err(String mesg, String[] arg, Integer... argv)
	{
		//arg[] should be in the format:
		//arg[0] - full message
		//arg[1] - error number
		//arg[2] - :var1:var2:...:varn
		StringBuilder buf = new StringBuilder();
		if (argv != null)
		{
			//argc[] :var1:var2...:varn
			// we need to consume the first :
			String[] argc = arg[2].substring(1).split(":");
			buf.append(String.format(mesg, argc));
		}
		else
		{
			buf.append(mesg);
		}
		return buf.append('[').append(arg[2]).append(']').toString();
	}

	private final static String errmsg(String[] arg)
	{
		int err = 0;
		try
		{
			err = Integer.valueOf(arg[1]);
		}
		catch (NumberFormatException e)
		{
			throw new TOC2MUDException(e);
		}
		switch (err)
		{
			//General Errors
			case 901:
				/** $1 not currently available
				*/
				return err("General Error: %s not currently available.", arg, 1);
			case 902:
				/** Warning of $1 not currently available
					*/
				return err("General Error: Warning of %s not currently available",
						arg, 1);
			case 903:
				/** A message has been dropped, you are exceeding the server speed limit
					*/
				return err("General Error: A message has been dropped, you are exceeding the server speed limit.", arg);

			//Admin Errors
			case 911:
				/** Error validating input
					*/
				return err("Admin Error: Error validating input.", arg);
			case 912:
				/** Invalid account
					*/
				return err("Admin Error: Invalid account.", arg);
			case 913:
				/** Error encountered while processing request
					*/
				return err("Admin Error: Error encountered while processing request.", arg);
			case 914:
				/** Service unavailable
					*/
				return err("Admin Error: Service unavailable.", arg);

			//Chat Errors
			case 950:
				/** Chat in $1 is unavailable
					*/
				return err("Chat Error: Chat in %s is unavailable.", arg, 1);


			//IM & Info Errors
			case 960:
				/** You are sending message too fast to %s
					*/
				return err("IM & Info Error: You are sending message too fast to %s", arg, 1);
			case 961:
				/** You missed an im from $1 because it was too big
					*/
				return err("IM & Info Error: You missed an IM from %s because it was too big.", arg, 1);
			case 962:
				/** You missed an im from $1 because it was sent too fast
					*/
				return err("IM & Info Error: You missed an IM from %s because it was sent too fast.", arg, 1);
	
			//Dir Errors
			case 970:
				/** Failure
					*/
				return err("Dir Error: Failure.", arg);
			case 971:
				/** Too many matches
					*/
				return err("Dir Error: Too many matches.", arg);
			case 972:
				/** Need more qualifiers
					*/
				return err("Dir Error: Need more qualifiers.", arg);
			case 973:
				/** Dir service temporarily unavailable
					*/
				return err("Dir Error: Dir service unavailable.", arg);
			case 974:
				/** Email lookup restricted
					*/
				return err("Dir Error: Email lookup restricted.", arg);
			case 975:
				/** Keyword ignored
					*/
				return err("Dir Error: Keyword ignored.", arg);
			case 976:
				/** No keywords
					*/
				return err("Dir Error: No keywords.", arg);
			case 977:
				/** Language not supported
					*/
				return err("Dir Error: Language not supported.", arg);
			case 978:
				/** Country not supported
					*/
				return err("Dir Error: Country not supported.", arg);
			case 979:
				/** Failure unknown $1
					*/
				return err("Dir Error: Failure unknown %s.", arg);

			//Auth Errors
			case 980:
				/** Incorrect nickname or password
					*/
				return err("Auth Error: Incorrect nickname or password.", arg);
			case 981:
				/** The service is temporarily unavailable
					*/
				return err("Auth Error: The service is temporarily unavailable.", arg);
			case 982:
				/** Your warning level is currently too high to sign on
					*/
				return err("Auth Error: Your warning level is currently too high to sign on.", arg);
			case 983:
				/** You have been connecting and disconnecting too frequently. Wait 10
					minutes and try again. If you continue to try, you will need to wait
					even longer.
					*/
				return err("You have been connecting and disconnecting too frequently. Wait 10 minutes and try again. If you continue to try, you will need to wait even longer.", arg);
			case 989:
				/** An unknown signon error has occurred %s
					*/
				return err("An unknown signon error has occurred %s.", arg, 1);

			//Errors unspecified by the spec
			default:
				return err("Unspecified Error", arg);
		}
	}

	public final static String errmsg(Flap flap)
	{
		if (flap.frame.type != DataType.ERROR)
		{
			return "no error message";
		}
		return errmsg(flap.frame.arg);
	}
}
