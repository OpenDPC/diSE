package demo;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class test {
    public static void main(String[] args) throws IOException {
        String encfile = "tmp/cpabe/enc_AESkey.ser";
        int i, len;
        InputStream is = new FileInputStream(encfile);
        byte[][] res = new byte[2][];
        byte[] aesBuf, cphBuf;

        /* read aes buf */
        len = 0;
        for (i = 3; i >= 0; i--) {
            System.out.println(is.read());
            len |= is.read() << (i * 8);
        }
        aesBuf = new byte[len];
        is.read(aesBuf);
        /* read cph buf */
        len = 0;
        for (i = 3; i >= 0; i--)
            len |= is.read() << (i * 8);
        cphBuf = new byte[len];

        is.read(cphBuf);

        is.close();

        res[0] = aesBuf;
        res[1] = cphBuf;

        System.out.println(new String(aesBuf));
        System.out.println("=============");
        System.out.println(new String(cphBuf));


    }


}
