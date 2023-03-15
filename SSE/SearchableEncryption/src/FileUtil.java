import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileUtil {

    public static void write(String content, String path, String charset) throws IOException {
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file, true);
        OutputStreamWriter osw = new OutputStreamWriter(fos, charset);
        osw.append(content);
        osw.close();
    }

    public static String read(String path, String charset) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, charset);
        BufferedReader reader = new BufferedReader(isr);
        String content = "";
        String temp;
        while ((temp = reader.readLine()) != null) {
            content += temp;
        }
        return content;
    }

    public static File[] getDirFiles(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        return files;
    }

    public static List<String> getWords(String essay) {
        String pattern = "(\\w[\\w']*\\w|\\w)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(essay);
        List<String> words = new ArrayList<>();
        while (m.find())
            words.add(m.group());
        return words;
    }

    public static String[] getEncWords(String encEssay) {
        int count = encEssay.length() / 64;
        String[] encWords = new String[count];
        for (int i = 0; i < count; i++) {
            String temp = "";
            for (int j = i * 64; j < i * 64 + 64; j++) {
                temp += encEssay.charAt(j);
            }
            encWords[i] = temp;
        }
        return encWords;
    }

    public static void deleteFiles(String dir) {
        File[] files = getDirFiles(dir);
        for (File file : files) {
            if (file.exists()){
                file.delete();
            }
        }
    }
}
