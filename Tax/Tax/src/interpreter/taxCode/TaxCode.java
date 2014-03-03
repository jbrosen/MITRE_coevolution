package interpreter.taxCode;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaxCode {
	
	protected double annuityThreshold = 0;
	protected int childSalePrevention = 0;
	protected boolean annuityForMaterial = true;
	protected ArrayList<Integer> auditScoreSeeds = new ArrayList<Integer>();
	protected double materialForAnnuityAudit = 0.0;
	protected double singleLinkAudit = 0.0;
	protected double doubleLinkAudit = 0.0;
	
	
	public TaxCode(){
		
	}
	
	public TaxCode(ArrayList<String> clauses) {
		this.createClauses(clauses);
//		System.out.println(this.materialForAnnuityAudit+", "+this.singleLinkAudit+", "+this.doubleLinkAudit);
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
	
	public ArrayList<Integer> getAuditScoreSeeds() {
		return auditScoreSeeds;
	}

	public void setAuditScoreSeeds(ArrayList<Integer> auditScoreSeeds) {
		this.auditScoreSeeds = auditScoreSeeds;
	}

	public double getMaterialForAnnuityAudit() {
		return materialForAnnuityAudit;
	}

	public void setMaterialForAnnuityAudit(double materialForAnnuityAudit) {
		this.materialForAnnuityAudit = materialForAnnuityAudit;
	}

	public double getSingleLinkAudit() {
		return singleLinkAudit;
	}

	public void setSingleLinkAudit(double singleLinkAudit) {
		this.singleLinkAudit = singleLinkAudit;
	}

	public double getDoubleLinkAudit() {
		return doubleLinkAudit;
	}

	public void setDoubleLinkAudit(double doubleLinkAudit) {
		this.doubleLinkAudit = doubleLinkAudit;
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
//			System.out.println(s);
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
			else if (s.startsWith("AuditScores")) {
				String newS = s.split("\\(")[1].split("\\)")[0];
				String[] strSeeds = newS.split(",");
				double total = 0;
				for (int i = 0 ; i < strSeeds.length ; ++i) {
					this.auditScoreSeeds.add(Integer.parseInt(strSeeds[i]));
					total += Double.parseDouble(strSeeds[i]);
				}
				this.materialForAnnuityAudit = this.auditScoreSeeds.get(0)/total;
				this.singleLinkAudit = this.auditScoreSeeds.get(1)/total;
				this.doubleLinkAudit = this.auditScoreSeeds.get(2)/total;
				
			}
			
		}
	}
}
