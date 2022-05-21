package dataAccess.repository.wordParser;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.io.InputStream;

public class WordParserDOCX implements WordParserState
{
	private XWPFDocument           docxFile;
	private XWPFWordExtractor      extractor;
	private XWPFHeaderFooterPolicy headerFooterPolicy;


	public WordParserDOCX()
	{
	}

	@Override
	public String parseDoc(String name, InputStream file) throws IOException, InvalidFormatException
	{
		docxFile = new XWPFDocument(OPCPackage.open(file));
		headerFooterPolicy = new XWPFHeaderFooterPolicy(docxFile);
		XWPFWordExtractor extractor = new XWPFWordExtractor(docxFile);
		String text = extractor.getText();
		return text;
	}

	@Override
	public String parseDoc(String fileName) throws InvalidFormatException, IOException
	{
		docxFile = new XWPFDocument(OPCPackage.open(fileName));
		headerFooterPolicy = new XWPFHeaderFooterPolicy(docxFile);
		XWPFWordExtractor extractor = new XWPFWordExtractor(docxFile);
		String text = extractor.getText();
		return text;
	}

	@Override
	public String getFormat()
	{
		return "docx";
	}
}
