
package demo;

import index.Index;
import scheme.Bswabe;
import scheme.BswabeCph;
import scheme.BswabeMsk;
import scheme.BswabePrv;
import scheme.BswabePub;
import scheme.BswabeToken;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class DemoForBswabe2 {
    final static boolean DEBUG = true;
	/*final static String inputfile = "F://dataEclipse/keyword.txt";
	final static String encfile = "F://dataEclipse/cipher.txt";
	final static String decfile = "F://dataEclipse/result.txt";//输出搜索结果：文件的id或者TRUE/false
*/

    final static String[] u = {"ECNU", "teacher", "doctor", "master", "bachelor", "2016", "2015", "2014"};
    final static String[] attrs = {"ECNU", "teacher"};//用户上传
    final static String[] policy = {"ECNU", "teacher"};
    final static String[] file = {"E:\\DeskDocument\\Attribute-Based-Searchable-Encryption\\files\\1.txt", "E:\\DeskDocument\\Attribute-Based-Searchable-Encryption\\files\\2.txt"};//包含关键字的所有文件
    final static String FilesIndex = "4";
    final static String word = "4";

    static int flag = 0;


    public static void main(String[] args) throws Exception {
        BswabePub pub = new BswabePub(); //A public key
        BswabeMsk msk = new BswabeMsk();//A master secret key
        BswabePrv prv;//A private key
        BswabeToken token;//token
        BswabeCph cph = new BswabeCph();//public BswabePolicy p
        boolean result = false;



            Index index = new Index(FilesIndex, file);

            Bswabe.setup(u, pub, msk);// 生成公钥、主密钥；传入所有属性、公钥、主密钥
            cph = Bswabe.enc(u, pub, policy, index);//开始加密；传入所有属性，公钥，访问策略，要加密的文件索引
            System.out.println(cph.w1);
//            cph.u_gate="7640665919159006931758008393909237162650265911574196118951726030249729205580626040522877146633479041022225673252604242537629934605103479901871665891094058,8700717315646702314706095944820307737100354036984799267977699756484606860046136156987454669664574365109954445744750349199884715317110504849682306439902428,0";

            prv = Bswabe.keygen(u, pub, msk, attrs);//生成私钥；传入所有属性，公钥，主密钥，搜索用户属性，这里只用了u的length
            token = Bswabe.tokgen(prv, pub, word);//生成 token， 传入私钥，公钥，要搜索的关键字

            result = Bswabe.search(pub, token, cph);//把公钥、token、加密索引发送给服务器，让服务器进行匹配

            if (result) {//搜索成功
                flag = 1;
                println("have found!");
                String[] fileReturned = index.file;
                for (int i = 0; i < fileReturned.length; i++) {
                    println(fileReturned[i] + " ");
                }
            } else {
                println("There are no results!");
            }

    }

    private static void println(Object o) {
        if (DEBUG)
            System.out.println(o);
    }
}
