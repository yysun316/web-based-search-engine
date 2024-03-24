package hk.ust.comp4321.utils;

import java.io.Serializable;

public class Posting implements Serializable {

    public String doc;
    public int freq;

    Posting(String doc, int freq) {
        this.doc = doc;
        this.freq = freq;
    }
}
