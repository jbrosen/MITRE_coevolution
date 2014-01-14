package interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class graphVisual {

	public graphVisual(){	
	}
	
	public void listFilesForFolder(final File folder) throws InterruptedException {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	String name = fileEntry.getName();
	        	if (name.indexOf(".") > 0)
	        	    name = name.substring(0, name.lastIndexOf("."));
	        	name =  fileEntry.getParentFile()+"/output/"+ name +".gif";
	            //System.out.println(name);
	            //System.out.println(fileEntry.getAbsolutePath());

	            Runtime rt = Runtime.getRuntime();
	            try {
					Process p = Runtime.getRuntime().exec("/usr/local/bin/dot -Tgif " + fileEntry.getAbsolutePath() + " -o " + name);
					//Process p = Runtime.getRuntime().exec("cat " + fileEntry.getAbsolutePath());

					p.waitFor();
					BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = "";

					while ((line=buf.readLine())!=null) {

						System.out.println(line);

			}
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
	}

	public void createAnimation(File folder) throws InterruptedException{
    	String n = "/opt/local/bin/convert -delay 200";
    	String o = " /Users/Badar/MITRE_ArtificialStepUpBasisTransactions/code/code/Tax/Tax/src/interpreter/dot/output/animation.gif";

		 for (final File fileEntry : folder.listFiles()) {
		    	String name = fileEntry.getName();
		    	if (name.indexOf(".") > 0)
		    	    name = name.substring(name.lastIndexOf("."),name.length());
				 	//System.out.println(name);

		       	if(name.equals(".gif"))
		       			n+=" " + fileEntry.getName();
		 }
		 n+=o;
		 System.out.println(n);
        Runtime rt = Runtime.getRuntime();
        try {
			Process p = Runtime.getRuntime().exec(n);

			p.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";

			while ((line=buf.readLine())!=null) {

				System.out.println(line);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		        
		    
	}

	
	
}
