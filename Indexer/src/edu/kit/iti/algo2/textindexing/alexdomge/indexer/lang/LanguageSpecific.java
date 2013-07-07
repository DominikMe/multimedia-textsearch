package edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.iti.algo2.textindexing.util.TokenTool;

public abstract class LanguageSpecific {
    private static final int MIN_WORD_LENGTH = 4;
    protected Set<String> stopwords = new HashSet<>();

    public Collection<String> tokenize(String content) {
	content = content.replaceAll("[:;?.,!()\\[\\]]", " ");
	String[] s = content.split("\\s");
	return Arrays.asList(s);
    }

    public Collection<String> clean(Collection<String> content) {
	List<String> a = new ArrayList<String>(content.size());
	for (String string : content) {
	    if (!badWord(string))
		a.add(string);
	}
	return a;
    }

    public Collection<String> tokenizeAndClean(String content) {
	return clean(tokenize(content));
    }

    public boolean isStopWord(String s) {
	return stopwords.contains(s);
    }

    public boolean badWord(String s) {
	return isStopWord(s) || s.length() < MIN_WORD_LENGTH
		|| contains(s, "!§$%&/()=?`<>|_");
    }

    private boolean contains(String s, String badchars) {
	for (Character c : badchars.toCharArray()) {
	    if (s.indexOf(c) >= 0)
		return true;
	}
	return false;
    }

}
