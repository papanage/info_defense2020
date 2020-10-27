package ru.nsu.block;


import java.nio.ByteBuffer;

public class ByteUtils {
    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static byte[] longToBytes(long l) {
        int size = Rc5.BLOCK_HALF / Byte.SIZE;
        byte[] result = new byte[size];
        for (int i = size - 1; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result <<= Byte.SIZE;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static long cycleShift(long base, long shift) {
        int mod = Rc5.BLOCK_HALF;
        shift = (shift % mod + mod) % mod;
     //   System.out.println("start = " + base + " shift = " + shift +  " " + Long.toBinaryString(base).length());
        long temp = base;
      //  System.out.println(Long.toBinaryString(base));
        base = base << shift;
       // System.out.println(Long.toBinaryString(base));
        //base = base & 0xFFFFFFFFL;
        //System.out.println(Long.toBinaryString(base));
        temp = temp >> (mod -  shift);
        //System.out.println(Long.toBinaryString(temp));
        base = base | temp;
       // System.out.println(Long.toBinaryString(base));
       // System.out.println("res = " + base);
        base = base & 0x7FFFFFFFFFFFFFFFL;
        return base;
    }






    public static void main(String[] args) {
        //cycleShift(cycleShift(4698668300966100165L, 52L), -52L);
       long h =  cycleShift(cycleShift(418547787361L, 2934294345L),-2934294345L) ;
        System.out.println(h % 4294967296L);
        ///long h2 =  cycleShift(9710L, -2934294345L);
       /// System.out.println(h % 4294967296L);
        //System.out.println(h2 % 4294967296L);
    }
}
