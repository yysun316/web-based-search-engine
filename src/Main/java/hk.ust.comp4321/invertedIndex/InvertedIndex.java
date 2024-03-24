package hk.ust.comp4321.invertedIndex;

import java.io.IOException;

public interface InvertedIndex {
    public void addEntry(Object obj1, Object obj2) throws IOException;
    public void delEntry(Object obj) throws IOException;
    public void printAll() throws IOException;
}
