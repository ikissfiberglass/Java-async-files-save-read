package Model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Handlowiec extends Pracownik {
    private BigDecimal stawkaProwizji;
    private BigDecimal limitProwizjiMiesiac;
    private Roles stanowisko = Roles.HANDLOWIEC;

    public void setStawkaProwizji(BigDecimal stawkaProcentowa){
        this.stawkaProwizji = stawkaProcentowa;
    }

    public Handlowiec(String pesel, String firstName, String secondName, float wynagrodzenie, String phoneNumber, BigDecimal stawkaProwizji, BigDecimal limitProwizjiMiesiac) {
        super(pesel, firstName, secondName, wynagrodzenie, phoneNumber);
        this.stawkaProwizji = stawkaProwizji;
        this.limitProwizjiMiesiac = limitProwizjiMiesiac;
        this.stanowisko = stanowisko;
    }

    @Override
    public String toString() {
        return "Identyfikator PESEL:             " + pesel + "\n" +
                "Imię:                            " + firstName + "\n" +
                "Nazwisko:                        " + secondName + "\n" +
                "Stanowisko:                      " + stanowisko + "\n" +
                "Wynagrodzenie:                   " + wynagrodzenie + "\n" +
                "Numer telefonu:                  " + phoneNumber + "\n" +
                "Stawka prowizji:                 " + stawkaProwizji.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "Limit prowizji miesięczny:       " + limitProwizjiMiesiac.setScale(2, RoundingMode.HALF_UP);
    }



}
