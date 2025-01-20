package Controller.Service;

import Model.Pracownik;
import Model.PracownikRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class pracownikService2 {
    private final ExecutorService executor =  Executors.newFixedThreadPool(10);
    private final Map<String, Pracownik> pracownicy = new ConcurrentHashMap<>();

    private final String defaultSaveFolder;

    public pracownikService2(String defaultSaveFolder) {
        this.defaultSaveFolder = defaultSaveFolder;

    }

    public void savePracownikToDefaultFolder(Pracownik pracownik) {
        savePracownikToFile(pracownik, defaultSaveFolder);
    }

    private void savePracownikToFile(Pracownik pracownik, String folderPath) {
        try {
            Files.createDirectories(Paths.get(folderPath));
            String filePath = folderPath + File.separator + pracownik.getPesel() + ".dat";
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(pracownik);
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisywania pracownika: " + e.getMessage());
        }
    }

    public List<Pracownik> loadWorkersFromFolder(String folderPath) {
        List<CompletableFuture<Pracownik>> futures = new ArrayList<>();
        List<Pracownik> workers = new ArrayList<>();

        try {
            List<Path> files = Files.list(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            for (Path file : files) {
                futures.add(CompletableFuture.supplyAsync(() -> readPracownikFromFile(file.toString()), executor));
            }

            for (CompletableFuture<Pracownik> future : futures) {
                try {
                    Pracownik worker = future.join();
                    if (worker != null) {
                        workers.add(worker);
                    }
                } catch (CompletionException e) {
                    System.err.println("Błąd podczas odczytu pracownika: " + e.getCause());
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu plików z folderu: " + e.getMessage());
        }

        return workers;
    }

    private Pracownik readPracownikFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Pracownik) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Błąd podczas odczytu pliku: " + filePath + " - " + e.getMessage());
            return null;
        }
    }


}
