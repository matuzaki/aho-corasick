package org.ahocorasick.trie;

import java.io.Serializable;

import org.ahocorasick.interval.Interval;

@SuppressWarnings("serial")
public class Emit<T> extends Interval implements Serializable {

    private final String keyword;
    private final T payload;

    public Emit(final int start, final int end, final String keyword,
            final T payload) {
        super(start, end);
        this.keyword = keyword;
        this.payload = payload;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public T getPayload() {
        return this.payload;
    }

    @Override
    public String toString() {
        return super.toString() + "=" + this.keyword + "\n"
                + payload.toString();
    }

}
