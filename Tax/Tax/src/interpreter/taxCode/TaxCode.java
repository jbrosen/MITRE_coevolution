package interpreter.taxCode;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaxCode {
	
	protected double annuityThreshold = 0;
	protected int childSalePrevention = 0;
	protected boolean annuityForMaterial = true;
	
	
	public TaxCode(){
		
	}
	
	public TaxCode(ArrayList<String> clauses) {
		this.createClauses(clauses);
	}
	
	public void setAnnuityForMaterial(boolean canSell) {
		this.annuityForMaterial = canSell;
	}
	
	public boolean getAnnuityForMaterial() {
		return this.annuityForMaterial;
	}
	
	public void setAnnuityThreshold(double annuityThreshold){
		this.annuityThreshold=annuityThreshold;
	}
	
	public double getAnnuityThreshold(){
		return annuityThreshold;
	}

	public void setChildSalePrevention(int numLoops) {
		this.childSalePrevention = numLoops;
	}
	
	public int getChildSalePrevention() {
		return this.childSalePrevention;
	}
	
	/*
	String tokenRegex = "([a-zA-Z0-9]+\\([0-9]+\\))";
	Pattern tokenPattern = Pattern.compile(tokenRegex);
	Matcher m = tokenPattern.matcher(s);
	//ArrayList<String> transactions = new ArrayList<String>();
	while(m.find()) {
		//System.out.println("now tokens: " + m.group());
		tmpClauses.add(m.group());
	}
	*/
	public void createClauses(ArrayList<String> clauses) {
		for (String s : clauses) {
			if (s.startsWith("setAnnuityThreshold")) {
				double thresh = Double.parseDouble(s.split("[()]")[1]);
				this.annuityThreshold = thresh/100.0;
			}
			else if (s.startsWith("setChildSalePrevention")) {
				int numLoops = Integer.parseInt(s.split("[()]")[1]);
				this.childSalePrevention = numLoops;
			}
			else if (s.startsWith("setAnnuityForMaterial")) {
				boolean canSell = Boolean.parseBoolean(s.split("[()]")[1]);
				this.annuityForMaterial = canSell;
			}
			
		}
	}
}
