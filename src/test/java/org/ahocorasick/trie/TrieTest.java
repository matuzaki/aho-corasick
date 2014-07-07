package org.ahocorasick.trie;

import static junit.framework.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

public class TrieTest {

    public void keywordAndTextAreTheSame() {
        Trie<Integer> trie = new Trie<Integer>();
        trie.addKeyword("abc", 0);
        Collection<Emit<Integer>> emits = trie.parseText("abc");
        Iterator<Emit<Integer>> iterator = emits.iterator();
        checkEmit(iterator.next(), 0, 2, "abc", 0);
    }

    @Test
    public void textIsLongerThanKeyword() {
        Trie<Integer> trie = new Trie<Integer>();
        trie.addKeyword("abc", 0);
        Collection<Emit<Integer>> emits = trie.parseText(" abc");
        Iterator<Emit<Integer>> iterator = emits.iterator();
        checkEmit(iterator.next(), 1, 3, "abc", 0);
    }

    @Test
    public void variousKeywordsOneMatch() {
        Trie<Integer> trie = new Trie<Integer>();
        trie.addKeyword("abc", 0);
        trie.addKeyword("bcd", 1);
        trie.addKeyword("cde", 2);
        Collection<Emit<Integer>> emits = trie.parseText("bcd");
        Iterator<Emit<Integer>> iterator = emits.iterator();
        checkEmit(iterator.next(), 0, 2, "bcd", 1);
    }

    @Test
    public void ushersTest() {
        Trie<Integer> trie = new Trie<Integer>();
        trie.addKeyword("hers", 0);
        trie.addKeyword("his", 1);
        trie.addKeyword("she", 2);
        trie.addKeyword("he", 3);
        Collection<Emit<Integer>> emits = trie.parseText("ushers");
        assertEquals(3, emits.size()); // she @ 3, he @ 3, hers @ 5
        Iterator<Emit<Integer>> iterator = emits.iterator();
        checkEmit(iterator.next(), 1, 3, "she", 2);
        checkEmit(iterator.next(), 2, 3, "he", 3);
        checkEmit(iterator.next(), 2, 5, "hers", 0);
    }

    @Test
    public void misleadingTest() {
        Trie<Integer> trie = new Trie<Integer>();
        trie.addKeyword("hers", 0);
        Collection<Emit<Integer>> emits = trie.parseText("h he her hers");
        Iterator<Emit<Integer>> iterator = emits.iterator();
        checkEmit(iterator.next(), 9, 12, "hers", 0);
    }

    @Test
    public void recipes() {
        Trie<Integer> trie = new Trie<Integer>();
        trie.addKeyword("veal", 0);
        trie.addKeyword("cauliflower", 1);
        trie.addKeyword("broccoli", 2);
        trie.addKeyword("tomatoes", 3);
        Collection<Emit<Integer>> emits = trie
                .parseText("2 cauliflowers, 3 tomatoes, 4 slices of veal, 100g broccoli");
        Iterator<Emit<Integer>> iterator = emits.iterator();
        checkEmit(iterator.next(), 2, 12, "cauliflower", 1);
        checkEmit(iterator.next(), 18, 25, "tomatoes", 3);
        checkEmit(iterator.next(), 40, 43, "veal", 0);
        checkEmit(iterator.next(), 51, 58, "broccoli", 2);
    }

    @Test
    public void longAndShortOverlappingMatch() {
        Trie<Integer> trie = new Trie<Integer>();
        trie.addKeyword("he", 0);
        trie.addKeyword("hehehehe", 1);
        Collection<Emit<Integer>> emits = trie.parseText("hehehehehe");
        Iterator<Emit<Integer>> iterator = emits.iterator();
        checkEmit(iterator.next(), 0, 1, "he", 0);
        checkEmit(iterator.next(), 2, 3, "he", 0);
        checkEmit(iterator.next(), 4, 5, "he", 0);
        checkEmit(iterator.next(), 0, 7, "hehehehe", 1);
        checkEmit(iterator.next(), 6, 7, "he", 0);
        checkEmit(iterator.next(), 2, 9, "hehehehe", 1);
        checkEmit(iterator.next(), 8, 9, "he", 0);
    }

    @Test
    public void nonOverlapping() {
        Trie<Integer> trie = new Trie<Integer>().removeOverlaps();
        trie.addKeyword("ab", 0);
        trie.addKeyword("cba", 1);
        trie.addKeyword("ababc", 2);
        Collection<Emit<Integer>> emits = trie.parseText("ababcbab");
        assertEquals(2, emits.size());
        Iterator<Emit<Integer>> iterator = emits.iterator();
        // With overlaps: ab@1, ab@3, ababc@4, cba@6, ab@7
        checkEmit(iterator.next(), 0, 4, "ababc", 2);
        checkEmit(iterator.next(), 6, 7, "ab", 0);
    }

    @Test
    public void startOfChurchillSpeech() {
        Trie<Integer> trie = new Trie<Integer>().removeOverlaps();
        trie.addKeyword("T", 0);
        trie.addKeyword("u", 0);
        trie.addKeyword("ur", 0);
        trie.addKeyword("r", 0);
        trie.addKeyword("urn", 0);
        trie.addKeyword("ni", 0);
        trie.addKeyword("i", 0);
        trie.addKeyword("in", 0);
        trie.addKeyword("n", 0);
        trie.addKeyword("urning", 0);
        Collection<Emit<Integer>> emits = trie.parseText("Turning");
        assertEquals(2, emits.size());
    }

    @Test
    public void partialMatch() {
        Trie<Integer> trie = new Trie<Integer>().onlyWholeWords();
        trie.addKeyword("sugar", 0);
        Collection<Emit<Integer>> emits = trie
                .parseText("sugarcane sugarcane sugar canesugar"); // left,
                                                                   // middle,
                                                                   // right test
        assertEquals(1, emits.size()); // Match must not be made
        checkEmit(emits.iterator().next(), 20, 24, "sugar", 0);
    }

    @Test
    public void tokenizeFullSentence() {
        Trie<Integer> trie = new Trie<Integer>();
        trie.addKeyword("Alpha", 0);
        trie.addKeyword("Beta", 0);
        trie.addKeyword("Gamma", 0);
        Collection<Token> tokens = trie
                .tokenize("Hear: Alpha team first, Beta from the rear, Gamma in reserve");
        assertEquals(7, tokens.size());
        Iterator<Token> tokensIt = tokens.iterator();
        assertEquals("Hear: ", tokensIt.next().getFragment());
        assertEquals("Alpha", tokensIt.next().getFragment());
        assertEquals(" team first, ", tokensIt.next().getFragment());
        assertEquals("Beta", tokensIt.next().getFragment());
        assertEquals(" from the rear, ", tokensIt.next().getFragment());
        assertEquals("Gamma", tokensIt.next().getFragment());
        assertEquals(" in reserve", tokensIt.next().getFragment());
    }

    @Test
    public void bug5InGithubReportedByXCurry() {
        Trie<Integer> trie = new Trie<Integer>().caseInsensitive()
                .onlyWholeWords();
        trie.addKeyword("turning", 0);
        trie.addKeyword("once", 1);
        trie.addKeyword("again", 2);
        trie.addKeyword("börkü", 3);
        Collection<Emit<Integer>> emits = trie
                .parseText("TurninG OnCe AgAiN BÖRKÜ");
        assertEquals(4, emits.size()); // Match must not be made
        Iterator<Emit<Integer>> it = emits.iterator();
        checkEmit(it.next(), 0, 6, "turning", 0);
        checkEmit(it.next(), 8, 11, "once", 1);
        checkEmit(it.next(), 13, 17, "again", 2);
        checkEmit(it.next(), 19, 23, "börkü", 3);
    }

    @Test
    public void caseInsensitive() {
        Trie<Integer> trie = new Trie<Integer>().caseInsensitive();
        trie.addKeyword("turning", 0);
        trie.addKeyword("once", 1);
        trie.addKeyword("again", 2);
        trie.addKeyword("börkü", 3);
        Collection<Emit<Integer>> emits = trie
                .parseText("TurninG OnCe AgAiN BÖRKÜ");
        assertEquals(4, emits.size()); // Match must not be made
        Iterator<Emit<Integer>> it = emits.iterator();
        checkEmit(it.next(), 0, 6, "turning", 0);
        checkEmit(it.next(), 8, 11, "once", 1);
        checkEmit(it.next(), 13, 17, "again", 2);
        checkEmit(it.next(), 19, 23, "börkü", 3);
    }

    @Test
    public void tokenizeTokensInSequence() {
        Trie<Integer> trie = new Trie<Integer>();
        trie.addKeyword("Alpha", 0);
        trie.addKeyword("Beta", 0);
        trie.addKeyword("Gamma", 0);
        Collection<Token> tokens = trie.tokenize("Alpha Beta Gamma");
        assertEquals(5, tokens.size());
    }

    @Test
    public void zeroLengthTestBug7InGithubReportedByXCurry() {
        Trie<Integer> trie = new Trie<Integer>().removeOverlaps()
                .onlyWholeWords().caseInsensitive();
        trie.addKeyword("", 0);
        trie.tokenize("Try a natural lip and subtle bronzer to keep all the focus on those big bright eyes with NARS Eyeshadow Duo in Rated R And the winner is... Boots No7 Advanced Renewal Anti-ageing Glycolic Peel Kit ($25 amazon.com) won most-appealing peel.");
    }

    private void checkEmit(Emit<Integer> next, int expectedStart,
            int expectedEnd, String expectedKeyword, Integer expectedPayload) {
        assertEquals(expectedStart, next.getStart());
        assertEquals(expectedEnd, next.getEnd());
        assertEquals(expectedKeyword, next.getKeyword());
        assertEquals(expectedPayload, next.getPayload());
    }

}
