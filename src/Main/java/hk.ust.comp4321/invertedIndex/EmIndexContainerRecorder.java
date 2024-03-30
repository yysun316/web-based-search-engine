package hk.ust.comp4321.invertedIndex;

import hk.ust.comp4321.indexer.EmIndexContainer;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.Serializer;
import jdbm.htree.HTree;

import java.io.IOException;

public class EmIndexContainerRecorder {

    private RecordManager recordManager;
    private HTree objectTree;
    private Serializer serializer;

    public EmIndexContainerRecorder(String recordManagerName) throws IOException {
        recordManager = RecordManagerFactory.createRecordManager(recordManagerName);
        long recid = recordManager.getNamedObject("objectTreeRec");
        if (recid != 0) {
            objectTree = HTree.load(recordManager, recid);
        } else {
            objectTree = HTree.createInstance(recordManager);
            recordManager.setNamedObject("objectTreeRec", objectTree.getRecid());
            recordManager.commit();
        }
    }

    public void storeEmIndexContainer(String key, EmIndexContainer emIndexContainer) throws IOException {
        objectTree.put(key, emIndexContainer);
        recordManager.commit();
    }

    public EmIndexContainer retrieveEmIndexContainer(String key) throws IOException {
        return (EmIndexContainer) objectTree.get(key);
    }

    public void closeRecordManager() throws IOException {
        recordManager.close();
    }
}