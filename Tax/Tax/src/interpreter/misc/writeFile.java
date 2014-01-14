package interpreter.misc;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class writeFile {

	String path;
	public writeFile(String path){
		this.path=path;
	}
	public void writeToText(String text){
		FileWriter write;
		try {
			write = new FileWriter(path);
			
			
			PrintWriter print = new PrintWriter(write);
			//print.printf("%s" + "%n",text);
			print.printf(text);

			print.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
