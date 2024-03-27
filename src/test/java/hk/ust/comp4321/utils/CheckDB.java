package hk.ust.comp4321.utils;

import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import java.io.IOException;

public class CheckDB {
    public static void checkHTree(HTree hTree) throws IOException {
        System.out.println("Checking the HTree:");
        FastIterator iter = hTree.keys();
        Object key;
        while ((key = iter.next()) != null) {
            if (hTree.get(key) instanceof WebNode)
                System.out.println(key + " -> " + ((WebNode) hTree.get(key)).getUrl());
            else
                System.out.println(key + " -> " + hTree.get(key));
        }
        System.out.println("End of the HTree.");
    }
}