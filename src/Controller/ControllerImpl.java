package Controller;

import Controller.Service.pracownikService;
import Controller.Service.pracownikService2;
import Model.Dyrektor;
import Model.Handlowiec;
import Model.PracownikRepository;
import View.ViewImpl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import Model.Pracownik;

public class ControllerImpl {
    private final ViewImpl view = new ViewImpl();
    private final Scanner userInput = new Scanner(System.in);
    PracownikRepository pracownicy = new PracownikRepository();
//    private final pracownikService repository = new pracownikService();
    private final pracownikService2 repository = new pracownikService2("typeofolder" );


    public ControllerImpl() {
    }

    public void startMenu() {

        boolean startMenuRunning = true;
        while (startMenuRunning) {
            view.displayMessageNewLine("MENU");
            view.displayMessageNewLine("1. Lista pracowników");
            view.displayMessageNewLine("2. Dodaj pracownika");
            view.displayMessageNewLine("3. Usuń pracownika");
            view.displayMessageNewLine("4. Kopia zapasowa");
            view.displayMessageNewLine("5. Kopiowanie asynchroniczne");
            view.displayMessageNewLine("6. Odczytywanie asynchroniczne");
            view.displayMessageNewLine("q - wyjście");

            view.displayMessage("Wybór> \n");

            String startMenuOption = userInput.nextLine();
            switch (startMenuOption) {
                case "1":
                    view.displayMessageNewLine("Lista pracowników: ");
                    List<Pracownik> allWorkers = pracownicy.getAllPracownicy();

                    if (allWorkers.isEmpty()) {
                        view.displayMessageNewLine("Brak pracowników \n");
                    } else {
                        for (Pracownik pracownik : allWorkers) {
                            view.displayMessageNewLine(pracownik.toString() + "\n");

                        }
                    }
                    break;
                case "2":
                    boolean roleMenuRunning = true;
                    while (roleMenuRunning) {
                        view.displayMessageNewLine("[D]yrektor/[H]andlowiec:       ");
                        String roleOption = userInput.nextLine();
                        view.displayMessageNewLine("------------------------------------------------------------------");
                        if (roleOption.equalsIgnoreCase("d")) {
                            initializeDyrektor();
                            roleMenuRunning = false;
                        } else if (roleOption.equalsIgnoreCase("h")) {
                            intializeHandlowiec();
                            roleMenuRunning = false;
                        } else {
                            view.displayMessageNewLine("Spróbuj jeszcze raz");
                        }
                        view.displayMessageNewLine("------------------------------------------------------------------");
                    }


                    break;
                case "3":
                    view.displayMessageNewLine("Podaj Pesel pracownika: ");
                    String deletingOption = userInput.nextLine();

                    try {
                        if (pracownicy.findPracownikByPesel(deletingOption) != null) {
                            Pracownik foundPracownik = pracownicy.findPracownikByPesel(deletingOption);
                            view.displayMessageNewLine("Znaleziono: " + foundPracownik);

                            view.displayMessageNewLine("[Enter] - potwierdź \n[Q] - porzuć");
                            String validate = userInput.nextLine();
                            if (validate.trim().isEmpty()) {
                                String result = pracownicy.removePracownikByPesel(deletingOption);
                                view.displayMessageNewLine(result);
                            } else if (validate.equalsIgnoreCase("q")) {
                                view.displayMessageNewLine("Usuwanie anulowane");
                            }
                        } else {
                            view.displayMessageNewLine("Nie znaleziono pracownika o podanym numerze PESEL");
                        }
                    } catch (NumberFormatException e) {
                        view.displayError("Nieprawidłowy format numeru PESEL: " + e.getMessage());
                    }
                    break;

                case "4":
                    view.displayMessageNewLine("[Z]achowaj/[O]dtwórz :");
                    String serOption = userInput.nextLine();

                    if (serOption.equalsIgnoreCase("z")) {
                        view.displayMessageNewLine("Kompresja [G]zip/[Z]ip : ");
                        String archOption = userInput.nextLine();

                        if (archOption.equalsIgnoreCase("Z")) {
                            view.displayMessageNewLine("Nazwa pliku: ");
                            String fileName = userInput.nextLine();
                            pracownicy.serializeToZip(fileName);
                            view.displayMessageNewLine("Dane zostały zapisane do pliku ZIP: " + fileName + ".zip");
                        } else if (archOption.equalsIgnoreCase("g")) {
                            view.displayMessageNewLine("Nazwa pliku: ");
                            String fileName = userInput.nextLine();
                            pracownicy.serializeToGzip(fileName);
                            view.displayMessageNewLine("Dane zostały zapisane do pliku GZIP: " + fileName + ".gz");
                        } else {
                            view.displayMessageNewLine("Nie ma takiej opcji\n");
                        }
                    } else if (serOption.equalsIgnoreCase("o")) {
                        view.displayMessageNewLine("Nazwa pliku (z rozszerzeniem): ");
                        String fileName = userInput.nextLine();
                        ArrayList<Pracownik> tempArrayList;


                        if(fileName.endsWith(".zip") || fileName.endsWith(".gz")){
                            pracownicy.deserialize(fileName);
                            view.displayMessageNewLine("Sukces ");
                        }else{
                            view.displayMessageNewLine("Nieobsługiwany format pliku: " + fileName);
                        }
                    }
                    break;

                case "5":

//                    view.displayMessageNewLine("Podaj nazwę pliku: ");
//                    String filename = userInput.nextLine();
//                    repository.savePracownicyToFile(filename);
                    asyncSaveWorkers();

                    break;

                case "6":
//                    view.displayMessageNewLine("Podaj nazwę folderu z plikami: ");
//                    String folderPath = userInput.nextLine();
//
//                    List<Pracownik> loadedWorkers = repository.readAllPracownicyFromFiles(folderPath);
//
//                    if (loadedWorkers.isEmpty()) {
//                        view.displayMessageNewLine("Brak odczytanych pracowników.");
//                    } else {
//                        pracownicy.getAllPracownicy().addAll(loadedWorkers);
//                        for (Pracownik pracownik : loadedWorkers) {
//                            view.displayMessageNewLine(pracownik.toString() + "\n");
//                        }
//                    }

                    asyncLoadWorkers();


                    break;


                case "q":
                    startMenuRunning = false;
                    break;

                default:
                    view.displayMessageNewLine("Wpisz ponownie");
            }
        }
    }

 private void asyncSaveWorkers() {
     List<Pracownik> allWorkers = pracownicy.getAllPracownicy();
     if (allWorkers.isEmpty()) {
         view.displayMessageNewLine("Brak pracowników do zapisania.");
     } else {
         allWorkers.forEach(repository::savePracownikToDefaultFolder);
         view.displayMessageNewLine("Pracownicy zapisani asynchronicznie.");
     }
 }

    private void asyncLoadWorkers() {
        view.displayMessageNewLine("Podaj ścieżkę do folderu: ");
        String folderPath = userInput.nextLine();
        List<Pracownik> workers = repository.loadWorkersFromFolder(folderPath);
        if (workers.isEmpty()) {
            view.displayMessageNewLine("Brak pracowników do odczytania.");
        } else {
            workers.forEach(pracownicy::addPracownik);
            view.displayMessageNewLine("Pracownicy odczytani asynchronicznie i dodani do repozytorium.");
        }
    }


    public void initializeDyrektor () {
            view.displayMessageNewLine("Podaj Pesel dyrektora: ");
            String peselLocal = userInput.nextLine();

            view.displayMessageNewLine("Podaj imię dyrektora: ");
            String firstName = userInput.nextLine();

            view.displayMessageNewLine("Podaj nazwisko dyrektora: ");
            String secondName = userInput.nextLine();

            float wynagrodzenie = readFloat("Podaj wynagrodzenie dyrektora: ");
            view.displayMessageNewLine("Podaj numer telefonu dyrektora: ");
            String phoneNumber = userInput.nextLine();

            BigDecimal dodatekSluzbowy = readBigDecimal("Podaj dodatek służbowy dyrektora: ");
            view.displayMessageNewLine("Podaj numer karty: ");
            String kartaSluzbowa = userInput.nextLine();

            float limitKosztow = readFloat("Podaj limit kosztów dyrektora: ");

            view.displayMessageNewLine("[Enter] - zapisz");
            String validate = userInput.nextLine();
            if (validate.trim().isEmpty()) {
                try {
                    Dyrektor dyrektorLocal = new Dyrektor(peselLocal,
                            firstName,
                            secondName,
                            wynagrodzenie,
                            phoneNumber,
                            dodatekSluzbowy,
                            kartaSluzbowa,
                            limitKosztow);
                    pracownicy.addPracownik(dyrektorLocal);
                } catch (IllegalArgumentException e) {
                    view.displayError(e.getMessage());
                }
            }
        }

        public void intializeHandlowiec () {
            view.displayMessageNewLine("Podaj Pesel handlowca: ");
            String peselLocal = userInput.nextLine();

            view.displayMessageNewLine("Podaj imię handlowca: ");
            String firstName = userInput.nextLine();

            view.displayMessageNewLine("Podaj nazwisko handlowca: ");
            String secondName = userInput.nextLine();

            float wynagrodzenie = readFloat("Podaj wynagrodzenie handlowca: ");
            view.displayMessageNewLine("Podaj numer telefonu handlowca: ");
            String phoneNumber = userInput.nextLine();

            BigDecimal stawkaProwizji = readBigDecimal("Podaj stawkę prowizji handlowca: ");
            BigDecimal limitProwizjiMiesiac = readBigDecimal("Podaj limit prowizji miesięczny: ");

            view.displayMessageNewLine("[Enter] - zapisz");
            String validate = userInput.nextLine();
            if (validate.trim().isEmpty()) {
                try {
                    Handlowiec handlowiecLocal = new Handlowiec(peselLocal,
                            firstName,
                            secondName,
                            wynagrodzenie,
                            phoneNumber,
                            stawkaProwizji,
                            limitProwizjiMiesiac);
                    pracownicy.addPracownik(handlowiecLocal);
                } catch (IllegalArgumentException e) {
                    view.displayError(e.getMessage());
                }

            }
        }


        private float readFloat (String prompt){
            while (true) {
                try {
                    view.displayMessageNewLine(prompt);
                    return Float.parseFloat(userInput.nextLine());
                } catch (NumberFormatException nfe) {
                    view.displayMessageNewLine("Nieprawidłowy format. Wprowadź liczbę zmiennoprzecinkową.");
                }
            }
        }

        private BigDecimal readBigDecimal (String prompt){
            while (true) {
                try {
                    view.displayMessageNewLine(prompt);
                    return new BigDecimal(userInput.nextLine());
                } catch (NumberFormatException nfe) {
                    view.displayMessageNewLine("Nieprawidłowy format. Wprowadź poprawną liczbę.");
                }
            }
        }


    }


