package demo;

import co.junwei.cpabe.Cpabe;

public class cpabeDec {
        final static boolean DEBUG = true;

        static String dir = "tmp/cpabe";
        static String pubfile = dir + "/pub_key.ser";
        static String mskfile = dir + "/master_key.ser";
        static String prvfile = dir + "/prv_key.ser";


        public static byte[] cpabeDecAPI(String[] attrs, byte[] Enckey) throws Exception {
		    String attr_str;
		    attr_str = array2Str(attrs);

            Cpabe test = new Cpabe();

            println("//start to keygen");
            test.keygen(pubfile, prvfile, mskfile, attr_str);
            println("//end to keygen");

            println("//start to dec");
            byte[] DecKey = test.dec(pubfile, prvfile, Enckey);
            println("//end to dec");

            return DecKey;

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
