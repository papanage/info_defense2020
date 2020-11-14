package ru.nsu;

public class AlphabetController {
    String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    int size;

    public AlphabetController(String alphabet) {
        this.alphabet = alphabet;
        size = alphabet.length();
    }

    public Character getAtPos(int pos) throws Exception{
        if (pos > size) {
            throw new Exception();
        }
        else return alphabet.charAt(pos);
    }


    public Integer posOf(char c) throws Exception {
        int pos =  alphabet.indexOf(c);
        if (pos == -1) {
            throw new Exception();
        }
        return pos;
    }
}
