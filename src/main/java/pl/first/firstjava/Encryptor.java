package pl.first.firstjava;

public class Encryptor {

    private final int rowsNumber = 4;
    private final int columnsNumber = 4;
    private final char[][] dataState = new char[rowsNumber][columnsNumber];
    private final S_Box sbox = new S_Box();
    private Key key;
    private int roundsNumber;


    private final char[][] multipliers = {
            {0x02, 0x03, 0x01, 0x01},
            {0x01, 0x02, 0x03, 0x01},
            {0x01, 0x01, 0x02, 0x03},
            {0x03, 0x01, 0x01, 0x02}
    };


    private final char[][] invmultipliers = {
            {0x0e, 0x0b, 0x0d, 0x09},
            {0x09, 0x0e, 0x0b, 0x0d},
            {0x0d, 0x09, 0x0e, 0x0b},
            {0x0b, 0x0d, 0x09, 0x0e}
    };


    public char[] encryption(char[] data, int dataLength){
        if(dataLength != 0 && dataLength % (columnsNumber * rowsNumber) == 0){
            int blocksNumber = dataLength / (columnsNumber * rowsNumber);
            for(int i = 0; i < blocksNumber; i++){
                for (int column = 0,j=i*16; column < columnsNumber; column++) {
                    for (int row = 0; row < rowsNumber; row++,j++) {
                        dataState[row][column] = data[j];
                    }
                }
                encryptDataState();
                for (int column = 0, j=i*16; column < columnsNumber; column++) {
                    for (int row = 0; row < rowsNumber; row++,j++) {
                        data[j] = dataState[row][column];
                    }
                }
            }
        }
        return data;
    }

    private void encryptDataState(){
        addRoundKey(0);
        for(int i = 1; i <= roundsNumber - 1; i++){
            byteSub();
            shiftRow();
            mixColumn();
            addRoundKey(i);
        }
        byteSub();
        shiftRow();
        addRoundKey(roundsNumber);
    }

    private void addRoundKey(int roundNumber){
        char[] roundKey = key.getRoundKey(roundNumber);
        int i=0;
        for(int column = 0; column < columnsNumber; column++){
            for(int row = 0; row < rowsNumber; row++){
                dataState[row][column]= (char) (dataState[row][column] ^ roundKey[i]);
                i++;
            }
        }
    }

    private void byteSub(){
        for(int row = 0; row < rowsNumber; row++){
            for(int column = 0; column < columnsNumber; column++){
                dataState[row][column]=sbox.subbytes(dataState[row][column]);
            }
        }
    }

    public void shiftRow(){
        for(int row = 1; row < rowsNumber; row++){
            char[] tmp = new char[rowsNumber];
            for (int i=0; i<columnsNumber; i++) {
                tmp[i] = dataState[row][i];
            }
            for(int column = 0; column < rowsNumber; column++){
                dataState[row][column] = tmp[(column+row) % columnsNumber];
            }
        }
    }


    public void mixColumn(){
        for(int column = 0; column < columnsNumber; column++){
            char[] newColumn = new char[rowsNumber];
            for(int row = 0; row < rowsNumber; row++){
                newColumn[row] =
                        (char) (multipleBytes(multipliers[row][0], dataState[0][column]) ^
                                multipleBytes(multipliers[row][1], dataState[1][column]) ^
                                multipleBytes(multipliers[row][2], dataState[2][column]) ^
                                multipleBytes(multipliers[row][3], dataState[3][column]));
            }

            for(int row = 0; row < rowsNumber; row++){
                dataState[row][column] = newColumn[row];
            }
        }
    }
    public char multipleBytes(char b1, char b2){
        char out = 0x00;
        int a=0;
        for(int i = 0; i < 8; i++){
            if((int)(b2 & (0x01 << i))==0){
                a= out ^ xpowtime(i, b1);
                out = (char) (out ^ xpowtime(i, b1));
            }
        }
        return out;
    }

    public char xpowtime(int pow, char b){

        for(int i = 0; i < pow; i++){
            b = xtime(b);
        }
        return b;
    }

    public char xtime(char b){
        int a = b << 1;
        if(a>=256){
            a=a-256;
        }
        char out = (char)(a);
        if((b & 0x80) !=0) {
            int z= out ^ 0x1b;
            if(z>=256){
                z=z-256;
            }
            out = (char)z;
        }
        return out;
    }

    public char[] decryption(char[] data, int dataLength){
        if(dataLength != 0 && dataLength % (columnsNumber * rowsNumber) == 0){
            int blocksNumber = dataLength / (columnsNumber * rowsNumber);
            for(int i = 0; i < blocksNumber; i++){
                for (int column = 0, j = i * 16; column < columnsNumber; column++) {
                    for (int row = 0; row < rowsNumber; row++,j++) {
                        dataState[row][column] = data[j];
                    }
                }
                decryptState();
                for(int column = 0, j=i*16; column < columnsNumber; column++) {
                    for (int row = 0; row < rowsNumber; row++,j++) {
                        data[j] = dataState[row][column];
                    }
                }
            }
        }
        return data;
    }


    public void decryptState(){
        addRoundKey(roundsNumber);
        invShiftRows();
        invSubBytes();
        for(int i = roundsNumber - 1; i > 0; i--){
            addRoundKey(i);
            invMixColumns();
            invShiftRows();
            invSubBytes();
        }

        addRoundKey(0);
    }

    public void invSubBytes(){
        for(int row = 0; row < rowsNumber; row++){
            for(int column = 0; column < columnsNumber; column++){
                dataState[row][column]=sbox.invSubbytes(dataState[row][column]);
            }
        }
    }

    public void invShiftRows(){
        for(int row = 1; row < rowsNumber; row++){
            char[] tmp = new char[columnsNumber];
            for (int i=0; i<columnsNumber; i++) {
                tmp[i] = dataState[row][i];
            }
            if (row % 2 == 0){
                for(int column = 0; column < columnsNumber; column++){
                    dataState[row][column] = tmp[(column+row) % columnsNumber];
                }
            }
            if (row % 2 == 1){
                for(int column = 0; column < columnsNumber; column++){
                    dataState[row][column] = tmp[(column+row+2) % columnsNumber];
                }
            }
        }
    }

    public void invMixColumns(){
        for(int column = 0; column < columnsNumber; column++){
            char[] newColumn = new char[rowsNumber];
            for(int row = 0; row < rowsNumber; row++){
                newColumn[row] =
                        (char) (multipleBytes(invmultipliers[row][0], dataState[0][column]) ^
                                                        multipleBytes(invmultipliers[row][1], dataState[1][column]) ^
                                                        multipleBytes(invmultipliers[row][2], dataState[2][column]) ^
                                                        multipleBytes(invmultipliers[row][3], dataState[3][column]));
            }
            for(int row = 0; row < rowsNumber; row++){
                dataState[row][column] = newColumn[row];
            }
        }
    }


    public void setKey(Key key){
        this.key = key;
        switch (key.getKeyLength()) {
            case 16 -> roundsNumber = 10;
            case 24 -> roundsNumber = 12;
            case 32 -> roundsNumber = 14;
            default -> roundsNumber = 0;
        }
        key.expandKey();
    }
}
