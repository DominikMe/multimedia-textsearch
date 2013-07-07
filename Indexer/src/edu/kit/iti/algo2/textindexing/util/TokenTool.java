package edu.kit.iti.algo2.textindexing.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TokenTool {

    public static List<String> stemm(List<String> tokens) {
	List<String> s = new LinkedList<String>();
	for (String string : tokens) {
	    s.add(stemm(string));
	}
	return s;
    }

    public static String stemm(String word) {
	Stemmer st = new Stemmer();
	st.add(word.toCharArray(), word.length());
	st.stem();
	return new String(Arrays.copyOf(st.getResultBuffer(),
		st.getResultLength()));
    }

}
