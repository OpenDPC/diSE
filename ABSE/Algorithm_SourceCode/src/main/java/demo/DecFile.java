package demo;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;
import java.util.Base64.Decoder;

import static demo.cpabeDec.cpabeDecAPI;

public class DecFile {

    final static String[] attrs = {"ECNU", "teacher"};
    final static String ivParameter = "AAAABBBBCCCCDDDD";
    private static String DirName = "UploadFiles";

    public static void main(String[] args) {

        File dir = new File(DirName);
        String[] children = dir.list();

        for (int i = 0; i < children.length; i++) {
            try {
                String PlainFile = "DownloadFiles\\" + children[i];
                String encfile = DirName + "\\" + children[i];
                decryptfile(encfile, ivParameter, PlainFile, attrs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void decryptfile(String encfile, String ivParameterm, String PlainFile, String[] attrs) throws Exception {
        // ciphertext.txt
        File file = new File(encfile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String key = br.readLine();
//        System.err.println(key);
        String Data = br.readLine();
        System.out.println("=====CipherText:=====\n" + Data);

        //解密AES密钥
        byte[] Basekey = key.getBytes();
        Decoder decoder1 = Base64.getDecoder();
        byte[] Enckey = decoder1.decode(Basekey);
        byte[] raw = cpabeDecAPI(attrs,Enckey);
//        System.out.println(new String(raw));

        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(ivParameterm.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

        // PlainFile
        byte[] data = Data.getBytes();
        Decoder decoder2 = Base64.getDecoder();
        byte[] bytIn = decoder2.decode(data);
        byte[] bytOut = cipher.doFinal(bytIn);

        File outfile = new File(PlainFile);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outfile));
        System.out.println("====PlainText:====\n" + new String(bytOut));
        bos.write(bytOut);
        bos.close();
    }
}