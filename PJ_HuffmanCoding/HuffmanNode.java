package PJ_HuffmanCoding;

import java.io.Serializable;

public class HuffmanNode implements Serializable, Comparable<HuffmanNode> {
    private Byte data;
    private long frequency;
    private HuffmanNode left, right;

    //构造方法
    public HuffmanNode(Byte data, long frequency) {
        this.data = data;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        return Long.compare(this.frequency, other.frequency);
    }

    //setter 方法
    public void setLeft(HuffmanNode leftNode){
        left = leftNode;
    }

    public void setRight(HuffmanNode rightNode){
        right = rightNode;
    }

    public void setData(Byte data){
        this.data = data;
    }

    public void setFrequency(long frequency){
        this.frequency = frequency;
    }

    //getter 方法
    public HuffmanNode getLeft(){
        return left;
    }

    public HuffmanNode getRight(){
        return right;
    }

    public long getFrequency(){
        return frequency;
    }

    public Byte getData() {
        return data;
    }
}
