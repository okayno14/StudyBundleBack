package dataAccess.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Report
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id=-1;
	@Column(name = "sym_count")
	private long symCount;
	@Column(name="unique_words")
	private long uniqueWords;
	@Column(name = "word_count")
	private long wordCount;
	@Column(name = "sym_count_no_space")
	private long symCountNoSpace;
	@Transient
	private boolean isSetCompatible=true;
	@Transient
	private List<String> text = new ArrayList<String>();
	@Transient
	private Map<String,Integer> textVector = new HashMap<String,Integer>();

}
