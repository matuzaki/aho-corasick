package org.ahocorasick.trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import org.ahocorasick.interval.IntervalTree;
import org.ahocorasick.interval.Intervalable;
import org.ahocorasick.trie.State.StringPayload;

/**
 *
 * Based on the Aho-Corasick white paper, Bell technologies:
 * ftp://163.13.200.222/assistant/bearhero/prog/%A8%E4%A5%A6/ac_bm.pdf
 * 
 * @author Robert Bor
 */
@SuppressWarnings("serial")
public class Trie<T> implements Serializable {

    private TrieConfig trieConfig;

    private State<T> rootState;

    private boolean failureStatesConstructed = false;

    public Trie(TrieConfig trieConfig) {
        this.trieConfig = trieConfig;
        this.rootState = new State<T>();
    }

    public Trie() {
        this(new TrieConfig());
    }

    public Trie<T> caseInsensitive() {
        this.trieConfig.setCaseInsensitive(true);
        return this;
    }

    public Trie<T> removeOverlaps() {
        this.trieConfig.setAllowOverlaps(false);
        return this;
    }

    public Trie<T> onlyWholeWords() {
        this.trieConfig.setOnlyWholeWords(true);
        return this;
    }

    public void addKeyword(String keyword, T payload) {
        if (keyword == null || keyword.length() == 0) {
            return;
        }
        State<T> currentState = this.rootState;
        for (Character character : keyword.toCharArray()) {
            currentState = currentState.addState(character);
        }
        currentState.addEmit(keyword, payload);
    }

    public Collection<Token> tokenize(String text) {

        Collection<Token> tokens = new ArrayList<Token>();

        Collection<Emit<T>> collectedEmits = parseText(text);
        int lastCollectedPosition = -1;
        for (Emit<T> emit : collectedEmits) {
            if (emit.getStart() - lastCollectedPosition > 1) {
                tokens.add(createFragment(emit, text, lastCollectedPosition));
            }
            tokens.add(createMatch(emit, text));
            lastCollectedPosition = emit.getEnd();
        }
        if (text.length() - lastCollectedPosition > 1) {
            tokens.add(createFragment(null, text, lastCollectedPosition));
        }

        return tokens;
    }

    private Token createFragment(Emit<T> emit, String text,
            int lastCollectedPosition) {
        return new FragmentToken(text.substring(lastCollectedPosition + 1,
                emit == null ? text.length() : emit.getStart()));
    }

    private Token createMatch(Emit<T> emit, String text) {
        return new MatchToken(
                text.substring(emit.getStart(), emit.getEnd() + 1), emit);
    }

    @SuppressWarnings("unchecked")
    public Collection<Emit<T>> parseText(String text) {
        checkForConstructedFailureStates();

        if (trieConfig.isCaseInsensitive()) {
            text = text.toLowerCase();
        }

        int position = 0;
        State<T> currentState = this.rootState;
        List<Emit<T>> collectedEmits = new ArrayList<Emit<T>>();
        for (Character character : text.toCharArray()) {
            currentState = getState(currentState, character);
            storeEmits(position, currentState, collectedEmits);
            position++;
        }

        if (trieConfig.isOnlyWholeWords()) {
            removePartialMatches(text, collectedEmits);
        }

        if (!trieConfig.isAllowOverlaps()) {
            IntervalTree intervalTree = new IntervalTree(
                    (List<Intervalable>) (List<?>) collectedEmits);
            intervalTree
                    .removeOverlaps((List<Intervalable>) (List<?>) collectedEmits);
        }

        return collectedEmits;
    }

    private void removePartialMatches(String searchText,
            List<Emit<T>> collectedEmits) {
        long size = searchText.length();
        List<Emit<T>> removeEmits = new ArrayList<Emit<T>>();
        for (Emit<T> emit : collectedEmits) {
            if ((emit.getStart() == 0 || !Character.isAlphabetic(searchText
                    .charAt(emit.getStart() - 1)))
                    && (emit.getEnd() + 1 == size || !Character
                            .isAlphabetic(searchText.charAt(emit.getEnd() + 1)))) {
                continue;
            }
            removeEmits.add(emit);
        }

        for (Emit<T> removeEmit : removeEmits) {
            collectedEmits.remove(removeEmit);
        }
    }

    private State<T> getState(State<T> currentState, Character character) {
        State<T> newCurrentState = currentState.nextState(character);
        while (newCurrentState == null) {
            currentState = currentState.failure();
            newCurrentState = currentState.nextState(character);
        }
        return newCurrentState;
    }

    private void checkForConstructedFailureStates() {
        if (!this.failureStatesConstructed) {
            constructFailureStates();
        }
    }

    private void constructFailureStates() {
        Queue<State<T>> queue = new LinkedBlockingDeque<State<T>>();

        // First, set the fail state of all depth 1 states to the root state
        for (State<T> depthOneState : this.rootState.getStates()) {
            depthOneState.setFailure(this.rootState);
            queue.add(depthOneState);
        }
        this.failureStatesConstructed = true;

        // Second, determine the fail state for all depth > 1 state
        while (!queue.isEmpty()) {
            State<T> currentState = queue.remove();

            for (Character transition : currentState.getTransitions()) {
                State<T> targetState = currentState.nextState(transition);
                queue.add(targetState);

                State<T> traceFailureState = currentState.failure();
                while (traceFailureState.nextState(transition) == null) {
                    traceFailureState = traceFailureState.failure();
                }
                State<T> newFailureState = traceFailureState
                        .nextState(transition);
                targetState.setFailure(newFailureState);
                targetState.addEmit(newFailureState.emits());
            }
        }
    }

    private void storeEmits(int position, State<T> currentState,
            List<Emit<T>> collectedEmits) {
        Collection<StringPayload<T>> emits = currentState.emits();
        if (emits != null && !emits.isEmpty()) {
            for (StringPayload<T> emit : emits) {
                collectedEmits.add(new Emit<T>(position - emit.keyword.length()
                        + 1, position, emit.keyword, emit.payload));
            }
        }
    }

}
