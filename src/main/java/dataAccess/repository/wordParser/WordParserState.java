package dataAccess.repository.wordParser;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;

public interface WordParserState
{
	String parseDoc(String name, InputStream file) throws IOException, InvalidFormatException;
	String parseDoc(String fileName) throws IOException, InvalidFormatException;
	String getFormat();
}
