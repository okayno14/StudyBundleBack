package controller;

import java.util.*;

public class Authoriser
{
	private       Map<String, Long> tokens = new HashMap<>();
	private final int               tokenLength;
	private final long              authenticationTime;

	public Authoriser(int tokenLength, long authenticationTime)
	{
		this.tokenLength        = tokenLength;
		this.authenticationTime = authenticationTime;
	}

	private char getNumber()
	{
		int begin = 48;
		int end   = 57;
		int code  = (int) (begin + Math.random() * (end - begin + 1));
		return (char) code;
	}

	private char getLetter()
	{
		int begin = 97;
		int end   = 122;
		int code  = (int) (begin + Math.random() * (end - begin + 1));
		return (char) code;
	}

	public String genToken()
	{
		StringBuffer tokenText = null;
		do
		{
			tokenText = new StringBuffer();
			for (int i = 0; i < tokenLength; i++)
			{
				tokenText.append(getLetter());
				tokenText.append(getNumber());
			}
		} while (tokens.containsKey(tokenText.toString()));
		tokens.put(tokenText.toString(), System.currentTimeMillis() + authenticationTime);
		return tokenText.toString();
	}

	public boolean existsToken(String tokenText)
	{
		return tokens.containsKey(tokenText);
	}

	public long timeLeft(String tokenText)
	{
		Long res = tokens.get(tokenText);
		if (res == null)
		{
			return 0L;
		}
		return res.longValue();
	}

	public void removeToken(String tokenText)
	{
		tokens.remove(tokenText);
	}
}
