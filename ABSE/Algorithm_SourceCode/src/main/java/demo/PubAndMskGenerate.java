package demo;

import scheme.Bswabe;
import scheme.BswabeMsk;
import scheme.BswabePub;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class PubAndMskGenerate {
    final static String []u={"ECNU","teacher", "doctor","master","bachelor","2016","2015","2014"};

    public static void main(String[] args) throws Exception {
        BswabePub pub = new BswabePub(); //A public key
        BswabeMsk msk = new BswabeMsk();//A master secret key

        Bswabe.setup(u, pub, msk);

        FileOutputStream fileOut1 = new FileOutputStream("./tmp/Pub.ser");
        ObjectOutputStream PubOut = new ObjectOutputStream(fileOut1);
        PubOut.writeObject(pub);
        PubOut.flush();
        PubOut.close();
        System.out.println("Pub.ser over");

        FileOutputStream fileOut2 = new FileOutputStream("./tmp/Msk.ser");
        ObjectOutputStream MskOut = new ObjectOutputStream(fileOut2);
        MskOut.writeObject(msk);
        MskOut.flush();
        MskOut.close();
        System.out.println("Msk.ser over");


    }

}
