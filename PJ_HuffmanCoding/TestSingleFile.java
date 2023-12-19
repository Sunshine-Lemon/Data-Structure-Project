package PJ_HuffmanCoding;

import java.io.IOException;
import java.io.File;
import java.util.*;

public class TestSingleFile {
    public static void main(String[] args) throws IOException, ClassNotFoundException{
        System.out.println("\n*********************************************************************************");
        SingleFile singleFile = new SingleFile();

        //指定路径前缀与文件夹路径，与后续文件名拼接即可
        String prefix = "SingleFileTest\\";
        String folderPath = "D:\\Program    Java";
        String outPrefix = "SingleFileTest_out\\";

        System.out.println("原始文件与压缩后的文件的默认路径为：" + folderPath + prefix);
        System.out.println("解压缩后的文件保存路径默认为：" + folderPath + outPrefix);
        System.out.println();
        System.out.println("*********************************************************************************");
        Scanner input = new Scanner(System.in);
        String command;
        String inputFile, compressedFile, decompressedFile;

        System.out.println("请输入命令(zip || unzip || preview || exit):");
        command = input.nextLine();
        while(!command.equals("exit")){
            switch(command){
                case "zip" -> {
                    System.out.println("请输入要压缩的文件名:");
                    inputFile = prefix + input.nextLine();

                    //判断要压缩的文件是否存在
                    while(!HuffmanHelper.isFileExist(folderPath, inputFile)){
                        System.out.println("该文件不存在，请再次输入文件名：");
                        inputFile = prefix + input.nextLine();
                    }

                    System.out.println("请输入压缩后的文件名(默认后缀为.huff):");
                    compressedFile = prefix + input.nextLine();

                    boolean zipFlag = true;
                    //处理文件覆盖问题
                    if(HuffmanHelper.isFileExist(folderPath, compressedFile)){
                        System.out.println("该压缩文件已经存在,是否进行覆盖？[Yes/No]");
                        System.out.println("Yes:继续压缩  No:停止压缩    请输入指令：");
                        String choice = input.nextLine();
                        if(choice.equals("No")){
                            System.out.println("压缩，关闭！");
                            zipFlag = false;
                        }
                        else{
                            //删除已存在的文件
                            File existedFile = new File(compressedFile);
                            existedFile.delete();
                        }
                    }
                    if(zipFlag){
                        System.out.println("压缩，启动！");

                        //计算压缩时间
                        long startTime = System.currentTimeMillis();
                        singleFile.compressFile(inputFile, compressedFile, null);
                        long endTime = System.currentTimeMillis();

                        long elapsedTime = endTime - startTime;
                        String ratio = HuffmanHelper.calFileCompressionRatio(inputFile, compressedFile);
                        System.out.println("压缩完毕！  压缩耗时为：" + elapsedTime + " ms " + "  压缩率：" + ratio + "%");

                        //计算压缩前后大小
                        double inputSize = HuffmanHelper.getFileSize(inputFile) / 1024.0;
                        double compressedSize = HuffmanHelper.getFileSize(compressedFile) / 1024.0;
                        System.out.println("文件初始大小：" + inputSize + " KB" + "    压缩文件大小：" + compressedSize + " KB");
                    }
                    
                }
                case "unzip" -> {
                    System.out.println("请输入要解压的文件名(默认后缀为.huff):");
                    compressedFile = prefix + input.nextLine();

                    //判断压缩文件格式与文件是否存在
                    while(!(HuffmanHelper.checkFormat(compressedFile) && HuffmanHelper.isFileExist(folderPath, compressedFile))){
                        if(!HuffmanHelper.checkFormat(compressedFile)){
                            System.out.println("无法解压,请确保后缀名为.huff,再次输入文件名：");
                        }
                        else{
                            System.out.println("该文件不存在，请再次输入文件名：");
                        }
                        compressedFile = prefix + input.nextLine();
                    }

                    //获取原始文件名
                    String originalName = HuffmanHelper.getOriginalFileName(compressedFile).substring(15);
                    decompressedFile = outPrefix + originalName;
                    System.out.println("解压后，文件将自动还原为：" + originalName);

                    boolean unzipFlag = true;
                    //处理文件覆盖问题
                    if(HuffmanHelper.isFileExist(folderPath, decompressedFile)){
                        System.out.println("该解压缩文件已经存在,是否进行覆盖？[Yes/No]");
                        System.out.println("Yes:继续解压缩  No:停止解压缩    请输入指令：");
                        String choice = input.nextLine();
                        if(choice.equals("No")){
                            System.out.println("解压缩，关闭！");
                            unzipFlag = false;
                        }
                        else{
                            //删除已存在的文件
                            File existedFile = new File(decompressedFile);
                            existedFile.delete();
                        }
                    }
        
                    if(unzipFlag){
                        System.out.println("解压缩，启动！");

                        //计算解压缩时间
                        long startTime = System.currentTimeMillis();
                        singleFile.decompressFile(compressedFile, decompressedFile,null);
                        long endTime = System.currentTimeMillis();

                        long elapsedTime = endTime - startTime;
                        System.out.println("解压缩完毕！  解压缩耗时为：" + elapsedTime + "ms");
                    }
                }
                case "exit" -> {
                    break;
                }
                case "preview" -> {
                    System.out.println("请输入要预览的压缩包名(默认后缀为.huff):");
                    compressedFile = prefix + input.nextLine();

                    //检查压缩包是否存在
                    while(!HuffmanHelper.isFileExist(folderPath, compressedFile)){
                        System.out.println("该文件不存在，请再次输入文件名：");
                        compressedFile = prefix + input.nextLine();
                    }
                    //获取压缩包内原始文件名
                    String originalName = HuffmanHelper.getOriginalFileName(compressedFile).substring(15);

                    System.out.println("该压缩包结构如下：");
                    System.out.println(compressedFile.substring(15));
                    System.out.println("|—  " + originalName);
                }
                default -> {
                    System.out.println("命令错误！");
                }
            }
            System.out.println("\n*********************************************************************************");
            System.out.println("请输入命令(zip || unzip || preview || exit):");
            command = input.nextLine();
        }
        input.close();
    }
}
