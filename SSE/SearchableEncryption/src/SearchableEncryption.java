import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SearchableEncryption {
    private static String nonce = "c59bcf35";
    private static String STREAM_CIPHER_KEY = "thiskeyisverybad";
    private static String IV = "This is seed 16B";


    /**
     * 构建索引，即加密文件
     * @param encrypted_key    主加密密钥
     * @param ki_key           关键字密钥
     * @param plain_dir        待加密的文件目录
     * @param encrypted_dir    加密文件的输出目录
     * @throws IOException
     */
    public static void encryptFile(String encrypted_key, String ki_key, String plain_dir, String encrypted_dir) throws IOException {
        FileUtil.deleteFiles( encrypted_dir + "/encrypted_index/");
        FileUtil.deleteFiles( encrypted_dir + "/encrypted_data/");

        File[] files = FileUtil.getDirFiles(plain_dir + "/");
        for (File file : files) {
            String filePath = file.toString();
            String fileName = file.getName();
            String content = FileUtil.read(filePath, "UTF-8");

            //构建加密的索引
            List<String> words = FileUtil.getWords(content);
            int count = 0;
            for (String word : words) {
                String counter = HexUtil.ljust(24, count, "0");

                //Si = Stream Cipher
                byte[] streamCipher = AESUtil.generateStreamCipher(STREAM_CIPHER_KEY, nonce, counter, IV);
                String Si = HexUtil.byteArray2HexStr(streamCipher);
                String str = word;
                for (int i = 0; i < 32 - word.length(); i++) {
                    str = str + ".";
                }

                // E(Wi)
                byte[] Ewibyte = AESUtil.aesEncrypt(encrypted_key, str, IV);
                String Ewi = HexUtil.byteArray2HexStr(Ewibyte);

                //F_ki(Si)
                byte[] FiSibyte = AESUtil.aesEncrypt(ki_key, streamCipher, IV);
                String FiSi = HexUtil.byteArray2HexStr(FiSibyte);

                // Ti = Si || F_ki(Si)
                String Ti = Si + FiSi;

                //Ciphertext = E(Wi) ^ (Si || F_ki(Si))
                char[] result = HexUtil.XOR(Ewi, Ti);

                String writePath = encrypted_dir + "/encrypted_index/" + fileName;
                FileUtil.write(new String(result), writePath, "UTF-8");
                count++;
            }

            byte[] encetpted_file = AESUtil.aesEncryptWithPadding(encrypted_key, content, IV);
            String encetpted_file_hex = HexUtil.byteArray2HexStr(encetpted_file);
            String writeEncryptedFilePath = encrypted_dir + "/encrypted_data/" + fileName;
            FileUtil.write(encetpted_file_hex, writeEncryptedFilePath, "UTF-8");
        }

    }


    /**
     * 生成关键字的检索陷门
     * @param encrypted_key      主加密密钥
     * @param ki_key             关键字密钥
     * @param keyword            待检索的关键字
     * @param trapdoor_dir       陷门输出目录
     * @throws Exception
     */
    public static void genTrapdoor(String encrypted_key, String ki_key, String keyword, String trapdoor_dir) throws Exception{
        String str = keyword;
        for (int i = 0; i < 32 - keyword.length(); i++) {
            str = str + ".";
        }

        // E(Wi)
        byte[] cipherKeywordbyte = AESUtil.aesEncrypt(encrypted_key, str, IV);
        String cipher2Search = HexUtil.byteArray2HexStr(cipherKeywordbyte);

        String path = trapdoor_dir + "/trapdoor";
        PrintWriter writer = new PrintWriter(path);
        writer.println(cipher2Search);
        writer.println(ki_key);
        writer.close();
    }


    /**
     * 加密检索功能
     * @param trapdoor_dir      关键字陷门
     * @param encrypted_dir     加密文件的目录
     * @return                  检索结果，文件名列表
     * @throws Exception
     */
    public static List<String> searchFile(String trapdoor_dir, String encrypted_dir, String encrypted_search_result_dir) throws Exception {
        String trapdoor_path = trapdoor_dir + "/trapdoor";

        BufferedReader reader = new BufferedReader(new FileReader(trapdoor_path));
        String K = reader.readLine();
        String Ki = reader.readLine();
        reader.close();

        boolean flag;
        File[] files = FileUtil.getDirFiles(encrypted_dir + "/encrypted_index/");

        List<String> searchResult = new ArrayList<>();

        for (File file : files) {
            flag = false;
            String filePath = file.toString();
            String fileName = file.getName();
            String content = FileUtil.read(filePath, "UTF-8");
            String[] encWords = FileUtil.getEncWords(content);
            for (String encWord : encWords) {
                char[] TiChar = HexUtil.XOR(K, encWord);
                String TiStr = new String(TiChar);
                String[] Ti = new String[2];
                Ti[0] = TiStr.substring(0, TiStr.length() / 2);
                Ti[1] = TiStr.substring(TiStr.length() / 2);
                byte[] ti0 = AESUtil.aesEncrypt(Ki, HexUtil.hexStr2ByteArray(Ti[0]), IV);
                String magic = HexUtil.byteArray2HexStr(ti0).toLowerCase();
                if (magic.equals(Ti[1])) {
                    flag = true;
                }
            }
            if (flag){
                searchResult.add(fileName);
            }
        }

        for (String fileName : searchResult){
            String srcFilePath = encrypted_dir + "/encrypted_data/" + fileName;
            String objFilePath = encrypted_search_result_dir + "/" + fileName;

            Files.copy(Path.of(srcFilePath), Path.of(objFilePath));
        }

        return searchResult;
    }


    /**
     * 解密检索结果
     * @param encrypted_key                  加密（解密）密钥
     * @param plainSearchResultDir           存储解密后的文件目录
     * @param encryptedSearchResultDir       服务器返回的加密检索结果
     * @param searchResult                   检索结果（文件名）
     * @throws Exception
     */
    public static void decryptSearchResult(String encrypted_key, String plainSearchResultDir, String encryptedSearchResultDir, List<String> searchResult) throws Exception{
        for (String fileName : searchResult){
            String encryptedFilePath = encryptedSearchResultDir + "/" + fileName;
            String encrypted_content = FileUtil.read(encryptedFilePath, "UTF-8");
            byte[] encrypted_content_bytes = HexUtil.hexStr2ByteArray(encrypted_content);
            byte[] plain_byte = AESUtil.aesDecryptedWithPadding(encrypted_key, encrypted_content_bytes, IV);
            String plain = new String(plain_byte);

            String writePlainFilePath = plainSearchResultDir + "/" + fileName;
            PrintWriter pw = new PrintWriter(writePlainFilePath);
            pw.print(plain);
            pw.close();

//            FileUtil.write(plain, writePlainFilePath, "UTF-8");
        }
    }


    /**
     * 解密检索结果
     * @param encrypted_key                    加密（解密）密钥
     * @param plainSearchResultDir             存储解密后的文件目录
     * @param encryptedSearchResultDir         服务器返回的加密检索结果
     * @throws Exception
     */
    public static void decryptSearchResult(String encrypted_key, String plainSearchResultDir, String encryptedSearchResultDir) throws Exception{
        File[] files = FileUtil.getDirFiles(encryptedSearchResultDir + "/");

        for (File file : files){
            String filePath = file.toString();
            String fileName = file.getName();

            String encrypted_content = FileUtil.read(filePath, "UTF-8");
            byte[] encrypted_content_bytes = HexUtil.hexStr2ByteArray(encrypted_content);
            byte[] plain_byte = AESUtil.aesDecryptedWithPadding(encrypted_key, encrypted_content_bytes, IV);
            String plain = new String(plain_byte);

            String writePlainFilePath = plainSearchResultDir + "/" + fileName;
            PrintWriter pw = new PrintWriter(writePlainFilePath);
            pw.print(plain);
            pw.close();
        }
    }
}

