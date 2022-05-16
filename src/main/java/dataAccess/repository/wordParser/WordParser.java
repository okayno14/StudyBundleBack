package dataAccess.repository.wordParser;

import exception.DataAccess.DataAccessException;
import exception.DataAccess.FormatNotSupported;
import exception.DataAccess.WordParserException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordParser implements WordParserState
{
	private HashMap<String, WordParserState> stateMap = new HashMap<>();
	private final Pattern pattern = Pattern.compile(".[a-z]+$");

	public WordParser()
	{
		//пихаем все реализации в Mapper
		WordParserState wordParserState = new WordParserDOC();
		stateMap.put(wordParserState.getFormat(),wordParserState);

		wordParserState=new WordParserDOCX();
		stateMap.put(wordParserState.getFormat(),wordParserState);
	}


	@Override
	public String parseDoc(String name, InputStream file)
	{
		String format = extractFormat(name);
		WordParserState wordParserState = stateMap.get(format);

		if(wordParserState==null)
		{
			throw new DataAccessException(new FormatNotSupported(format));
		}

		try
		{
			return wordParserState.parseDoc(name, file);
		}
		catch (Exception ex)
		{
			standartExceptionHandler(ex);
		}
		return "";
	}

	@Override
	public String parseDoc(String fileName)
	{
		String format = extractFormat(fileName);
		WordParserState wordParserState = stateMap.get(format);

		if(wordParserState==null)
		{
			throw new DataAccessException(new FormatNotSupported(format));
		}

		try
		{
			return wordParserState.parseDoc(fileName);
		}
		catch (Exception ex)
		{
			standartExceptionHandler(ex);
		}
		return "";
	}

	@Override
	public String getFormat()
	{
		return null;
	}

	private String extractFormat(String name)
	{
		String res="";
		Matcher matcher = pattern.matcher(name);
		if(matcher.find())
		{
			res=matcher.group();
		}
		res = res.replace(".","");
		return res;
	}

	private void standartExceptionHandler(Exception ex)
	{
		StringBuffer msg = new StringBuffer("Ошибка при парсинге ворда в память\n");
		msg.append(new StringBuffer(ex.toString()));
		throw new WordParserException(msg.toString(), ex);
	}
}
