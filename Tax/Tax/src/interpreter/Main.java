package interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Parser.Symbol;
import Parser.Type;

import calculator.Calculator;
public class Main {
	

	public static void main(String[] args) throws IOException, InterruptedException{
		graphVisual gv = new graphVisual();
//		final File folder = new File("/Users/Badar/MITRE_ArtificialStepUpBasisTransactions/code/code/Tax/Tax/src/interpreter/dot");		
//		final File folder1 = new File("/Users/Badar/MITRE_ArtificialStepUpBasisTransactions/code/code/Tax/Tax/src/interpreter/dot/output/");
		final File folder = new File("C:\\Users\\Jacob\\Documents\\MIT\\SCOTE\\code\\Tax\\Tax\\src\\interpreter\\dot");		
		final File folder1 = new File("C:\\Users\\Jacob\\Documents\\MIT\\SCOTE\\code\\Tax\\Tax\\src\\interpreter\\dot\\output\\");

		gv.listFilesForFolder(folder);
		gv.createAnimation(folder1);
		
		
		


	}
}
