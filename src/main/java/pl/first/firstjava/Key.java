package pl.first.firstjava;

import java.util.Arrays;

public class Key {
    private final S_Box sbox;
    private final int roundKeyLength = 16;
    private final char[] rcon = {0x01, 0x02, 0x04, 0x08, 0x10,
            0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a};
    private final int keyLength;
    private final char[] key;
    private int expandedKeyLength;
    private char[] expandedKey;
    private int currentLength=0;



    public Key(char[] key, int keyLength ) {
        this.sbox = new S_Box();
        this.key= Arrays.copyOf(key,keyLength);
        this.expandedKey= new char[240];
        expandedKeyLength=0;
        this.keyLength=keyLength;

    }

    public void expandKey() {
        switch (keyLength) {
            case 16 -> expandedKeyLength = 176;
            case 24 -> expandedKeyLength = 208;
            case 32 -> expandedKeyLength = 240;
        }
        expandedKey = new  char[expandedKeyLength];
        for (int i= 0; i<keyLength;i++) {
            expandedKey[i] = key[i];
        }
        currentLength = keyLength;
        if(keyLength/4<=6) {
            for (int i = 1; currentLength < expandedKeyLength; i++) {
                createFirstWord(i);
                for (int j = 0; j < keyLength / 4 - 1; j++) {
                    createWord(i);
                }

            }
        }
        else {
            for (int i = 1; currentLength < expandedKeyLength; i++) {
                createFirstWord(i);
                for (int j = 0; j < 2; j++) {
                    createWord(i);
                }
                create32Word(i);
            }
        }

    }

    public void createFirstWord(int i) {
        char[] temp = new char[4];
        for(int t = 0; t < 4; t++){
            temp[t] = expandedKey[currentLength - 4 + ((t + 1) % 4)];
        }
        for(int t = 0; t < 4; t++){
            temp[t]=sbox.subbytes( temp[t]);
        }

        temp[0] ^= rcon[i - 1];
        for(int t = 0; t < 4; t++){
            expandedKey[currentLength + t] = (char) (temp[t] ^ expandedKey[currentLength - keyLength + t]);
        }
        currentLength += 4;
    }

    public void createWord(int i) {
        char[] temp = new char[4];
        for (int z = currentLength - 4, y = 0; z < currentLength; z++, y++) {
            temp[y] = expandedKey[z];
        }
        for (int t = 0; t < 4; t++) {
            expandedKey[currentLength + t] = (char) (temp[t] ^ expandedKey[currentLength - keyLength + t]);
        }
        currentLength += 4;
    }

    public void create32Word(int i){
        char[] temp = new char[4];
        for(int t = 0; t < 4; t++){
            temp[t] = expandedKey[currentLength - 4 + ((t + 1) % 4)];
        }
        for(int t = 0; t < 4; t++){
            temp[t]=sbox.subbytes( temp[t]);
        }
        for(int t = 0; t < 4; t++){
            expandedKey[currentLength + t] = (char) (temp[t] ^ expandedKey[currentLength - keyLength + t]);
        }
        currentLength += 4;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public char[] getRoundKey(int roundNumber) {
        char[] tab = new char[16];
        for (int i = roundNumber* roundKeyLength, j = 0; i<roundNumber* roundKeyLength +16; i++, j++)  {
            tab[j]=expandedKey[i];
        }
        return tab;
    }



}
