package ru.nsu.block;

import java.util.Arrays;
import java.util.Vector;

import static java.lang.Long.max;


/**
 * Rc5 block-chiefr
 * java implementation
 * @author Lusnikov Vasily
 * FIT NSU 2020
 * I use description of https://ru.wikipedia.org/wiki/RC5
 */
public class Rc5 {

    /**
     * length of key
     */
    final int  KEY_LENGTH = 128;

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
    byte[] key = new byte[KEY_LENGTH];

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

    long A = 324324, B = -1432423;

    private void keyToWords() {
        l_mass = new Vector<>();
        for (int i = 0; i < KEY_LENGTH/(BLOCK_HALF/8); i++) {
            l_mass.add(Arrays.copyOfRange(key,i*BLOCK_HALF/8, (i+1)*BLOCK_HALF/8));
        }

    }

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
            G = ByteUtils.cycleShift(s[i] + (G + H) % MOD, 3);
            H = ByteUtils.cycleShift(ByteUtils.bytesToLong(l_mass.get(i)) + (G + H) % MOD ,  G + H);
            i = (i + 1) % 2*(ROUND_COUNT + 1);
            j = (j + 1) % l_mass.size();
        }
    }

    private void code() {
        System.out.println("start: " +A + " " + B);
        A = (A + s[0]) % MOD;
        B = (B + s[1]) % MOD;
        for (int i = 0; i < ROUND_COUNT; i++) {
            A = (ByteUtils.cycleShift(A^B, B) % MOD + s[2*i]) % MOD;
            B = (ByteUtils.cycleShift(B^A, A) % MOD + s[2*i + 1]) % MOD;
        }
    }
    private void decode() {
        for (int i = ROUND_COUNT - 1; i  >= 0; i--) {
            B = (ByteUtils.cycleShift((B - s[2*i + 1] + MOD) % MOD, -A) % MOD) ^ A;
            A = (ByteUtils.cycleShift((A - s[2*i] + MOD) % MOD, -B) % MOD) ^ B;
        }
        A = (A - s[0]) % MOD;
        B = (B - s[1]) % MOD;
        System.out.println("res: " + A + " " + B);
    }
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
    public void start() {
        keyToWords();
        extendKey();
        shake();
        code();
        decode();

    }

    public static void main(String[] args) {
       // byte[] bc = {0,0,1,1};
       // System.out.println(ByteUtils.bytesToLong(bc));

        Rc5 rc5 = new Rc5();
        Arrays.fill(rc5.key, (byte) 1);
        rc5.key[34] = 3;
        //System.out.println(rc5.MOD);
        rc5.start();
        for (byte[] b : rc5.l_mass) {
            for (byte f : b) {
              //  System.out.print(f);
            }
           // System.out.println();
        }
    }

}
