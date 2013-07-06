package edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang;

public enum Language {
    GERMAN(new GermanSpecific()), ENGLISH(new EnglishSpecific());

    public final LanguageSpecific languageSpecific;
    private Language(LanguageSpecific ls) {
	this.languageSpecific = ls;
    }
    
    public LanguageSpecific getLanguageSpecific() {
        return languageSpecific;
    }
}