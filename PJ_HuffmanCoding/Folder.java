package PJ_HuffmanCoding;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Folder {

    //压缩文件夹(供外部调用)
    public void compressFolder(String sourceFolder, String compressedFile) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(compressedFile))) {
            File folder = new File(sourceFolder);
            if (!folder.isDirectory()) {
                System.out.println("Source is not a folder!");
                return;
            }

            // 保存文件夹名
            String folderName = folder.getName();
            HuffmanHelper.saveFolderName(folderName, objectOutputStream);

            // 保存文件夹结构的元数据
            List<FileMetadata> folderStructureMetadata = generateFolderStructureMetadata(folder, folder.getAbsolutePath());
            objectOutputStream.writeObject(folderStructureMetadata);

            //保存各个压缩文件
            List<CompressedFileEntry> compressedFiles = compressFilesInFolder(folder, folder.getAbsolutePath());
            objectOutputStream.writeObject(compressedFiles);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //压缩文件夹具体实现
    private List<CompressedFileEntry> compressFilesInFolder(File folder, String rootPath) {
        List<CompressedFileEntry> compressedFiles = new ArrayList<>();
        File[] files = folder.listFiles();

        if (files != null) {
            if (files.length == 0) {
                // 如果文件夹为空，创建虚拟的压缩文件项表示空文件夹
                String relativePath = folder.getAbsolutePath().replace(rootPath, "");
                compressedFiles.add(new CompressedFileEntry(folder.getName() + File.separator, new byte[0], relativePath));
            } else {
                for (File file : files) {
                    if (file.isFile()) {
                        SingleFile singleFile = new SingleFile();
                        ByteArrayOutputStream compressedDataOutput = new ByteArrayOutputStream();

                        String relativePath = file.getAbsolutePath().replace(rootPath, "");
                        singleFile.compressFile(file.getAbsolutePath(), null, compressedDataOutput);
                        byte[] compressedData = compressedDataOutput.toByteArray();

                        //压缩完成之后，将信息添加到列表中
                        compressedFiles.add(new CompressedFileEntry(file.getName(), compressedData, relativePath));
                    } else if (file.isDirectory()) {
                        //子文件夹，递归调用
                        List<CompressedFileEntry> subfolderEntries = compressFilesInFolder(file, rootPath);

                        if (subfolderEntries.isEmpty()) {
                            // 如果子文件夹为空，创建虚拟的压缩文件项表示空文件夹
                            String relativePath = file.getAbsolutePath().replace(rootPath, "");
                            compressedFiles.add(new CompressedFileEntry(file.getName() + File.separator, new byte[0], relativePath));
                        } else {
                            compressedFiles.addAll(subfolderEntries);
                        }
                    }
                }
            }
        }
        return compressedFiles;
    }

    //解压缩文件夹(供外部调用)
    public void decompressFolder(String compressedFile, String destinationFolder) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(compressedFile))) {
            // 读取文件夹名
            String folderName = HuffmanHelper.readFolderName(objectInputStream);

            // 读取文件夹结构的元数据
            List<FileMetadata> folderStructureMetadata = (List<FileMetadata>) objectInputStream.readObject();

            List<CompressedFileEntry> compressedFiles = (List<CompressedFileEntry>) objectInputStream.readObject();
            decompressFilesInFolder(compressedFiles, destinationFolder);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //解压缩文件夹具体实现
    private void decompressFilesInFolder(List<CompressedFileEntry> compressedFiles, String destinationFolder) {
        for (CompressedFileEntry entry : compressedFiles) {
            //从压缩列表读取每个文件的压缩信息
            byte[] compressedData = entry.getCompressedData();
            String relativePath = entry.getRelativePath();

            SingleFile singleFile = new SingleFile();
            ByteArrayInputStream compressedDataInput = new ByteArrayInputStream(compressedData);
            String outputPath = destinationFolder + relativePath;

            if (entry.getFileName().endsWith(File.separator)) {
                // 创建空文件夹
                try {
                    Files.createDirectories(Paths.get(outputPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // 创建中间的文件夹并解压文件，维护结构
                File intermediateFolder = new File(outputPath);
                intermediateFolder.getParentFile().mkdirs();

                singleFile.decompressFile(null, outputPath, compressedDataInput);
            }
        }
    }
    
    //读取文件夹结构的元数据
    public List<FileMetadata> getFolderStructureMetadata(String compressedFile) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(compressedFile))) {
            // 跳过文件夹名的读取
            HuffmanHelper.readFolderName(objectInputStream);

            // 读取文件夹结构的元数据
            return (List<FileMetadata>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //创建维护文件夹结构的元数据
    private List<FileMetadata> generateFolderStructureMetadata(File folder, String rootPath) {
        List<FileMetadata> metadataList = new ArrayList<>();
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                String relativePath = file.getAbsolutePath().replace(rootPath, "");
                if (file.isDirectory()) {
                    List<FileMetadata> children = generateFolderStructureMetadata(file, rootPath);
                    metadataList.add(new FileMetadata(file.getName() + File.separator, relativePath, true, children));
                } else {
                    metadataList.add(new FileMetadata(file.getName(), relativePath, false, null));
                }
            }
        }
        return metadataList;
    }

    //预览压缩包，打印文件夹结构层次
    public void previewFolderStructure(String compressedFile) {
        List<String> folderStructurePreview = new ArrayList<>();

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(compressedFile))) {
            // 读取文件夹名
            HuffmanHelper.readFolderName(objectInputStream);

            // 读取文件夹结构的元数据
            List<FileMetadata> folderStructureMetadata = (List<FileMetadata>) objectInputStream.readObject();

            // 提取文件名和相对路径
            for (FileMetadata metadata : folderStructureMetadata) {
                extractMetadataInfo(metadata, folderStructurePreview, 1);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //树形结构层次
        for (String entry : folderStructurePreview) {
            System.out.println(entry);
        }
    }

    //生成文件夹结构的预览信息
    private void extractMetadataInfo(FileMetadata metadata, List<String> previewList, int level) {
        String fileName = metadata.getName();
        String indentation = getIndentation(level);
        
        //将相关信息添加到预览列表中
        previewList.add(indentation + fileName);
    
        // 递归处理子文件夹
        List<FileMetadata> children = metadata.getChildren();
        if (children != null) {
            for (FileMetadata child : children) {
                extractMetadataInfo(child, previewList, level + 1);
            }
        }
    }
    
    //根据文件夹结构层次生成不同的缩进
    private String getIndentation(int level) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indentation.append("|—  ");
        }
        return indentation.toString();
    }
}
