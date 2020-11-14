package ru.nsu.block;

public class ByteUtils {
    public static byte[] longToBytes(long l) {
        int size = Rc5.getBLOCK_HALF() / Byte.SIZE;
        byte[] result = new byte[size];
        for (int i = size - 1; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (byte aByte : bytes) {
            result <<= Byte.SIZE;
            result |= (aByte & 0xFF);
        }
        return result;
    }

    public static long cycleShift(long base, long shift) {
        long mask = Rc5.getMOD() - 1;
        int mod = Rc5.getBLOCK_HALF();
        shift = (shift % mod + mod) % mod;
        long temp = base;
        base = base << shift;
        base = base & mask;
        temp = temp >> (mod -  shift);
        base = base | temp;
        base = base & 0x7FFFFFFFFFFFFFFFL;
        return base;
    }
}
