package demo;

import co.junwei.cpabe.Cpabe;

public class cpabeEnc {
	final static boolean DEBUG = true;

	static String dir = "tmp/cpabe";
	static String pubfile = dir + "/pub_key.ser";
	static String encfile = dir + "/enc_AESkey.ser";

	public static byte[] cpabeEncAPI(String AESkey,String[] KeyPolicy) throws Exception {

		int KeyPolicyLength = KeyPolicy.length;
		String policy = array2Str(KeyPolicy);
		policy = policy + " " + KeyPolicyLength + "of" + KeyPolicyLength;

		Cpabe test = new Cpabe();
//		println("//start to setup");
//		println(pubfile);
//		test.setup(pubfile, mskfile);
//		println("//end to setup");

		println("//start to enc");
		byte[] Enckey = test.enc(pubfile, policy, AESkey, encfile);
		println("//end to enc");

		return Enckey;
	}

	/* connect element of array with blank */
	public static String array2Str(String[] arr) {
		int len = arr.length;
		String str = arr[0];

		for (int i = 1; i < len; i++) {
			str += " ";
			str += arr[i];
		}

		return str;
	}

	private static void println(Object o) {
		if (DEBUG)
			System.out.println(o);
	}
}
