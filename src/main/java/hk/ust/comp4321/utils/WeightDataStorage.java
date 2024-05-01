package hk.ust.comp4321.utils;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;

import java.io.IOException;
import java.util.List;

public class WeightDataStorage {
    private RecordManager recordManager;
    private HTree weight;

    /***
     * Constructor of the class
     * @param recordManagerName the name of the record manager
     * @throws IOException if the record manager is not found
     */
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

    /***
     *  Add a new entry to the database
     * @param matrixName the name of the matrix
     * @param matrix the matrix to be added
     * @throws IOException if the matrix is not found
     */
    public void updateEntry(String matrixName, List<List<Double>> matrix) throws IOException {
        weight.remove(matrixName);
        weight.put(matrixName, matrix);
        recordManager.commit();
    }

    /***
     * Get the entry from the database
     * @param matrixName the name of the matrix
     * @return the matrix
     * @throws IOException if the matrix is not found
     */
    public List<List<Double>> getEntry(String matrixName) throws IOException {
        return (List<List<Double>>) weight.get(matrixName);
    }
}
