package org.ahocorasick.trie;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TrieConfig implements Serializable {

    private boolean allowOverlaps = true;

    private boolean onlyWholeWords = false;

    private boolean caseInsensitive = false;

    public boolean isAllowOverlaps() {
        return allowOverlaps;
    }

    public void setAllowOverlaps(boolean allowOverlaps) {
        this.allowOverlaps = allowOverlaps;
    }

    public boolean isOnlyWholeWords() {
        return onlyWholeWords;
    }

    public void setOnlyWholeWords(boolean onlyWholeWords) {
        this.onlyWholeWords = onlyWholeWords;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }
}
