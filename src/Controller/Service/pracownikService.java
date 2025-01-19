package Controller.Service;

import Model.Pracownik;
import Model.PracownikRepository;
import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class pracownikService {

    private static final PracownikRepository pracownikRepository = new PracownikRepository();
    private static final ExecutorService savePool = Executors.newFixedThreadPool(10);

    private final String baseFolder = "typeofolder";

    private static void savePracownikToFile(Pracownik pracownik, String filename) {
        try {
            String file = filename + "_pracownik_" + pracownik.getPesel() + ".ser";
            pracownikRepository.serializeToZip(file);
            System.out.println("Saved employee #" + pracownik.getPesel());
        } catch (Exception e) {
            System.err.println("Error while saving employee #" + pracownik.getPesel() + ": " + e.getMessage());
        }
    }
    
    public void savePracownicyToFile(String fileName) {
        File folder = new File(baseFolder);

        String filePath = folder.getPath() + File.separator + fileName;

//        CompletableFuture<Void> allOf = CompletableFuture.allOf(
//                pracownikRepository.getAllPracownicy().stream()
//                        .map(pracownik -> CompletableFuture.runAsync(() -> {
//                            savePracownikToFile(pracownik, filePath);
//                        }, Executors.newSingleThreadExecutor()))
//                        .toArray(CompletableFuture[]::new)
//        );

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                pracownikRepository.getAllPracownicy().stream()
                        .map(pracownik -> CompletableFuture.runAsync(() -> {
                            savePracownikToFile(pracownik, filePath);
                        }, savePool))
                        .toArray(CompletableFuture[]::new)
        );


        try {
            allOf.join();
            System.out.println("All employees have been saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            savePool.shutdown();
        }
    }


    private Pracownik readPracownikFromFile(File file){
        try{
            List<Pracownik> pracownicy = pracownikRepository.deserializeFromZip(file.getAbsolutePath());
            if (pracownicy != null && pracownicy.size() == 1){
                return pracownicy.get(0);
            }else{
                System.err.println("Liczba pracowników w pliku nie równa się 1: " + file.getName() );
            }
        }catch (Exception e){
            System.err.println("Nie udało się odczytać plik: " + file.getName() + " " + e.getMessage());
        }
        return null;
    }

        public List<Pracownik> readAllPracownicyFromFiles(String folderName){
            ExecutorService readPool = Executors.newFixedThreadPool(10);
            List<CompletableFuture<Pracownik>> futureList;

            try{
//                File folder = new File(folderPath);

                File folder = new File(baseFolder + File.separator + folderName );

                if (!folder.exists() || !folder.isDirectory()) {
                    System.err.println("Taki folder nie istnieje: " + folderName);
                    return List.of(); //pusta lista
                }

                File[] files = folder.listFiles();
                if( files == null || files.length == 0 ){
                    System.err.println("Folder jest pusty" + folderName);
                    return List.of();
                }

                futureList = List.of(files).stream()
                        .filter(File::isFile)
                        .map(file -> CompletableFuture.supplyAsync(() -> readPracownikFromFile(file), readPool))
                        .collect(Collectors.toList());


                List<Pracownik> loadedPracownicy = futureList.stream()
                        .map(future -> {
                            try {
                                return future.join(); // czeka na skończenie zadania
                            } catch (Exception e) {
                                System.err.println("Error processing file: " + e.getMessage());
                                return null;
                            }
                        })
                        .filter(pracownik -> pracownik != null)
                        .collect(Collectors.toList());

                System.out.println("OK, załadowano " + loadedPracownicy.size() + " pracowników ");
                return loadedPracownicy;
            }finally {
                readPool.shutdown();
            }
        }





}
