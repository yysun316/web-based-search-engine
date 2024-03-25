package hk.ust.comp4321.utils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

public class PostingFreqComparator implements Comparator<Posting>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Required for serialization

    @Override
    public int compare(Posting p1, Posting p2) {
        return Integer.compare(p1.getFreq(), p2.getFreq());
    }
}
