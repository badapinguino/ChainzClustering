package util;

import java.io.*;
import java.util.ArrayList;
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

    public static int methodDistance(File file1, File file2) throws FileNotFoundException {
        List<String> file1MethodsList = new ArrayList<>();
        List<String> file2MethodsList = new ArrayList<>();
        // Scanner used to read files
        Scanner sc;
        //Instantiating the Scanner class
        sc= new Scanner(file1);
        String input;
        while (sc.hasNextLine()) {
            input = sc.nextLine();
            if(!input.trim().equals("[") && !input.trim().equals("]")){
                file1MethodsList.add(input);
            }
        }
        //Instantiating the Scanner class
        sc= new Scanner(file2);
        while (sc.hasNextLine()) {
            input = sc.nextLine();
            if(!input.trim().equals("[") && !input.trim().equals("]")){
                file2MethodsList.add(input);
            }
        }
        // rimuovo dalla prima lista i metodi già presenti nella seconda lista, rimangono solo quelli presenti in file1
        List<String> diffFile1File2List = new ArrayList<>(file1MethodsList);
        diffFile1File2List.removeAll(file2MethodsList);
        // rimuovo dalla seconda lista i metodi già presenti nella prima lista, rimangono solo quelli presenti in file2
        List<String> diffFile2File1List = new ArrayList<>(file2MethodsList);
        diffFile2File1List.removeAll(file1MethodsList);
        // The difference is made by the methods that are not in the lists
        // 1 method more on each one results in a difference of two
        //return diffFile1File2List.size() + diffFile2File1List.size();

        // 1 method less in the file1 and 2 methods more in file2 means a difference of two (because one is added to
        // file1 and the other one is the method changed between file1 and file2)
        return max(diffFile1File2List.size(), diffFile2File1List.size());
    }

    // TODO: levenshtein method distance, forse sarebbe la più corretta
    public static int levenshteinMethodDistance(File file1, File file2) throws FileNotFoundException {

        String[] file1MethodsArray = readFromFile(file1).split("\n");
        String[] file2MethodsArray = readFromFile(file2).split("\n");
        int[][] dp = new int[file1MethodsArray.length + 1][file2MethodsArray.length + 1];

        for (int i = 0; i <= file1MethodsArray.length; i++) {
            for (int j = 0; j <= file2MethodsArray.length; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    int costOfSubstitution = file1MethodsArray[i-1].equals(file2MethodsArray[j-1]) ? 0 : 1;
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution,
                                    //+ costOfSubstitution(str1.charAt(i - 1), str2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[file1MethodsArray.length][file2MethodsArray.length];
    }

    // TODO: hamming method distance, potrebbe già andare bene dato che le chain sembrano avere una misura uguale
    public static int hammingMethodDistance(File file1, File file2) throws FileNotFoundException {


        String[] file1MethodsArray = readFromFile(file1).split("\n");
        String[] file2MethodsArray = readFromFile(file2).split("\n");

        if(file1MethodsArray.length != file2MethodsArray.length){
            return -1;
        } else {
            int count=0;
            for (int i = 0; i < file1MethodsArray.length; i++) {
                if(!file1MethodsArray[i].equals(file2MethodsArray[i]))
                count++;
            }
            return count;
        }
    }

    public static void writeListToFile(List<File> list, String fileName) throws IOException {
        // creates output directory if doesn't exists
        new File("./output/").mkdirs();
        File file = new File("./output/" + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < list.size(); i++) {
            bw.write(list.get(i).getName() + "\n");
            // writes on file the file content too (the chain)
            bw.write("\t" + readFromFile(list.get(i)));
        }
        bw.flush();
        bw.close();
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

    private static int max(int... numbers) {
        return Arrays.stream(numbers)
                .max().orElse(Integer.MIN_VALUE);
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static void executeAllDifferenceTypes(File singleChainFile, File interiorIterationChainFile) throws FileNotFoundException {
        String singleChainString = Utils.readFromFile(singleChainFile);
        String interiorIterationChainString = Utils.readFromFile(interiorIterationChainFile);
        int hammingDistance = Utils.hammingDistance(singleChainString, interiorIterationChainString);
        int levenshteinDistance = Utils.levenshteinDistance(singleChainString, interiorIterationChainString);
        // calculate the distance between external and internal chain checking how many methods are different between them
        int methodsDistance = Utils.methodDistance(singleChainFile, interiorIterationChainFile);
        int hammingMethodsDistance = Utils.hammingMethodDistance(singleChainFile, interiorIterationChainFile);
        int levenshteinMethodsDistance = Utils.levenshteinMethodDistance(singleChainFile, interiorIterationChainFile);
        System.out.println("File1: " + singleChainFile.getName() +
                "\nFile2: " + interiorIterationChainFile.getName());
        System.out.println("Hamming distance: " + hammingDistance);
        System.out.println("Levenshtein distance: " + levenshteinDistance);
        System.out.println("Methods distance: " + methodsDistance);
        System.out.println("Hamming methods distance: " + hammingMethodsDistance);
        System.out.println("Levenshtein methods distance: " + levenshteinMethodsDistance);
    }
}
