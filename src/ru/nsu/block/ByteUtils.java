package ru.nsu.block;


import java.nio.ByteBuffer;

public class ByteUtils {
    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result <<= (bytes.length);
            result |= (bytes[i] & 0xFF);
        }
       // System.out.println("res = " + result);
        return result;
    }

    public static long cycleShift(long base, long shift) {

        shift = (shift % Rc5.BLOCK_HALF + Rc5.BLOCK_HALF) % Rc5.BLOCK_HALF;
       // System.out.println("start = " + base + " shift = " + shift +  " " + Long.toBinaryString(base).length());
        long temp = base;
       // System.out.println(Long.toBinaryString(base));
        base = base << shift;
        //System.out.println(Long.toBinaryString(base));
        base = base & 0xFFFFFFFFL;
       // System.out.println(Long.toBinaryString(base));
        temp = temp >> (32 -  shift);
       // System.out.println(Long.toBinaryString(temp));
        base = base | temp;
        //System.out.println(Long.toBinaryString(base));
        //System.out.println("res = " + base);
        return base;
    }






    public static void main(String[] args) {
        //cycleShift(cycleShift(4698668300966100165L, 52L), -52L);
       long h =  cycleShift(cycleShift(2545587777L, 2934294345L),-2934294345L) ;
        ///long h2 =  cycleShift(9710L, -2934294345L);
       /// System.out.println(h % 4294967296L);
        //System.out.println(h2 % 4294967296L);
    }
}
