package Controller.Service;

import Model.PracownikRepository;

import java.util.InputMismatchException;

public class genericArray<T> {

    private PracownikRepository pracownikRepository;

    private T[] arr;

    public genericArray(int size){
        arr = (T[]) new Object[size];
    }

    public int getSize(){
        return arr.length;
    }

    public T[] getArr() {
        return arr;
    }

    public void setArr(T[] arr) {
        this.arr = arr;
    }

    public T getByIndex(int index){
        try{
            return arr[index];
        }catch(IndexOutOfBoundsException | InputMismatchException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

}
