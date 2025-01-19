package Model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Dyrektor extends Pracownik{
    private BigDecimal dodatekSluzbowy;
    private String kartaSluzbowa;
    private float limitKosztowMiesiac;
    private Roles stanowisko = Roles.DYREKTOR;

    public Dyrektor(String pesel, String firstName, String secondName, float wynagrodzenie, String phoneNumber, BigDecimal dodatekSluzbowy, String kartaSluzbowa, float limitKosztowMiesiac) {
        super(pesel, firstName, secondName, wynagrodzenie, phoneNumber);

        this.dodatekSluzbowy = dodatekSluzbowy;
        this.kartaSluzbowa = kartaSluzbowa;
        this.limitKosztowMiesiac = limitKosztowMiesiac;

    }

    @Override
    public String toString() {
        return "Identyfikator PESEL:             " + pesel + "\n" +
                "Imię:                            " + firstName + "\n" +
                "Nazwisko:                        " + secondName + "\n" +
                "Stanowisko:                      " + stanowisko + "\n" +
                "Wynagrodzenie:                   " + wynagrodzenie + "\n" +
                "Numer telefonu:                  " + phoneNumber + "\n" +
                "Dodatek służbowy:                " + dodatekSluzbowy.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "Karta służbowa:                  " + kartaSluzbowa + "\n" +
                "Limit kosztów miesięcznych:      " + limitKosztowMiesiac;
    }



}
