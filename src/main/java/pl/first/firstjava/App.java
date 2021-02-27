package pl.first.firstjava;


import java.util.Scanner;

public class App {

    public static void main(final String[] args) {
        String keystring = "sI2%tplsG10Eym>msO02Q22L5Ikfw_3@";
        char[] Key = keystring.toCharArray();
        Scanner scan = new Scanner(System.in);
        System.out.println("Wprowadz tekst do zaszyfrowania: ");
        String text = scan.nextLine();
        while(text.length()%16!=0) {
            text = text + " ";
        }
        char[] Text = text.toCharArray();
        Key key = new Key(Key, 32);
        Encryptor encryptor=new Encryptor();
        encryptor.setKey(key);
        Text=encryptor.encryption(Text, text.length());
        System.out.print("Zaszyfrowany tekst: ");
        for ( int i =0;i<text.length();i++){
            System.out.print(Text[i]);
        }
        System.out.println();
        Text=encryptor.decryption(Text, text.length());
        System.out.print("Odszyfrowany tekst: ");
        for ( int i =0;i<text.length();i++){
            System.out.print(Text[i]);
        }
        System.out.println();
    }
}
