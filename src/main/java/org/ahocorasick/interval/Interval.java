package org.ahocorasick.interval;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Interval implements Intervalable, Serializable {

    private int start;
    private int end;

    public Interval(final int start, final int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public int size() {
        return end - start + 1;
    }

    public boolean overlapsWith(Interval other) {
        return this.start <= other.getEnd() && this.end >= other.getStart();
    }

    public boolean overlapsWith(int point) {
        return this.start <= point && point <= this.end;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Intervalable)) {
            return false;
        }
        Intervalable other = (Intervalable) o;
        return this.start == other.getStart() && this.end == other.getEnd();
    }

    @Override
    public int hashCode() {
        return this.start % 100 + this.end % 100;
    }

    @Override
    public int compareTo(Intervalable other) {
        int comparison = this.start - other.getStart();
        return comparison != 0 ? comparison : this.end - other.getEnd();
    }

    @Override
    public String toString() {
        return this.start + ":" + this.end;
    }

}
