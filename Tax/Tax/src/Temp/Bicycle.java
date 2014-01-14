package Temp;

import java.util.ArrayList;

public class Bicycle {
    
    private int cadence;
    private int gear;
    private int speed;
        
    // add an instance variable for the object ID
    private int id;
    
    // add a class variable for the
    // number of Bicycle objects instantiated
    public static ArrayList<Integer> number;
    public Bicycle(){
    	number = new ArrayList<Integer>();
    	number.add(1);
    	number.add(2);
    }
    
    public static ArrayList<Integer> getNumber(){
    	return number;
    }
    
    public static void setNumber(Integer i){
    	number.add(i);
    }
    
    
}