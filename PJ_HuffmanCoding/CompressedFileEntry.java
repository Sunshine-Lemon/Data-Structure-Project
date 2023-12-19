package PJ_HuffmanCoding;

import java.io.Serializable;

//存储被压缩的文件的相关信息,用于后续还原文件夹
public class CompressedFileEntry implements Serializable {
    private String fileName;
    private byte[] compressedData;
    private String relativePath;

    public CompressedFileEntry(String fileName, byte[] compressedData, String relativePath) {
        this.fileName = fileName;
        this.compressedData = compressedData;
        this.relativePath = relativePath;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getCompressedData() {
        return compressedData;
    }

    public String getRelativePath(){
        return relativePath;
    }
}
