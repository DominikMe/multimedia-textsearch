package edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang;

import java.util.Collection;

import edu.kit.iti.algo2.textindexing.util.TokenTool;

public class GermanSpecific extends LanguageSpecific {

    @Override
    public Collection<String> tokenizeAndClean(String content) {
	return clean(tokenize(content));
    }

    @Override
    public Collection<String> tokenize(String content) {
	return TokenTool.tokenize(content);
    }

    @Override
    public Collection<String> clean(Collection<String> content) {
	return content;
    }
}