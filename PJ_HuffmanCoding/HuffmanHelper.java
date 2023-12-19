package PJ_HuffmanCoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class HuffmanHelper {
    
    //获取某文件的大小
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.length();
    }

    //计算文件夹大小
    public static long calFolderSize(File folder) {
        long size = 0;

        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    size += calFolderSize(file);
                }
            }
        } else {
            size = folder.length();
        }
        return size;
    }

    //计算单个文件的压缩率
    public static String calFileCompressionRatio(String inputFile, String compressedFile) {
        long inputSize = getFileSize(inputFile);
        long compressedSize = getFileSize(compressedFile);
        double ratio = ((double) (compressedSize) / inputSize) * 100;
        String formattedRatio = String.format("%.2f", ratio);
        return formattedRatio;
    }

    //判断某个压缩文件后缀名是否合法(.huff)
    public static boolean checkFormat(String compressedFile){
        return compressedFile.endsWith(".huff");
    }

    //判断某个文件是否在指定文件夹中
    public static boolean isFileExist(String folderPath, String fileName){
        File folder = new File(folderPath);

        // 检查文件夹是否存在
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("指定的文件夹不存在。");
            return false;
        }

        File file = new File(folder, fileName);
        return file.exists();
    }

    //判断某个文件夹是否存在于文件中
    public static boolean isFolderExist(String parentFolder, String targetFolder) {
        // 创建Path对象表示两个文件夹的路径
        Path parentPath = Paths.get(parentFolder);
        Path targetPath = parentPath.resolve(targetFolder);

        // 检查目标文件夹是否存在且为文件夹
        return Files.exists(targetPath) && Files.isDirectory(targetPath);
    }

    //获取原始文件名
    public static String getOriginalFileName(String compressedFileName) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(compressedFileName));
        String originalName = (String) objectInputStream.readObject();
        objectInputStream.close();
        return originalName;
    }

    //保存原始文件夹名
    public static void saveFolderName(String folderName, ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(folderName);
    }

    //读取原始文件夹名
    public static String readFolderName(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        return (String) objectInputStream.readObject();
    }

    //计算文件夹的压缩率
    public static String calFolderCompressionRatio(String sourceFolder, String compressedFile) {
        File folder = new File(sourceFolder);

        long folderSize = calFolderSize(folder);
        long fileSize = getFileSize(compressedFile);

        double ratio = ((double) (fileSize) / folderSize) * 100;
        String formattedRatio = String.format("%.2f", ratio);
        return formattedRatio;
        
    }

    //删除一个文件夹
    public static void deleteFolder(String existedFoder){
        // 创建Path对象
        Path folder = Paths.get(existedFoder);

        try {
            // 使用Files.walk方法遍历文件夹及其所有内容
            Files.walk(folder).sorted((p1, p2) -> -p1.compareTo(p2)).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                        e.printStackTrace();
                    }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
}
