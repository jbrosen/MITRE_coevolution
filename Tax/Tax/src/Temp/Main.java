package Temp;
import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


public class Main {

	public static void main(String args[]) {
	
			HashMap<String, Integer> map1 = new HashMap<String, Integer>();
			
			map1.put("Osama", 1);
			map1.put("Badar", 4);
			
			System.out.println("MAP IS:" + map1);
			
			HashMap<String,Integer> map2 = (HashMap<String, Integer>) map1.clone();
			
			System.out.println("MAP IS:" + map2);

		}
		
		
			   

	
}