import util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        // Cartella nella quale cercare le catene da clusterizzare
        String chainzFolder;
        // TODO aggiungere la possibilità di regolare la differenza massima per raggruppare in cluster (es: 2 o 3) e
        //  anche la possibilità di usare la distanza di hamming, levenshtein o numero di metodi
        if(args.length < 1){
            System.out.println("The program must be called with the following arguments:\n" +
                    "\t<ChainzFolder>\n" +
                    "<ChainzFolder> should be the folder that contains all and only the chain files " +
                    "that you want to be clustered");
            // TODO: rimuovere, solo per debug
            chainzFolder = "./targetChainz";
        } else {
            chainzFolder = args[0];
        }
        //Creating a File object for directory
        File chainzDirectory = new File(chainzFolder);
        //List of all files and directories
        File[] filesList = chainzDirectory.listFiles();
        // List of chain files
        List<File> chainFiles = new ArrayList<>();
        System.out.println("List of files and directories in the specified directory:");
        for(File file : filesList) {
            // TODO valutare se aggiungere un controllo che il nome file finisca con estensione .chain (or whatever it is)
            System.out.println("File name: "+file.getName());
            System.out.println("File path: "+file.getAbsolutePath());
            System.out.println("Size :"+file.getTotalSpace());
            System.out.println(" ");
            if(file.isFile()){
                chainFiles.add(file);
            }
        }

        // Creating the clusters list
        //List<Map> singleCluster = new ArrayList<Map>(); // todo forse da cambiare con una lista contente più di una di questa lista
        //List<List> clustersList = new ArrayList<>();
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
                        System.out.println("Hamming methods distance: " + methodsDistance);
                        System.out.println("Levenshtein methods distance: " + levenshteinMethodsDistance);
                        //if(methodsDistance >= 0 && methodsDistance <= 2) {
                        //if(hammingMethodsDistance >= 0 && hammingMethodsDistance <= 2) {
                        if(levenshteinMethodsDistance >= 0 && levenshteinMethodsDistance <= 2) {
                            if(!Utils.contains(clustersList, interiorIterationChainFile)) {
                                clusterN.add(interiorIterationChainFile);
                            }
                        }
                    }
                }
                clustersList.add(clusterN);
            }
            // se nessun cluster contiene già questa mappa
            /*if(!clustersList.contains(chainEntry)){ // todo da cambiare con controllo se non è presente in ogni in ogni cluster dentro la lista
                for ( Map.Entry<File, String> chainEntryToConfront: chainsMap.entrySet() ) {
                    // TODO: calcola distanza tra chain esterna ed interna controllando quanti metodi sono diversi (max 2)
                }
            }*/
        }
        System.out.println(clustersList);
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
        // TODO vedere come gestire l'output dei vari cluster, se salvare su un file o creare più file per ogni cluster.
    }
}
