package edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class LanguageSpecific {
    protected Set<String> stopwords = new HashSet<>();

    public abstract Collection<String> tokenize(String content);

    public abstract Collection<String> clean(Collection<String> content);

    public abstract Collection<String> tokenizeAndClean(String content);

    public boolean isStopWord(String s) {
	return stopwords.contains(s);
    }


}
