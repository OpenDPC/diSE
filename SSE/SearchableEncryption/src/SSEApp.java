import java.util.List;
import java.util.Scanner;


public class SSEApp {
    public static String plain_dir = "plain_data";

    public static String encrypted_dir = "encrypted_data";

    private static String enceypted_key = "Sixteen byte key";

    private static String ki_key = "This is a ki key";


    public static void main(String[] args) throws Exception {
        if (args.length == 0){
            System.out.println("Usage: java -jar SSEApp.jar [ index | trapdoor | search | decrypt ]");
            return;
        }

        if (args[0].equals("index")){
            if (args.length != 5){
                System.out.println("Usage: java -jar SSEApp.jar index Key KiKey plain_dir encrypted_dir");
                return;
            }

            SearchableEncryption.encryptFile(args[1], args[2], args[3], args[4]);
        }
        else if (args[0].equals("trapdoor")){
            if (args.length != 5){
                System.out.println("Usage: java -jar SSEApp.jar trapdoor Key KiKey keyword trapdoor_dir");
                return;
            }

            SearchableEncryption.genTrapdoor(args[1], args[2], args[3], args[4]);

        }else if (args[0].equals("search")){
            if (args.length != 4){
                System.out.println("Usage: java -jar SSEApp.jar search trapdoor_dir encrypted_dir encrypted_search_result_dir");
                return;
            }

            List<String> searchResult = SearchableEncryption.searchFile(args[1], args[2], args[3]);

            System.out.println("Search result is:");
            for (String fileName : searchResult){
                System.out.println(fileName);
            }

        } else if(args[0].equals("decrypt")){
            if (args.length != 4){
                System.out.println("Usage: java -jar SSEApp.jar decrypt Key search_result_dir encrypted_search_result_dir");
                return;
            }

            SearchableEncryption.decryptSearchResult(args[1], args[2], args[3]);
        }else{
            System.out.println("Usage: java -jar SSEApp.jar [index|trapdoor|search|decrypt]\n");
        }


    }
}
