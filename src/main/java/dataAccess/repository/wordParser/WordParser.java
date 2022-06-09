package dataAccess.repository.wordParser;

import exception.DataAccess.DataAccessException;
import exception.DataAccess.FormatNotSupported;
import exception.DataAccess.WordParserException;
import org.apache.poi.openxml4j.util.ZipSecureFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordParser implements WordParserStrategy
{
	private HashMap<String, WordParserStrategy> stratMap = new HashMap<>();
	private final Pattern                       pattern  = Pattern.compile(".[a-z]+$");

	public WordParser()
	{
		//отключаем защиту от ZIP-бомб
		ZipSecureFile.setMinInflateRatio(0);

		//пихаем все реализации в Mapper
		WordParserStrategy wordParserStrategy = new WordParserDOC();
		stratMap.put(wordParserStrategy.getFormat(), wordParserStrategy);

		wordParserStrategy =new WordParserDOCX();
		stratMap.put(wordParserStrategy.getFormat(), wordParserStrategy);
	}


	@Override
	public String parseDoc(String name, InputStream file)
	{
		String             format             = extractFormat(name);
		WordParserStrategy wordParserStrategy = stratMap.get(format);

		if(wordParserStrategy ==null)
		{
			throw new DataAccessException(new FormatNotSupported(format));
		}

		try
		{
			return wordParserStrategy.parseDoc(name, file);
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
		String             format             = extractFormat(fileName);
		WordParserStrategy wordParserStrategy = stratMap.get(format);

		if(wordParserStrategy ==null)
		{
			throw new DataAccessException(new FormatNotSupported(format));
		}

		try
		{
			return wordParserStrategy.parseDoc(fileName);
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
