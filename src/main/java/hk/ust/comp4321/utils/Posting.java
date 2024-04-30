//package hk.ust.comp4321.utils;
//
//import java.io.Serializable;
//import java.util.Comparator;
//
//public class Posting implements Serializable, Comparator<Posting> {
//    private static final long  serialVersionUID = 1L;
//    public int id;
//    public int freq;
//
//    public Posting(int id, int freq) {
//        this.id = id;
//        this.freq = freq;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getFreq() {
//        return freq;
//    }
//
//    public void setFreq(int freq) {
//        this.freq = freq;
//    }
//
//    public void printAll() {
//        System.out.println("id: " + id + ", freq: " + freq);
//    }
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null || getClass() != obj.getClass())
//            return false;
//        Posting other = (Posting) obj;
//        return id == other.id;
//    }
//    @Override
//    public int compare(Posting p1, Posting p2) {
//        return Integer.compare(p1.getId(), p2.getId());
//    }
//
//
//    public static class IdComparator implements Comparator<Posting>, Serializable {
//        @Override
//        public int compare(Posting p1, Posting p2) {
//            return Integer.compare(p1.getId(), p2.getId());
//        }
//    }
//
//    public static class FreqComparator implements Comparator<Posting>, Serializable{
//        @Override
//        public int compare(Posting p1, Posting p2) {
//            if (p1.getFreq() == p2.getFreq())
//                return Integer.compare(p1.getId(), p2.getId());
//            return Integer.compare(p2.getFreq(), p1.getFreq()); // descending order
//        }
//    }
//}
//
