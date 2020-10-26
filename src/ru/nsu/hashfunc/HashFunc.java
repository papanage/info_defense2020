package ru.nsu.hashfunc;

import ru.nsu.block.Rc5;

import java.util.Arrays;

public class HashFunc {
    public static void main(String[] args) {
        Rc5 rc5= new Rc5();
        Arrays.fill(rc5.key, (byte) 1);
    }
}
