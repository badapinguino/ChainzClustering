import util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import org.apache.log4j.Logger;

public class Main {
    /** Folder that contains the chains to be clustered */
    private static String chainzFolder;
    /** Maximum difference that you want in every cluster */
    private static int maxDifference;
    /** Is the method used to calculate the distance */
    private static String differenceComputationTypeSelected;

    /** Used to log info and errors on file */
    private static Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * The program is used to cluster all the files contained in a folder. Every file should contain a chain of Java methods. <br>
     * It should be called with the following arguments: <br>
     * <i>&lt;ChainzFolder&gt;</i> is the folder that contains all and only the chain files that you want to be clustered. 	 [Default: ./targetChainz] <br>
     * <i>&lt;MaxDifference&gt;</i> is the maximum difference that you want in your cluster (ex: 2 methods can differ, or a hamming distance of 40). 	 [Default: 2] <br>
     * <i>&lt;DifferenceComputationType&gt;</i> is the method that you want to use to calculate the distance. The supported method are: Hamming, Levenshtein, Methods, HammingMethods, LevenshteinMethods. 	 [Default: LevenshteinMethods].
     *
     * @param args &lt;ChainzFolder&gt; &lt;MaxDifference&gt; &lt;DifferenceComputationType&gt;
     */
    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("The program should be called with the following arguments:\n" +
                    "\t<ChainzFolder>\n" +
                    "\t<MaxDifference>\n" +
                    "\t<DifferenceComputationType>\n" +
                    "<ChainzFolder> is the folder that contains all and only the chain files " +
                    "that you want to be clustered. \t [Default: ./targetChainz]\n" +
                    "<MaxDifference> is the maximum difference that you want in your cluster " +
                    "(ex: 2 methods can differ, or a hamming distance of 40). \t [Default: 2]\n" +
                    "<DifferenceComputationType> is the method that you want to use to calculate the distance. " +
                    "The supported method are: Hamming, Levenshtein, Methods, HammingMethods, LevenshteinMethods. \t " +
                    "[Default: LevenshteinMethods].");
            logger.info("The program should be called with the following arguments:\n" +
                    "\t<ChainzFolder>\n" +
                    "\t<MaxDifference>\n" +
                    "\t<DifferenceComputationType>\n" +
                    "<ChainzFolder> is the folder that contains all and only the chain files " +
                    "that you want to be clustered. \t [Default: ./targetChainz]\n" +
                    "<MaxDifference> is the maximum difference that you want in your cluster " +
                    "(ex: 2 methods can differ, or a hamming distance of 40). \t [Default: 2]\n" +
                    "<DifferenceComputationType> is the method that you want to use to calculate the distance. " +
                    "The supported method are: Hamming, Levenshtein, Methods, HammingMethods, LevenshteinMethods. \t " +
                    "[Default: LevenshteinMethods].");
            chainzFolder = "./targetChainz";
            maxDifference = 2;
            differenceComputationTypeSelected = "LevenshteinMethods";
        } else {
            chainzFolder = args[0];
            maxDifference = 2;
            differenceComputationTypeSelected = "LevenshteinMethods";
            if(args.length > 1) {
                if(Integer.parseInt(args[1])>0){
                    maxDifference = Integer.parseInt(args[1]);
                } else {
                    System.out.println("The maximum difference must be more than 0");
                    logger.error("MaxDifference: " + maxDifference);
                    logger.error("The maximum difference must be more than 0");
                    return;
                }
                if(args.length > 2) {
                    differenceComputationTypeSelected = args[2];
                }
            }
        }
        logger.info("ChainzFolder: " + chainzFolder);
        logger.info("MaxDifference: " + maxDifference);
        logger.info("DifferenceComputationTypeSelected: " + differenceComputationTypeSelected);
        // Creating a File object for directory
        File chainzDirectory = new File(chainzFolder);
        // List of all files and directories
        File[] filesList = chainzDirectory.listFiles();
        // List of chain files
        List<File> chainFiles = new ArrayList<>();
        logger.info("List of files and directories in the specified directory:");
        for(File file : filesList) {
            logger.info("File name: "+file.getName());
            logger.info("File path: "+file.getAbsolutePath());
            logger.info("Size: "+file.getTotalSpace());
            logger.info(" ");
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
                        // Utils.executeAllDifferenceTypes(singleChainFile, interiorIterationChainFile);
                        if(differenceComputationTypeSelected.equalsIgnoreCase("Hamming")){
                            String singleChainString = null;
                            String interiorIterationChainString = null;
                            try {
                                singleChainString = Utils.readFromFile(singleChainFile);
                                interiorIterationChainString = Utils.readFromFile(interiorIterationChainFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            int hammingDistance = Utils.hammingDistance(singleChainString, interiorIterationChainString);
                            if(hammingDistance >= 0 && hammingDistance <= maxDifference) {
                                if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                    clusterN.add(interiorIterationChainFile);
                                }
                            }
                        }else if(differenceComputationTypeSelected.equalsIgnoreCase("Levenshtein")){
                            String singleChainString = null;
                            String interiorIterationChainString = null;
                            try {
                                singleChainString = Utils.readFromFile(singleChainFile);
                                interiorIterationChainString = Utils.readFromFile(interiorIterationChainFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            int levenshteinDistance = Utils.levenshteinDistance(singleChainString, interiorIterationChainString);
                            if(levenshteinDistance >= 0 && levenshteinDistance <= maxDifference) {
                                if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                    clusterN.add(interiorIterationChainFile);
                                }
                            }
                        } else if(differenceComputationTypeSelected.equalsIgnoreCase("Methods")){
                            int methodsDistance = -2;
                            try {
                                methodsDistance = Utils.methodDistance(singleChainFile, interiorIterationChainFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            if(methodsDistance >= 0 && methodsDistance <= maxDifference) {
                                if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                    clusterN.add(interiorIterationChainFile);
                                }
                            }
                        } else if(differenceComputationTypeSelected.equalsIgnoreCase("HammingMethods")){
                            int hammingMethodsDistance = -2;
                            try {
                                hammingMethodsDistance = Utils.hammingMethodDistance(singleChainFile, interiorIterationChainFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            if(hammingMethodsDistance >= 0 && hammingMethodsDistance <= maxDifference) {
                                if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                    clusterN.add(interiorIterationChainFile);
                                }
                            }
                        } else if (differenceComputationTypeSelected.equalsIgnoreCase("LevenshteinMethods")) {
                            int levenshteinMethodsDistance = -2;
                            try {
                                levenshteinMethodsDistance = Utils.levenshteinMethodDistance(singleChainFile, interiorIterationChainFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            if(levenshteinMethodsDistance >= 0 && levenshteinMethodsDistance <= maxDifference) {
                                if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                    clusterN.add(interiorIterationChainFile);
                                }
                            }
                        } else {
                            System.out.println("You must provide a supported <DifferenceType> like: " +
                                    "Hamming, Levenshtein, Methods, HammingMethods, LevenshteinMethods!");
                            logger.error("You must provide a supported <DifferenceType> like: " +
                                    "Hamming, Levenshtein, Methods, HammingMethods, LevenshteinMethods!");
                            return;
                        }
                    }
                }
                clustersList.add(clusterN);
            }
        }
        logger.info("Clusters list: " + clustersList);
        // Saving clusters to different files.
        Utils.saveClustersOnFiles(clustersList);

    }
}
