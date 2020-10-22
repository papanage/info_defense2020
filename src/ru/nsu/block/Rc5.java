package ru.nsu.block;

import java.util.Arrays;
import java.util.Vector;
import java.util.stream.Stream;

import static java.lang.Long.max;


public class Rc5 {

    /**
     * length of key
     */
    final int  KEY_LENGTH = 128;

    /**
     * half of block size
     * usually equals machine word length
     */
    final int BLOCK_HALF = 32;
    final long MOD = pow(2, BLOCK_HALF);

    /**
     * counts of rounds
     */
    final int ROUND_COUNT = 12;

    final Long P = 0xB7E15163L;
    final Long Q = 0x9E3779B9L;

    byte[] key = new byte[KEY_LENGTH];
    Vector<byte[]>  l_mass;

    Long[] s = new Long[2*(ROUND_COUNT+1)];

    long A = 4, B = 5;
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
            //System.out.println(s[i]);
        }
    }

    private void shake() {
        long G = 0, H = 0;
        int i = 0, j = 0;
        for (int n = 0; n < max(l_mass.size()*2, 6*(ROUND_COUNT+1)); n++) {
            G = (s[i] + G + H) << 3;
            H = (ByteUtils.bytesToLong(l_mass.get(i)) + G + H ) << (G + H);
            System.out.println(G + " " + H);
            i = (i + 1) % 2*(ROUND_COUNT + 1);
            j = (j + 1) % l_mass.size();
        }
    }

    private void code() {
        //long A = 0, B = 0;
        System.out.println(A + " " + B);
        A = (A + s[0]) % MOD;
        B = (B + s[1]) % MOD;
        for (int i = 1; i < ROUND_COUNT; i++) {
            //System.out.println(A + " " + B);
            System.out.println(" a = " + A + "  b = " + B);
            A = (((A^B) << B) + s[2*i]) % MOD;
            B = (((B^A) << A) + s[2*i + 1]) % MOD;

        }
    }
    private void decode() {
        for (int i = ROUND_COUNT; i  > 0; i--) {
            B = ((B - s[2*i + 1]) >> A) ^ A;
            A = ((A - s[2*i]) >> B) ^ B;
        }
        A = (A - s[0]) % MOD;
        B = (B - s[1]) % MOD;
        System.out.println(A + " " + B);
    }
    private long pow(long x, long y) {
        long res = 1;
        while (y-- != 0) {
            res *= x;
        }
        return res;
    }

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
        System.out.println(rc5.MOD);
        rc5.start();
        for (byte[] b : rc5.l_mass) {
            for (byte f : b) {
              //  System.out.print(f);
            }
           // System.out.println();
        }
    }

}
