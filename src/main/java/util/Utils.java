package util;

import org.apache.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {
    /** Used to log info and errors on file */
    private static Logger logger = Logger.getLogger(Utils.class.getName());

    /**
     * Used to read a file and return the file content as a String.
     * @param file The input file to read.
     * @return A String containing the file content.
     * @exception FileNotFoundException When the file doesn't exists
     */
    public static String readFromFile(File file) throws FileNotFoundException {
        // Scanner used to read files
        Scanner sc;
        // Instantiating the Scanner class
        sc= new Scanner(file);
        String input;
        StringBuffer sb = new StringBuffer();
        while (sc.hasNextLine()) {
            input = sc.nextLine();
            sb.append(input+"\n");
        }
        return sb.toString();
    }

    /**
     * Method used to calculate the Hamming distance between two strings.
     * @param str1 The first string.
     * @param str2 The second string.
     * @return An integer that represents the Hamming distance between the two strings.
     */
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

    /**
     * Checks if a file is contained into a List of List of Files.
     * @param listOfFileLists The List of List of Files where the fileToSearch is searched.
     * @param fileToSearch The file to search.
     * @return True if the file is found, false otherwise.
     */
    public static boolean contains(List<List<File>> listOfFileLists, File fileToSearch) {
        for (List<File> fileList: listOfFileLists) {
            if(fileList.contains(fileToSearch)){
                return true;
            }
        }
        return false;
    }

    /**
     * Method used to calculate the Levenshtein distance between two strings.
     * @param str1 The first string.
     * @param str2 The second string.
     * @return An integer that represents the Levenshtein distance between the two strings.
     */
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

    /** Method used to calculate the distance between two files:
     *  The distance are maximum different lines between the two files, where every line is a method of a chain.
     * @param file1 The first file containing the first chain of methods.
     * @param file2 The second file containing the second chain of methods.
     * @return An integer representing the distance between the two chains.
     * @exception FileNotFoundException When a file doesn't exists
     */
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

    /** This method uses a way to compute the Hamming distance not on every character of two strings but on every
     * line of two files.
     * In this way we can compute the distance between the methods of two different chains in a way that is the same
     * used in the Hamming distance.
     * @param file1 The first file containing the first chain of methods.
     * @param file2 The second file containing the second chain of methods.
     * @param maxDifferenceForEachLine The maximum Levenshtein difference for each line (method) in a file (chain) to be considered equal to another one.
     * @return An integer representing the distance between the two chains.
     * @exception FileNotFoundException When a file doesn't exists
     */
    public static int hammingMethodDistance(File file1, File file2, int maxDifferenceForEachLine) throws FileNotFoundException {


        String[] file1MethodsArray = readFromFile(file1).split("\n");
        String[] file2MethodsArray = readFromFile(file2).split("\n");

        if(file1MethodsArray.length != file2MethodsArray.length){
            return -1;
        } else {
            int count=0;
            for (int i = 0; i < file1MethodsArray.length; i++) {
                if(maxDifferenceForEachLine<=0) {
                    if (!file1MethodsArray[i].equals(file2MethodsArray[i])) {
                        count++;
                    }
                } else {
                    if(levenshteinDistance(file1MethodsArray[i], file2MethodsArray[i])>maxDifferenceForEachLine){
                        count++;
                    }
                }
            }
            return count;
        }
    }

    /** This method uses a way to compute the Levenshtein distance not on every character of two strings but on every
     * line of two files.
     * In this way we can compute the distance between the methods of two different chains in a way that is the same
     * used in the Levenshtein distance.
     * This is the best way to calculate the different methods in two chains that have not the same length.
     * @param file1 The first file containing the first chain of methods.
     * @param file2 The second file containing the second chain of methods.
     * @param maxDifferenceForEachLine The maximum Levenshtein difference for each line (method) in a file (chain) to be considered equal to another one.
     * @return An integer representing the distance between the two chains.
     * @exception FileNotFoundException When a file doesn't exists
     */
    public static int levenshteinMethodDistance(File file1, File file2, int maxDifferenceForEachLine) throws FileNotFoundException {

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
                    int costOfSubstitution;
                    if(maxDifferenceForEachLine<=0) {
                        costOfSubstitution = file1MethodsArray[i - 1].equals(file2MethodsArray[j - 1]) ? 0 : 1;
                    } else {
                        String temp1 = file1MethodsArray[i-1];
                        String temp2 = file2MethodsArray[j-1];
                        int levenTemp = levenshteinDistance(temp1, temp2);
                        if(levenshteinDistance(file1MethodsArray[i-1], file2MethodsArray[j-1]) > maxDifferenceForEachLine) {
                            costOfSubstitution = 1;
                        } else {
                            costOfSubstitution = 0;
                        }
                    }
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

    /**
     * Method used to write a File list on an output file located in the ./output folder.
     * @param list The list of Files to be written.
     * @param fileName The file name of the output file.
     * @throws IOException In case of errors writing the file.
     * @see IOException
     */
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

    /**
     * Method used to calculate the minimum of several numbers.
     * @param numbers Integer numbers.
     * @return the minimum of all the numbers provided as parameter.
     */
    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

    /**
     * Method used to calculate the maximum of several numbers.
     * @param numbers Integer numbers.
     * @return the maximum of all the numbers provided as parameter.
     */
    private static int max(int... numbers) {
        return Arrays.stream(numbers)
                .max().orElse(Integer.MIN_VALUE);
    }

    /**
     * Method used in the LevenshteinDistance to calculate the cost of a substitution of a character.
     * @param a First Character
     * @param b Second Character
     * @return 0 if a = b, 1 if a != b.
     */
    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    /** Method used to execute all Difference computation methods and log the result.
     * The purpose of this method is to show the difference between all the computation methods.
     * @param singleChainFile The first file to use for compute all the differences.
     * @param interiorIterationChainFile The second file to use for compute all the differences.
     * @param maxDifferenceForEachLine The maximum Levenshtein difference for each line (method) in a file (chain) to be considered equal to another one.
     * @throws FileNotFoundException If a file doesn't exists.
     */
    public static void executeAllDifferenceTypes(File singleChainFile, File interiorIterationChainFile, int maxDifferenceForEachLine) throws FileNotFoundException {
        String singleChainString = Utils.readFromFile(singleChainFile);
        String interiorIterationChainString = Utils.readFromFile(interiorIterationChainFile);
        int hammingDistance = Utils.hammingDistance(singleChainString, interiorIterationChainString);
        int levenshteinDistance = Utils.levenshteinDistance(singleChainString, interiorIterationChainString);
        // calculate the distance between external and internal chain checking how many methods are different between them
        int methodsDistance = Utils.methodDistance(singleChainFile, interiorIterationChainFile);
        int hammingMethodsDistance = Utils.hammingMethodDistance(singleChainFile, interiorIterationChainFile, maxDifferenceForEachLine);
        int levenshteinMethodsDistance = Utils.levenshteinMethodDistance(singleChainFile, interiorIterationChainFile, maxDifferenceForEachLine);
        logger.info("File1: " + singleChainFile.getName() +
                "\nFile2: " + interiorIterationChainFile.getName());
        logger.info("Hamming distance: " + hammingDistance);
        logger.info("Levenshtein distance: " + levenshteinDistance);
        logger.info("Methods distance: " + methodsDistance);
        logger.info("Hamming methods distance: " + hammingMethodsDistance);
        logger.info("Levenshtein methods distance: " + levenshteinMethodsDistance);
    }

    /**
     * Method used to save the clusters list on different files.
     * @param clustersList The List containting the Lists of Files (the clusters) to be saved.
     */
    public static void saveClustersOnFiles(List<List<File>> clustersList){
        DateFormat dateFormat = new SimpleDateFormat("yyyyddMM-HHmmss");
        Date date = new Date();
        String dateToStr = dateFormat.format(date);
        int i = 1;
        for (List<File> cluster: clustersList) {
            String fileName = "Cluster" + i + "_" + dateToStr + ".txt";
            try {
                Utils.writeListToFile(cluster, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
