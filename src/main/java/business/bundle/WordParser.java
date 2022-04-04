package business.bundle;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.util.ArrayList;

public class WordParser
{
	private XWPFDocument           docxFile;
	private XWPFWordExtractor      extractor;
	private XWPFHeaderFooterPolicy headerFooterPolicy;

	public String parseDoc(String fileName) throws Exception
	{
		ArrayList<String> words = new ArrayList<String>();
		try (FileInputStream file = new FileInputStream(fileName))
		{
			// открываем файл и считываем его содержимое в объект XWPFDocument
			docxFile           = new XWPFDocument(OPCPackage.open(file));
			headerFooterPolicy = new XWPFHeaderFooterPolicy(docxFile);

			// печатаем все содержимое Word файла
			String            text      = new String();
			XWPFWordExtractor extractor = new XWPFWordExtractor(docxFile);
			text = extractor.getText();


			return text;
		}
		catch (Exception ex)
		{
			StringBuffer msg = new StringBuffer(ex.toString());
			msg.append("\n");
			msg.append("Ошибка при парсинге ворда в память");
			throw new Exception(msg.toString());
		}
	}
}
