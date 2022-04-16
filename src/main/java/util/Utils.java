package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static String readFromFile(File file) throws FileNotFoundException {
        // Scanner used to read files
        Scanner sc;
        //Instantiating the Scanner class
        sc= new Scanner(file);
        String input;
        StringBuffer sb = new StringBuffer();
        while (sc.hasNextLine()) {
            input = sc.nextLine();
            sb.append(input+"\n");
        }
        return sb.toString();
    }

    public static int hammingDistance(String str1, String str2){
        int count=0;
        if(str1.length()!=str2.length()) {
            return -1;
        } else {
            for (int i = 0; i < str1.length(); i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    count++;
                }
            }
            return count;
        }
    }

    public static boolean contains(List<List<File>> listOfFileLists, File fileToSearch) {
        for (List<File> fileList: listOfFileLists) {
            if(fileList.contains(fileToSearch)){
                return true;
            }
        }
        return false;
    }

    public static int levenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            for (int j = 0; j <= str2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(str1.charAt(i - 1), str2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[str1.length()][str2.length()];
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
}
