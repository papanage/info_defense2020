package ru.nsu.block;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Vector;

import static java.lang.Long.max;


/**
 * Rc5 block-encoding
 * java implementation
 * @author Lusnikov Vasily
 * FIT NSU 2020
 * I use description of https://ru.wikipedia.org/wiki/RC5
 * can code no more, than int
 */
public class Rc5 {

    /**
     * length of key
     */
    final int  KEY_LENGTH = 16;

    /**
     * half of block size
     * usually equals machine word length
     * for simply I use 32
     */
    static final int BLOCK_HALF = 32;

    /**
     * modulo of Gayla field
     */
    final long MOD = pow(2, BLOCK_HALF);

    /**
     * counts of rounds
     */
    final int ROUND_COUNT = 12;

    /**
     * magic constant - depends of BLOCK_HALF
     * P = Odd((f-1)*2^BLOCK_HALF))
     * Q = Odd((e-2)*2^BLOCK_HALF))
     * f - gold section
     * e - Euler number
     */
    final Long P = 0xB7E15163L;
    final Long Q = 0x9E3779B9L;

    /**
     *  key
     */
    public byte[] key = new byte[KEY_LENGTH];

    /**
     * mass with part of key
     * length of part is no more than long size
     * use for work in Gayla field
     */
    Vector<byte[]>  l_mass;

    /**
     * special array for constants
     */
    Long[] s = new Long[2*(ROUND_COUNT+1)];

    /**
     * data for encoding
     * actually we work with integer,
     * but packing in little 4 byte of long
     */
    long A = 0xFFFFFF, B = -1432423;


    public Rc5() {
        start();
    }
    /**
     * put big key in mass by part of length = BLOCK_HALF/8
     */
    private void keyToWords() {
        l_mass = new Vector<>();
        for (int i = 0; i < KEY_LENGTH/(BLOCK_HALF/8); i++) {
            l_mass.add(Arrays.copyOfRange(key,i*BLOCK_HALF/8, (i+1)*BLOCK_HALF/8));
        }
        //System.out.println(l_mass.size());
    }

    /**
     * generating table of extending key
     */
    private void extendKey() {
        s[0] = P % MOD ;
        for (int i = 1; i < 2*(ROUND_COUNT+1); i++) {
            s[i] = (s[i-1] + Q)  % MOD;
        }
    }

    private void shake() {
        long G = 0, H = 0;
        int i = 0, j = 0;
        for (int n = 0; n < max(l_mass.size()*2, 6*(ROUND_COUNT+1)); n++) {
            G = ByteUtils.cycleShift(s[i] + (G + H) % MOD, 3) % MOD;
            H = ByteUtils.cycleShift(ByteUtils.bytesToLong(l_mass.get(j)) % MOD + (G + H) % MOD ,  G + H) % MOD;
            i = (i + 1) % 2*(ROUND_COUNT + 1);
            j = (j + 1) % l_mass.size();
        }
    }

    /**
     * round encoding
     */
    private void code() {
      //  System.out.println("start: " +A + " " + B);
        A = (A + s[0]) % MOD;
        B = (B + s[1]) % MOD;
        for (int i = 0; i < ROUND_COUNT; i++) {
            A = (ByteUtils.cycleShift(A^B, B) % MOD + s[2*i]) % MOD;
            B = (ByteUtils.cycleShift(B^A, A) % MOD + s[2*i + 1]) % MOD;
        }
    }

    /**
     * decoding
     */
    private void decode() {
        for (int i = ROUND_COUNT - 1; i  >= 0; i--) {
            B = (ByteUtils.cycleShift((B - s[2*i + 1] + MOD) % MOD, -A) % MOD) ^ A;
            A = (ByteUtils.cycleShift((A - s[2*i] + MOD) % MOD, -B) % MOD) ^ B;
        }
        A = (A - s[0] + MOD) % MOD;
        B = (B - s[1] + MOD) % MOD;
        //System.out.println("res: " + A + " " + B);
    }

    /**
     * some utility func for power finding
     * @param x
     * @param y
     * @return
     */
    private long pow(long x, long y) {
        long res = 1;
        while (y-- != 0) {
            res *= x;
        }
        return res;
    }

    /**
     * start rc5
     * better run on 64x
     */
    private void start() {
        keyToWords();
        extendKey();
        shake();
    }

    /**
     * code file
     * @param path path to file
     * @throws IOException if some gone wrong
     */
    public void codeFile(String path) throws IOException {
        InputStream inputStream = Rc5.class.getResourceAsStream(path);
        byte[] file = inputStream.readAllBytes();
        inputStream.close();
        Vector<Long> code = new Vector<>();
        for (int i = 0; i < file.length; i += 8) {
            setData(i, file);

            code();
            code.add(A);
            code.add(B);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(
            new File("code_out.txt"));
        for (Long l: code) {
            fileOutputStream.write(ByteUtils.longToBytes(l));
        }
        fileOutputStream.close();
    }

    /**
     * code file
     * @param path path to file
     * @throws IOException if some gone wrong
     */
    public long hashFile(String path) throws IOException {
        InputStream inputStream = Rc5.class.getResourceAsStream(path);

        byte[] file = inputStream.readAllBytes();
        System.out.println(file.length);
        inputStream.close();
        long enc1 = 0, enc2 = 0;
        long res1 = 0, res2 = 0;
        for (int i = 0; i < file.length; i += 8) {
            setData(i, file);
            if (i > 0) {
                res1 = enc1 ^ A;
                res2 = enc2 ^ B;
            }
            start();
            code();
            System.out.println(A + " " + B);
            enc1 = A;
            enc2 = B;
        }
        System.out.println(res1 + " " + res2);
        return (res1 << 32) | res2;
    }


    private void setData(int i, byte[] file) {
        if (i + 4 > file.length) {
            A = ByteUtils.bytesToLong(Arrays.copyOfRange(file, i, file.length));
            B = 0;
        }
        else if (i + 8 > file.length) {
            A = ByteUtils.bytesToLong(Arrays.copyOfRange(file, i, i+4));
            B = ByteUtils.bytesToLong(Arrays.copyOfRange(file, i+4, file.length));
        }
        else {
            A = ByteUtils.bytesToLong(Arrays.copyOfRange(file, i, i + 4));
            B = ByteUtils.bytesToLong(Arrays.copyOfRange(file, i + 4, i + 8));
        }
    }

    /**
     * decoding file
     * @param path path to file which need decode
     * @throws IOException if some gone wrong
     */
    public void decodeFile(String path) throws IOException {
        FileInputStream inputStream = new FileInputStream(
            new File(path));
        byte[] file = inputStream.readAllBytes();
        //System.out.println("length = " + file.length);
        Vector<Long> code = new Vector<>();
        for (int i = 0; i < file.length; i += 8) {
            setData(i, file);

            decode();
            code.add(A);
            code.add(B);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(
            new File("decode_out.txt"));
        Vector<Byte> file2= new Vector<>();
        byte[] v;
        for (Long l: code) {
            v = ByteUtils.longToBytes(l);
            for (int i = 0; i < 4; i++) {
                if (v[i] == 0) continue;
                file2.add(v[i]);
            }
        }
        for (Byte b : file2) {
            fileOutputStream.write(b);
        }

        fileOutputStream.close();
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Rc5 rc5 = new Rc5();
        Arrays.fill(rc5.key, (byte) 1);
        //rc5.key[34] = 3;
        //System.out.println(rc5.MOD);
        rc5.codeFile("/test.txt");
        System.out.println(" hash = " + rc5.hashFile("/test.txt"));
        rc5.decodeFile("code_out.txt");
    }

}
