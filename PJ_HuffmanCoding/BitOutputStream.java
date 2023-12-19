package PJ_HuffmanCoding;

import java.io.*;

//按位写入数据的输出流
public class BitOutputStream implements Closeable {
    private OutputStream output;
    private int currentByte;
    private int numBitsFilled;

    public BitOutputStream(OutputStream out) {
        if (out == null) {
            throw new NullPointerException("Argument is null");
        }
        output = out;
        currentByte = 0;
        numBitsFilled = 0;
    }

    public void write(int b) throws IOException {
        if (!(b == 0 || b == 1)) {
            throw new IllegalArgumentException("Argument must be 0 or 1");
        }
        currentByte = (currentByte << 1) | b;
        numBitsFilled++;
        if (numBitsFilled == 8) {
            output.write(currentByte);
            currentByte = 0;
            numBitsFilled = 0;
        }
    }

    @Override
    public void close() throws IOException {
        while (numBitsFilled != 0) {
            write(0);
        }
        output.close();
    }
}
