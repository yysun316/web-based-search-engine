package hk.ust.comp4321.utils;

import java.io.Serializable;
import java.util.Comparator;

public class Posting implements Serializable
{
    public int id;
    public int freq;
    public Posting(int id, int freq)
    {
        this.id = id;
        this.freq = freq;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }
    public static Comparator<Posting> getFreqComparator() {
        return Comparator.comparingInt(Posting::getFreq);
    }
}

