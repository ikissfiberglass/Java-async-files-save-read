package Model;

import View.ViewImpl;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

public class PracownikRepository implements Serializable {
    ViewImpl view = new ViewImpl();
    private static final long serialVersionUID = 1L;
    private List<Pracownik> pracownicy = new ArrayList<>();

    public PracownikRepository() {
    }

    public List<Pracownik> getAllPracownicy() {
        return new ArrayList<>(pracownicy);
    }

    public String getPracownicyStringRepresentation() {
        return pracownicy.toString();
    }

    public int getPracownicySize() {
        return pracownicy.size();
    }

    public void addPracownik(Pracownik pracownik) {
        pracownicy.add(pracownik);
    }

    public void removePracownik(Pracownik pracownik) {
        pracownicy.remove(pracownik);
    }

    public Pracownik findPracownikByPesel(String pesel) {
        for (Pracownik pracownik : pracownicy) {
            if (pracownik.getPesel().equals(pesel)) {
                return pracownik;
            }
        }
        return null;
    }

    public String removePracownikByPesel(String pesel) {
        Pracownik pracownik = findPracownikByPesel(pesel);
        if (pracownik != null && pracownicy.remove(pracownik)) {
            return "Pracownik z numerem PESEL: [" + pesel + "] został usunięty";
        }
        return "Nie udało się usunąć pracownika (brak w liście lub błąd)";
    }

    public Pracownik getPracownikByIndex(int index) throws IndexOutOfBoundsException{
        if (index >= 0 && index < pracownicy.size()) {
            try {
                return pracownicy.get(index);
            }catch (IndexOutOfBoundsException e){
                view.displayError("Niema takiego pracownika (z poziomu PracownikRepsoitory)");
            }
        }
        return null;
    }

    public String getPracownikStringRepresentationByIndex(int index) {
        return pracownicy.get(index).toString();
    }

    public boolean containsPesel(String index){
        if(getPracownikByIndex(Integer.parseInt(index)) != null){
            return true;
        }else{
            return false;
        }
    }

    public void concatenate(ArrayList<Pracownik> localList){
            pracownicy.addAll(localList);
    }


    private void serializeToSer(String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(pracownicy);
        }
    }


    public void serializeToZip(String fileName) {
        if (!fileName.endsWith(".zip")) {
            fileName += ".zip";
        }

        File folder = new File("megafolder");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String serFileName = "megafolder" + File.separator + "pracownicy.ser";
        try {
            serializeToSer(serFileName);

            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(folder, fileName)));
                 FileInputStream fis = new FileInputStream(serFileName)) {
                zos.putNextEntry(new ZipEntry("pracownicy.ser"));

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Error during ZIP compression: " + ioe.getMessage());
        } finally {
            new File(serFileName).delete();
        }
    }

    public void serializeToGzip(String fileName) {
        if (!fileName.endsWith(".gz")) {
            fileName += ".gz";
        }

        File folder = new File("megafolder");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String serFileName = "megafolder" + File.separator + "pracownicy.ser";
        try {
            serializeToSer(serFileName);

            try (GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(new File(folder, fileName)));
                 FileInputStream fis = new FileInputStream(serFileName)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    gzos.write(buffer, 0, length);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Error during GZIP compression: " + ioe.getMessage());
        } finally {
            new File(serFileName).delete();
        }
    }


    private ArrayList<Pracownik> deserializeFromSer(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
//            view.displayMessageNewLine("deserialisation completed");
            return (ArrayList<Pracownik>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error during deserialization: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            new File(fileName).delete();
        }
    }


    public ArrayList<Pracownik> deserializeFromZip(String fileName) {
        File file = new File("megafolder" + File.separator + fileName);
        if (!file.exists()) {
            System.err.println("File not found: " + file.getPath());
            return new ArrayList<>();
        }

        String serFileName = "megafolder" + File.separator + "pracownicy.ser";
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
             FileOutputStream fos = new FileOutputStream(serFileName)) {

            ZipEntry entry = zis.getNextEntry();
            if (entry == null || !entry.getName().equals("pracownicy.ser")) {
                throw new IOException("Invalid or missing entry in ZIP file: " + entry);
            }

            byte[] buffer = new byte[1024];
            int length;
            while ((length = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.err.println("Error during ZIP decompression: " + ioe.getMessage());
            return new ArrayList<>();
        }

        return deserializeFromSer(serFileName);
    }

    public ArrayList<Pracownik> deserializeFromGzip(String fileName) {
        File file = new File("megafolder" + File.separator + fileName);
        if (!file.exists()) {
            System.err.println("File not found: " + file.getPath());
            return new ArrayList<>();
        }

        String serFileName = "megafolder" + File.separator + "pracownicy.ser";
        try (GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file));
             FileOutputStream fos = new FileOutputStream(serFileName)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = gzis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.err.println("Error during GZIP decompression: " + ioe.getMessage());
            return new ArrayList<>();
        }

        return deserializeFromSer(serFileName);
    }



    public List<Pracownik> deserialize(String fileName) {
        List<Pracownik> deserializedList = null;

        if (fileName.endsWith(".gz")) {
            deserializedList = deserializeFromGzip(fileName);
        } else if (fileName.endsWith(".zip")) {
            deserializedList = deserializeFromZip(fileName);
        } else {
            view.displayMessageNewLine("Nieobsługiwany format pliku: " + fileName);
            return pracownicy;
        }

        if (deserializedList != null && !deserializedList.isEmpty()) {
            pracownicy.addAll(deserializedList);
//            view.displayMessageNewLine("Deserialization complete. Workers added: " + deserializedList.size());
        } else {
            view.displayMessageNewLine("No data found in the file or deserialization failed.");
        }

        return pracownicy;
    }

}



