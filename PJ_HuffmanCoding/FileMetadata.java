package PJ_HuffmanCoding;

import java.io.Serializable;
import java.util.List;

//用于维护文件和文件夹的结构信息
public class FileMetadata implements Serializable {
    private String name;
    private String relativePath;
    private boolean isDirectory;
    private List<FileMetadata> children;

    public FileMetadata(String name, String relativePath, boolean isDirectory, List<FileMetadata> children) {
        this.name = name;
        this.relativePath = relativePath;
        this.isDirectory = isDirectory;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public List<FileMetadata> getChildren() {
        return children;
    }
}

