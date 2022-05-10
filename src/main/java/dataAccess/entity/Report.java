package dataAccess.entity;

import business.bundle.Similarity;
import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class Report implements Serializable, Similarity
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Expose
	private long                 id              = -1L;
	@Column(name = "file_name")
	@Expose
	private String               fileName        = null;
	@Column(name = "sym_count")
	@Expose
	private long                 symCount        = 0L;
	@Column(name = "unique_words")
	@Expose
	private long                 uniqueWords     = 0L;
	@Column(name = "word_count")
	@Expose
	private long                 wordCount       = 0L;
	@Column(name = "sym_count_no_space")
	@Expose
	private long                 symCountNoSpace = 0L;
	@Transient
	private boolean              isSetCompatible = true;
	@Transient
	private List<String>         text            = new LinkedList<>();
	@Transient
	private Map<String, Integer> textVector      = new HashMap<String, Integer>();

	public Report()
	{
	}

	public Report(String textStr, String filename)
	{
		this.fileName = filename;
		fillMetricAndTextVec(textStr);
	}

	private void fillText(String textStr)
	{
		if (!this.hasText())
		{
			//Поиск слов в документе
			Pattern pattern = Pattern.compile("[а-яА-Я0-9ёa-zA-Z-]+");
			Matcher matcher = pattern.matcher(textStr);

			//Слова, найденные регулярным выражением добавляются в коллекцию
			while (matcher.find())
			{
				text.add(matcher.group());
			}
		}
	}

	private int wordCount(String word)
	{
		int    res = 0;
		String w   = new String();
		for (Iterator<String> i = text.iterator(); i.hasNext(); )
		{
			w = i.next();
			if (w.equalsIgnoreCase(word))
			{
				res++;
			}
		}
		return res;
	}

	private void fillMetricAndTextVec(String textStr)
	{
		if (!this.hasTextVector())
		{
			fillText(textStr);
			symCount        = textStr.length();
			textStr         = textStr.replaceAll(" ", "");
			symCountNoSpace = textStr.length();

			HashSet<String> words = new HashSet<String>();
			words.addAll(text);

			for (Iterator<String> i = words.iterator(); i.hasNext(); )
			{
				String w   = i.next();
				int    buf = wordCount(w);
				wordCount += buf;
				textVector.put(w, buf);
			}
			uniqueWords = textVector.keySet().size();
			text.clear();
		}
	}

	private boolean hasText()
	{
		return text.size() > 0;
	}

	public boolean hasTextVector()
	{
		return textVector.size() > 0;
	}

	@Override
	public Set<Object> getSet()
	{
		return new HashSet<Object>(textVector.keySet());
	}

	public boolean isEmpty()
	{
		return fileName == null;
	}

	@Override
	public boolean isSetCompatible()
	{
		return isSetCompatible;
	}

	public void setSetCompatible(boolean setCompatible)
	{
		isSetCompatible = setCompatible;
	}

	public Map<String, Integer> getTextVector()
	{
		return new HashMap<String, Integer>(textVector);
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileNameAndMeta(String fileName, String textStr)
	{
		this.fileName = fileName;
		fillMetricAndTextVec(textStr);
	}

	public long getId()
	{
		return id;
	}

	public long getSymCount()
	{
		return symCount;
	}

	public long getUniqueWords()
	{
		return uniqueWords;
	}

	public long getWordCount()
	{
		return wordCount;
	}

	public long getSymCountNoSpace()
	{
		return symCountNoSpace;
	}


}
