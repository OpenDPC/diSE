
public class HexUtil {
    public static byte[] hexStr2ByteArray(String hex) {
        if (hex == null || hex.trim().equals(""))
            return new byte[0];
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length() / 2; i++) {
            String str = hex.substring(i * 2, (i + 1) * 2);
            bytes[i] = (byte) Integer.parseInt(str, 16);
        }
        return bytes;
    }

    public static String byteArray2HexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int char2Int(char a) {
        if (a >= '0' && a <= '9')
            return a - '0';
        else if (a >= 'a' && a <= 'z')
            return a - 'a' + 10;
        else
            return -1;
    }

    public static int int2char(int a) {
        if (a >= 0 && a <= 9)
            return a + '0';
        else if (a >= 10 && a <= 15)
            return a - 10 + 'a';
        else
            return -1;
    }

    public static char[] XOR(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int len;
        if ((len = a.length()) != b.length())
            return null;
        char[] ach = a.toCharArray();
        char[] bch = b.toCharArray();
        char[] result = new char[len];
        for (int i = 0; i < len; i++) {
            int ai = char2Int(ach[i]);
            int bi = char2Int(bch[i]);
            int ci = ai ^ bi;
            result[i] = (char) int2char(ci);

        }
        return result;
    }

    public static String ljust(int n, int count, String str) {
        String counter = Integer.toHexString(count);
        int len = counter.length();
        for (int i = 0; i < n - len; i++) {
            counter = str + counter;
        }
        return counter;
    }
}
