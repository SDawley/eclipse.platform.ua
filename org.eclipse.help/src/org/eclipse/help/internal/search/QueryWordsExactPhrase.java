/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.search;
import java.util.*;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
/**
 * Represents a quoted token in user search query words
 */
public class QueryWordsExactPhrase extends QueryWordsToken {
	private List words;
	public QueryWordsExactPhrase() {
		super(QueryWordsToken.EXACT_PHRASE, "");
		words = new ArrayList();
	}
	public void addWord(String word) {
		words.add(word);
		if (words.size() <= 1)
			value = word;
		else
			value += " " + word;
	}
	public List getWords() {
		return words;
	}
	/**
	 * Creates a lucene query for a field
	 */
	public Query createLuceneQuery(String field, float boost)
	{
		PhraseQuery q = new PhraseQuery();
		for (Iterator it = getWords().iterator(); it.hasNext();)
		{
			String word = (String) it.next();
			Term t = new Term("exact_"+field, word);
			q.add(t);
			q.setBoost(boost);
		}
		return q;
	}
}