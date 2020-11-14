package ru.nsu;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Vijiner it is also a stream cipher.
 * It propagates the vernam cipher (for modulo rings )
 * For keys of different lengths
 */
public class Vijiner {

    /**
     * using alphabet controller
     */
    AlphabetController alphabetController;

    /**
     * key
     */
    String key;

    /**
     * command for coding
     */
    public static final String CODE = "code";

    /**
     * command for decoding
     */
    public static final String DECODE = "decode";

    /**
     * command for set new key
     */
    public static final String KEY_SET = "key";

    /**
     * command for end vijiner app
     */
    public static final String END = "end";

    public Vijiner(AlphabetController alphabetController, String key) {
        this.alphabetController = alphabetController;
        this.key = key;
    }


    public void start(InputStream inputStream) throws Exception {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] commands = line.split(" ");
            switch (commands[0]) {
                case CODE:
                   computeCode(commands);
                   break;
                case DECODE:
                    computeDecode(commands);
                    break;
                case KEY_SET:
                    key = commands[1];
                    System.out.println("Key change to " + key);
                    break;
                case END: return;

            }
        }
    }

    /**
     * coding message using key
     * @param mess message for coding
     * @return chiefr res
     */
    public String code(String mess) throws Exception{
        StringBuilder stringBuilder = new StringBuilder();
        int n = 0;
        for (int i = 0; i < mess.length(); i++) {
            try {
                alphabetController.posOf(mess.charAt(i));
            } catch (Exception e) {
                stringBuilder.append(mess.charAt(i));
                continue;
            }
            stringBuilder.append(alphabetController.getAtPos(
               (alphabetController.posOf(mess.charAt(i))
               + alphabetController.posOf(key.charAt(n)))
                   % alphabetController.size
           ));
            n = (n + 1) % key.length();
        }
        return  stringBuilder.toString();
    }

    /**
     * decode mess
     * @param mess mess
     * @return decoding mess
     * @throws Exception if some gome wrong
     */
    public String decode(String mess) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        int n = 0;
        for (int i = 0; i < mess.length(); i++) {
            try {
                alphabetController.posOf(mess.charAt(i));
            } catch (Exception e) {
                stringBuilder.append(mess.charAt(i));
                continue;
            }
            stringBuilder.append(alphabetController.getAtPos(
                (alphabetController.posOf(mess.charAt(i))
                - alphabetController.posOf(key.charAt(n))
                + alphabetController.size)
                    % alphabetController.size
            ));
            n = (n + 1) % key.length();
        }
        return  stringBuilder.toString();
    }


    private void computeCode(String[] commands) throws Exception{
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < commands.length; i++) {
            stringBuilder.append(commands[i]);
            if (i != commands.length - 1) {
                stringBuilder.append(" ");
            }
        }
        System.out.println(code(stringBuilder.toString()));
    }

    private void computeDecode(String[] commands) throws Exception{
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < commands.length; i++) {
            stringBuilder.append(commands[i]);
            if (i != commands.length - 1) {
                stringBuilder.append(" ");
            }
        }
        System.out.println(decode(stringBuilder.toString()));
    }


    /**
     * sample using app
     * start your line with some command: code, decode, key
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        AlphabetController alphabetController = new AlphabetController("абвгдеёжзийклмнопрстуфхцчшщъыьэюя");
        Vijiner vijiner = new Vijiner(alphabetController, "абв");

        String command_example = "code ааа абв \nend";
        vijiner.start(new ByteArrayInputStream(command_example.getBytes(StandardCharsets.UTF_8)));
        //абв авд

        String command_example2 = "decode абв авд \nend";
        vijiner.start(new ByteArrayInputStream(command_example2.getBytes(StandardCharsets.UTF_8)));
        //ааа абв


        String command_example3 = "key фит \ndecode исе аьймсь \nend";
        vijiner.start(new ByteArrayInputStream(command_example3.getBytes(StandardCharsets.UTF_8)));
        //KEy change to фит
        //фит лучший


        vijiner.start(System.in);


    }
}
