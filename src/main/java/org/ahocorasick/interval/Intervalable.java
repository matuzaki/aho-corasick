package org.ahocorasick.interval;

public interface Intervalable extends Comparable<Intervalable> {

    public int getStart();

    public int getEnd();

    public int size();

}
