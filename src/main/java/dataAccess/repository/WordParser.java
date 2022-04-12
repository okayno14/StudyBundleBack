package dataAccess.repository;

import exception.DataAccess.WordParserException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class WordParser
{
	private XWPFDocument           docxFile;
	private XWPFWordExtractor      extractor;
	private XWPFHeaderFooterPolicy headerFooterPolicy;


	private void readWord(File file) throws IOException, InvalidFormatException
	{
		try
		{
			docxFile = new XWPFDocument(OPCPackage.open(file));
		}
		catch (InvalidFormatException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			throw e;
		}

	}

	private void readWord(InputStream file) throws IOException, InvalidFormatException
	{
		try
		{
			docxFile = new XWPFDocument(OPCPackage.open(file));
		}
		catch (InvalidFormatException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			throw e;
		}
	}

	private String getText()
	{
		headerFooterPolicy = new XWPFHeaderFooterPolicy(docxFile);
		XWPFWordExtractor extractor = new XWPFWordExtractor(docxFile);
		String text = extractor.getText();
		return text;
	}

	public String parseDoc(String fileName)
	{
		try
		{
			readWord(new File(fileName));
			return getText();
		}
		catch (Exception ex)
		{
			StringBuffer msg = new StringBuffer(ex.toString());
			msg.append("\n");
			msg.append("Ошибка при парсинге ворда в память");
			throw new WordParserException(msg.toString(),ex);
		}
	}

	public String parseDoc(InputStream file)
	{
		try
		{
			readWord(file);
			return getText();
		}
		catch (Exception ex)
		{
			StringBuffer msg = new StringBuffer(ex.toString());
			msg.append("\n");
			msg.append("Ошибка при парсинге ворда в память");
			throw new WordParserException(msg.toString(),ex);
		}
	}
}
