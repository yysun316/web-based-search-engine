package hk.ust.comp4321.utils;

import java.io.*;
import java.util.List;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;

public class WeightDataStorage {
    private RecordManager recordManager;
    private HTree weight;

    public WeightDataStorage(String recordManagerName) throws IOException {
        this.recordManager = RecordManagerFactory.createRecordManager(recordManagerName);
        long recid = recordManager.getNamedObject("weight");
        if (recid != 0) {
            weight = HTree.load(recordManager, recid);
        } else {
            weight = HTree.createInstance(recordManager);
            recordManager.setNamedObject("weight", weight.getRecid());
        }
    }

    public void updateEntry(String matrixName, List<List<Double>> matrix) throws IOException {
        weight.remove(matrixName);
        weight.put(matrixName, matrix);
        recordManager.commit();
    }

    public List<List<Double>> getEntry(String matrixName) throws IOException {
        return (List<List<Double>>) weight.get(matrixName);
    }
}
