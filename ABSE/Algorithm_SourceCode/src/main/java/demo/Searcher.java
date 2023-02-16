
package demo;
import scheme.*;
import java.io.*;

public class Searcher {
    final static String[] u = {"ECNU", "teacher", "doctor", "master", "bachelor", "2016", "2015", "2014"};
    final static String[] attrs = {"ECNU", "teacher"};//用户上传
    final static String[] words = {"123"};

    public static void main(String[] args) throws Exception {
        BswabePub pub = new BswabePub(); //A public key
        BswabeMsk msk = new BswabeMsk();//A master secret key
        BswabePrv prv;//A private key
        BswabeToken token;//token

        FileInputStream fileIn = new FileInputStream("./tmp/SearchableEncryption/Pub.ser");
        ObjectInputStream pubin = new ObjectInputStream(fileIn);
        pub = (BswabePub) pubin.readObject();
        pubin.close();

        FileInputStream fileIn2 = new FileInputStream("./tmp/SearchableEncryption/Msk.ser");
        ObjectInputStream mskin = new ObjectInputStream(fileIn2);
        msk = (BswabeMsk) mskin.readObject();
        mskin.close();

        prv = Bswabe.keygen(u, pub, msk, attrs);//生成私钥；传入所有属性，公钥，主密钥，搜索用户属性，这里只用了u的length
        token = Bswabe.tokgen(prv, pub, words[0]);//生成 token， 传入私钥，公钥，要搜索的关键字

        FileOutputStream fileOut = new FileOutputStream("./tmp/SearchableEncryption/UserToken.ser");
        ObjectOutputStream TokenOut = new ObjectOutputStream(fileOut);
        TokenOut.writeObject(token);
        TokenOut.flush();
        TokenOut.close();
        System.out.println("UserToken.ser over");

        //验证
//        FileInputStream fileIn = new FileInputStream("./tmp/UserToken.ser");
//        ObjectInputStream in = new ObjectInputStream(fileIn);
//        BswabeToken o = (BswabeToken) in.readObject();
//        in.close();
//        System.out.println(o.tok1);


    }

}
