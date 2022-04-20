import util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static int maxDifference;
    private static String differenceTypeSelected;
    public static void main(String[] args) throws FileNotFoundException {
        // Cartella nella quale cercare le catene da clusterizzare
        String chainzFolder;
        // TODO aggiungere la possibilità di regolare la differenza massima per raggruppare in cluster (es: 2 o 3) e
        //  anche la possibilità di usare la distanza di hamming, levenshtein o numero di metodi
        if(args.length < 1){
            // TODO: cambiare le stampe in log
            System.out.println("The program should be called with the following arguments:\n" +
                    "\t<ChainzFolder>\n" +
                    "\t<MaxDifference>\n" +
                    "\t<DifferenceType>\n" +
                    "<ChainzFolder> is the folder that contains all and only the chain files " +
                    "that you want to be clustered. \t [Default: ./targetChainz]\n" +
                    "<MaxDifference> is the maximum difference that you want in your cluster " +
                    "(ex: 2 methods can differ, or a hamming distance of 40). \t [Default: 2]\n" +
                    "<DifferenceType> is the method that you want to use to calculate the distance. " +
                    "The supported method are: Hamming, Levenshtein, Methods, HammingMethods, LevenshteinMethods. \t " +
                    "[Default: LevenshteinMethods].");
            chainzFolder = "./targetChainz";
            maxDifference = 2;
            differenceTypeSelected = "LevenshteinMethods";
        } else {
            chainzFolder = args[0];
            maxDifference = 2;
            differenceTypeSelected = "LevenshteinMethods";
            if(args.length > 1) {
                if(Integer.parseInt(args[1])>0){
                    maxDifference = Integer.parseInt(args[1]);
                } else {
                    System.out.println("The maximum difference must be more than 0");
                }
                if(args.length > 2) {
                    differenceTypeSelected = args[2];
                }
            }
        }
        // Creating a File object for directory
        File chainzDirectory = new File(chainzFolder);
        // List of all files and directories
        File[] filesList = chainzDirectory.listFiles();
        // List of chain files
        List<File> chainFiles = new ArrayList<>();
        System.out.println("List of files and directories in the specified directory:");
        for(File file : filesList) {
            System.out.println("File name: "+file.getName());
            System.out.println("File path: "+file.getAbsolutePath());
            System.out.println("Size: "+file.getTotalSpace());
            System.out.println(" ");
            if(file.isFile()){
                chainFiles.add(file);
            }
        }

        // Creating the clusters list
        List<List<File>> clustersList = new ArrayList<>();
        for ( File singleChainFile: chainFiles ) {
            // if the file has been already clustered this iteration is not necessary
            if(!Utils.contains(clustersList, singleChainFile)) {
                // creating the cluster of this singleChainFile
                List<File> clusterN = new ArrayList<>();
                clusterN.add(singleChainFile);
                // calculating distance between this chain and all the others
                for (File interiorIterationChainFile : chainFiles) {
                    if (singleChainFile != interiorIterationChainFile) {
                        // execute and print all types of difference calculation.
                        Utils.executeAllDifferenceTypes(singleChainFile, interiorIterationChainFile);

                        if(differenceTypeSelected.equalsIgnoreCase("Hamming")){
                            String singleChainString = Utils.readFromFile(singleChainFile);
                            String interiorIterationChainString = Utils.readFromFile(interiorIterationChainFile);
                            int hammingDistance = Utils.hammingDistance(singleChainString, interiorIterationChainString);
                            if(hammingDistance >= 0 && hammingDistance <= maxDifference) {
                                if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                    clusterN.add(interiorIterationChainFile);
                                }
                            }
                        }else if(differenceTypeSelected.equalsIgnoreCase("Levenshtein")){
                            String singleChainString = Utils.readFromFile(singleChainFile);
                            String interiorIterationChainString = Utils.readFromFile(interiorIterationChainFile);
                            int levenshteinDistance = Utils.levenshteinDistance(singleChainString, interiorIterationChainString);
                            if(levenshteinDistance >= 0 && levenshteinDistance <= maxDifference) {
                                if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                    clusterN.add(interiorIterationChainFile);
                                }
                            }
                        } else if(differenceTypeSelected.equalsIgnoreCase("Methods")){
                            int methodsDistance = Utils.methodDistance(singleChainFile, interiorIterationChainFile);
                            if(methodsDistance >= 0 && methodsDistance <= maxDifference) {
                                if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                    clusterN.add(interiorIterationChainFile);
                                }
                            }
                        } else if(differenceTypeSelected.equalsIgnoreCase("HammingMethods")){
                            int hammingMethodsDistance = Utils.hammingMethodDistance(singleChainFile, interiorIterationChainFile);
                            if(hammingMethodsDistance >= 0 && hammingMethodsDistance <= maxDifference) {
                                if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                    clusterN.add(interiorIterationChainFile);
                                }
                            }
                        } else if (differenceTypeSelected.equalsIgnoreCase("LevenshteinMethods")) {
                            int levenshteinMethodsDistance = Utils.levenshteinMethodDistance(singleChainFile, interiorIterationChainFile);
                            if(levenshteinMethodsDistance >= 0 && levenshteinMethodsDistance <= maxDifference) {
                                if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                    clusterN.add(interiorIterationChainFile);
                                }
                            }
                        } else {
                            System.out.println("You must provide a supported <DifferenceType> like: " +
                                    "Hamming, Levenshtein, Methods, HammingMethods, LevenshteinMethods!");
                            return;
                        }
                    }
                }
                clustersList.add(clusterN);
            }
        }
        System.out.println(clustersList);
        // Saving clusters to different files.
        // TODO: create a method for this saving operation
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HHmmss");
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
