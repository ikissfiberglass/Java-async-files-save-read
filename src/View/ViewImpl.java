package View;

import java.io.Serializable;

public class ViewImpl implements Serializable{
    public ViewImpl() {    }

    public void displayMessageNewLine(String message){System.out.println(message);}

    public void displayMessage (String message){System.out.print(message);}

    public void displayError(String errorMessage){System.err.println(errorMessage);}

}
