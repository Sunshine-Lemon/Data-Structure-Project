package PJ_HuffmanCoding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class SingleFile {
    private Map<Byte, String> huffmanCodes = new HashMap<>();
    private byte[] decompressedData; 
    private byte[] compressedData;
    private static final int BUFFER_SIZE = 8192; // 缓冲区大小，提高大文件读取效率

    public byte[] getCompressedData(){
        return compressedData;
    }

    
    public byte[] getDecompressedData() {
        return decompressedData;
    }

    public void setCompressedData(byte[] compressData){
        this.compressedData = compressData;
    }
    
    public void setDecompressedData(byte[] decompressedData) {
        this.decompressedData = decompressedData;
    }
    
    //压缩主函数
    public void compressFile(String inputFile, String compressedFile, ByteArrayOutputStream compressedDataOutput) {
        ObjectOutputStream objectOutputStream;
        //根据传递的参数构建输出流
        try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
            if(compressedDataOutput == null){
                objectOutputStream = new ObjectOutputStream(new FileOutputStream(compressedFile));
            }
            else{
                objectOutputStream = new ObjectOutputStream(compressedDataOutput);
            }
            
            // 保存原始文件名以及扩展名
            objectOutputStream.writeObject(new String(inputFile));

            // 读取文件原始内容并计算字符频率
            byte[] data = fileToByteArray(fileInputStream);
            Map<Byte, Long> frequencies = calculateFrequencies(data);

            // 构建哈夫曼树
            HuffmanNode root = buildHuffmanTree(frequencies);

            // 生成哈夫曼编码表
            generateHuffmanCodes(root, "");

            // 保存哈夫曼树到压缩文件
            saveHuffmanTree(root, objectOutputStream);

            // 保存哈夫曼编码表到压缩文件
            saveHuffmanCodes(huffmanCodes, objectOutputStream);

            // 压缩数据并写入压缩文件
            compressData(data, objectOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //解压缩主函数
    public void decompressFile(String compressedFile, String outputFile, ByteArrayInputStream compressedDataInput) {
        ObjectInputStream objectInputStream;
        //根据传递的参数构建输入流
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {

            if(compressedDataInput == null){
                objectInputStream = new ObjectInputStream(new FileInputStream(compressedFile));
            }
            else{
                objectInputStream = new ObjectInputStream(compressedDataInput);
            }
            // 读取原始文件名
            String originalFileName = (String) objectInputStream.readObject();

            // 从压缩文件中读取哈夫曼树
            HuffmanNode decompressedRoot = readHuffmanTree(objectInputStream);

            // 从压缩文件中读取哈夫曼编码表
            huffmanCodes = readHuffmanCodes(objectInputStream);

            // 解压缩文件
            decompressData(objectInputStream, fileOutputStream, decompressedRoot);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //使用缓冲机制，读取原始文件内容
    private byte[] fileToByteArray(FileInputStream fileInputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, BUFFER_SIZE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE]; // 维护缓冲区
        int bytesRead;

        while ((bytesRead = bufferedInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return byteArrayOutputStream.toByteArray();
    }

    //统计字符频率
    private Map<Byte, Long> calculateFrequencies(byte[] data) {
        Map<Byte, Long> frequencies = new HashMap<>();

        for (byte b : data) {
            frequencies.put(b, frequencies.getOrDefault(b, 0L) + 1);
        }

        return frequencies;
    }

    //以频率为权值，构建哈夫曼树
    private HuffmanNode buildHuffmanTree(Map<Byte, Long> frequencies) {
        //使用优先队列来选择节点
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();

        for (Map.Entry<Byte, Long> entry : frequencies.entrySet()) {
            priorityQueue.offer(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            HuffmanNode parent = new HuffmanNode(null, left.getFrequency() + right.getFrequency());
            parent.setLeft(left);
            parent.setRight(right);

            priorityQueue.offer(parent);
        }

        //返回根节点
        return priorityQueue.poll();
    }

    //依据哈夫曼树，生成哈夫曼编码表
    private void generateHuffmanCodes(HuffmanNode root, String code) {
        if (root != null) {
            if (root.getData() != null) {
                huffmanCodes.put(root.getData(), code);
            }

            generateHuffmanCodes(root.getLeft(), code + "0");
            generateHuffmanCodes(root.getRight(), code + "1");
        }
    }

    //保存哈夫曼树到压缩文件
    private void saveHuffmanTree(HuffmanNode root, ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(root);
    }

    //读取已保存的哈夫曼树
    private HuffmanNode readHuffmanTree(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        return (HuffmanNode) objectInputStream.readObject();
    }

    //保存哈夫曼编码表到压缩文件
    private void saveHuffmanCodes(Map<Byte, String> huffmanCodes, ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(huffmanCodes);
    }

    //读取已保存的哈夫曼编码表
    private Map<Byte, String> readHuffmanCodes(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        return (Map<Byte, String>) objectInputStream.readObject();
    }

    //压缩原始内容并写入压缩文件
    private void compressData(byte[] data, ObjectOutputStream objectOutputStream) throws IOException {
        try (BitOutputStream bitOutputStream = new BitOutputStream(objectOutputStream)) {
            for (byte b : data) {
                //获取每个字节的对应哈夫曼编码
                String code = huffmanCodes.get(b);
                
                for (char bit : code.toCharArray()) {
                    //以字符形式写入哈夫曼编码
                    bitOutputStream.write(bit - '0');
                }
            }
        }
    }

    //使用缓冲机制，解压编码内容
    private void decompressData(ObjectInputStream objectInputStream, FileOutputStream fileOutputStream, HuffmanNode root) throws IOException { 

        try (BitInputStream bitInputStream = new BitInputStream(objectInputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            HuffmanNode currentNode = root;

            while (true) {
                int bit = bitInputStream.read();
                if (bit == -1) break; // 达到输入流的末尾，结束

                if (bit == 0) {
                    currentNode = currentNode.getLeft();
                } else if (bit == 1) {
                    currentNode = currentNode.getRight();
                }

                if (currentNode.getLeft() == null && currentNode.getRight() == null) {
                    //找到叶子节点，将解码后的数据写入到输出流中
                    bufferedOutputStream.write(currentNode.getData());
                    currentNode = root; // 重置根节点
                }
            }
            // 在解压缩完成后，设置解压缩后的数据到 SingleFile 对象中
            setDecompressedData(byteArrayOutputStream.toByteArray());
        }
    }
}
