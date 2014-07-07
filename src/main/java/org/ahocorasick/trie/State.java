package org.ahocorasick.trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 * A state has various important tasks it must attend to:
 * </p>
 *
 * <ul>
 * <li>success; when a character points to another state, it must return that
 * state</li>
 * <li>failure; when a character has no matching state, the algorithm must be
 * able to fall back on a state with less depth</li>
 * <li>emits; when this state is passed and keywords have been matched, the
 * matches must be 'emitted' so that they can be used later on.</li>
 * </ul>
 *
 * <p>
 * The root state is special in the sense that it has no failure state; it
 * cannot fail. If it 'fails' it will still parse the next character and start
 * from the root node. This ensures that the algorithm always runs. All other
 * states always have a fail state.
 * </p>
 *
 * @author Robert Bor
 */
@SuppressWarnings("serial")
public class State<T> implements Serializable {

    /** effective the size of the keyword */
    private final int depth;

    /**
     * only used for the root state to refer to itself in case no matches have
     * been found
     */
    private final State<T> rootState;

    /**
     * referred to in the white paper as the 'goto' structure. From a state it
     * is possible to go to other states, depending on the character passed.
     */
    private Map<Character, State<T>> success = new TreeMap<Character, State<T>>();

    /** if no matching states are found, the failure state will be returned */
    private State<T> failure = null;

    /**
     * whenever this state is reached, it will emit the matches keywords for
     * future reference
     */
    private List<StringPayload<T>> emits = null;

    static class StringPayload<T> implements Serializable {

        final String keyword;
        final T payload;

        StringPayload(String keyword, T payload) {
            this.keyword = keyword;
            this.payload = payload;
        }

    }

    public State() {
        this(0);
    }

    public State(int depth) {
        this.depth = depth;
        this.rootState = depth == 0 ? this : null;
    }

    private State<T> nextState(Character character, boolean ignoreRootState) {
        State<T> nextState = this.success.get(character);
        if (!ignoreRootState && nextState == null && this.rootState != null) {
            nextState = this.rootState;
        }
        return nextState;
    }

    public State<T> nextState(Character character) {
        return nextState(character, false);
    }

    public State<T> nextStateIgnoreRootState(Character character) {
        return nextState(character, true);
    }

    public State<T> addState(Character character) {
        State<T> nextState = nextStateIgnoreRootState(character);
        if (nextState == null) {
            nextState = new State<T>(this.depth + 1);
            this.success.put(character, nextState);
        }
        return nextState;
    }

    public int getDepth() {
        return this.depth;
    }

    public void addEmit(String keyword, T payload) {
        addEmit(new StringPayload<T>(keyword, payload));
    }

    public void addEmit(StringPayload<T> sp) {
        if (this.emits == null) {
            this.emits = new ArrayList<StringPayload<T>>();
        }
        this.emits.add(sp);
    }

    public void addEmit(Collection<StringPayload<T>> emits) {
        for (StringPayload<T> emit : emits) {
            addEmit(emit);
        }
    }

    public Collection<StringPayload<T>> emits() {
        return this.emits == null ? Collections.<StringPayload<T>> emptyList()
                : this.emits;
    }

    public State<T> failure() {
        return this.failure;
    }

    public void setFailure(State<T> failState) {
        this.failure = failState;
    }

    public Collection<State<T>> getStates() {
        return this.success.values();
    }

    public Collection<Character> getTransitions() {
        return this.success.keySet();
    }

}
