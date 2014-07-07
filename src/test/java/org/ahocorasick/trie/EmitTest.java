package org.ahocorasick.trie;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

import org.junit.Test;

public class EmitTest {

    @Test
    public void equals() {
        Emit<Integer> one = new Emit<Integer>(13, 42, null, 0);
        Emit<Integer> two = new Emit<Integer>(13, 42, null, 0);
        assertEquals(one, two);
    }

    @Test
    public void notEquals() {
        Emit<Integer> one = new Emit<Integer>(13, 42, null, 0);
        Emit<Integer> two = new Emit<Integer>(13, 43, null, 0);
        assertNotSame(one, two);
    }

}
