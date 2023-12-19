package PJ_HuffmanCoding;

import java.io.*;

//按位读取数据的输入流
public class BitInputStream implements Closeable{
    private InputStream input;
    private int currentByte;
    private int numBitsFilled;

    public BitInputStream(InputStream in) {
        if (in == null) {
            throw new NullPointerException("Argument is null");
        }
        input = in;
        currentByte = 0;
        numBitsFilled = 0;
    }

    public int read() throws IOException {
        if (currentByte == -1) {
            return -1;
        }
        if (numBitsFilled == 0) {
            currentByte = input.read();
            if (currentByte == -1) {
                return -1;
            }
            numBitsFilled = 8;
        }
        numBitsFilled--;
        return (currentByte >>> numBitsFilled) & 1;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}