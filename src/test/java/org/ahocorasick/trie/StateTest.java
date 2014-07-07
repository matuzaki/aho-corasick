package org.ahocorasick.trie;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class StateTest {

    @Test
    public void constructSequenceOfCharacters() {
        State<Integer> rootState = new State<Integer>();
        rootState.addState('a').addState('b').addState('c');
        State<Integer> currentState = rootState.nextState('a');
        assertEquals(1, currentState.getDepth());
        currentState = currentState.nextState('b');
        assertEquals(2, currentState.getDepth());
        currentState = currentState.nextState('c');
        assertEquals(3, currentState.getDepth());
    }

}
