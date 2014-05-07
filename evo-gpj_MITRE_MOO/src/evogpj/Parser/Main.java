package evogpj.Parser;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws IOException{
		
		Parser p = new Parser();
		
		ArrayList<Integer> genotypeList = new ArrayList<Integer>();
		
		ArrayList<String> actions = p.getAction(genotypeList);
		
		for(int i=0;i<actions.size();i++){
			System.out.println("actions generated finally: " + actions.get(i));
		}
		System.out.println("DONE");
	}
}
