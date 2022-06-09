package dataAccess.repository.wordParser;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WordParserDOC implements WordParserStrategy
{

	public WordParserDOC()
	{
	}

	@Override
	public String parseDoc(String name, InputStream file) throws IOException
	{
		HWPFDocument  docFile   = new HWPFDocument(file);
		WordExtractor extractor = new WordExtractor(docFile);
		return extractor.getText();
	}

	@Override
	public String parseDoc(String fileName) throws IOException
	{
		try(InputStream is = new FileInputStream(fileName))
		{
			return parseDoc(fileName, is);
		}
	}

	@Override
	public String getFormat()
	{
		return "doc";
	}
}
