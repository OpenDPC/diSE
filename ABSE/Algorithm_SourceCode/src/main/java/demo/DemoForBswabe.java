
package demo;
import index.Index;
import it.unisa.dia.gas.jpbc.Element;
import scheme.Bswabe;
import scheme.BswabeCph;
import scheme.BswabeMsk;
import scheme.BswabePrv;
import scheme.BswabePub;
import scheme.BswabeToken;

public class DemoForBswabe {
	final static boolean DEBUG = true;
	/*final static String inputfile = "F://dataEclipse/keyword.txt";
	final static String encfile = "F://dataEclipse/cipher.txt";
	final static String decfile = "F://dataEclipse/result.txt";//�������������ļ���id����TRUE/false
*/

	//universal attribute set, any attribute is in u.
	final static String []u={"ECNU","teacher", "doctor","master","bachelor","2016","2015","2014"};
	//attributes of the user
	final static String []attrs = {"ECNU","teacher"};
	//attributes of the policy
	final static String []policy = {"teacher","ECNU"};
	final static String []file = {"E:\\DeskDocument\\Attribute-Based-Searchable-Encryption\\files\\1.txt","E:\\DeskDocument\\Attribute-Based-Searchable-Encryption\\files\\2.txt"};//�����ؼ��ֵ������ļ�
	final static String words = "4";
	final static Index index = new Index(words,file);
	final static String word = "4";


	public static void main(String[] args) throws Exception {
		BswabePub pub = new BswabePub(); //A public key
		BswabeMsk msk = new BswabeMsk();//A master secret key
		BswabePrv prv;//A private key
		BswabeToken token;//token
		BswabeCph cph;//public BswabePolicy p
		boolean result = false;

		println("//demo for bswabe: start to setup");

		Bswabe.setup(u, pub, msk);

//		for(int i=0;i<u.length;i++){
//			System.out.println(u[i]);
//		}
//		System.out.println(pub);
//		System.out.println(msk);
		println("//demo for bswabe: end to setup");

		println("\n//demo for bswabe: start to enc");
		cph = Bswabe.enc(u,pub, policy, index);
		System.out.println(index);
		println("//demo for bswabe: end to enc");

		println("\n//demo for bswabe: start to keygen");
		prv = Bswabe.keygen(u,pub, msk, attrs);
//		System.out.println(prv);
		println("//demo for bswabe: end to keygen");

		println("\n//demo for bswabe: start to tokengen");
		token = Bswabe.tokgen(prv,pub,word);
//		System.out.println(token);
		println("\n//demo for bswabe: end to tokengen");

		println("\n//demo for bswabe: start to search");
		result = Bswabe.search(pub, token, cph);
//		System.out.println(result);
		println("//demo for bswabe: end to dec");

		if (result){
			String []fileReturned = index.file;
			for(int i=0;i<fileReturned.length;i++)
				System.out.print(fileReturned[i]+" ");
			System.out.println();
		}
		else
			System.err.println("There are no results!");
	}

	private static void println(Object o) {
		if (DEBUG)
			System.out.println(o);
	}
}
