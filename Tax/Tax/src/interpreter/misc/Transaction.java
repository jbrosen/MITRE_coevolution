package interpreter.misc;

public class Transaction {
	
	private Actions action1;
	private Actions action2;
	
	private double auditScore;

	public Transaction(Actions action1, Actions action2){
		this.action1 = action1;
		this.action2 = action2;
	}
	
	public Actions getAction1(){
		return action1;
	}
	public Actions getAction2(){
		return action2;
	}
	
	public String toString(){
		return "Action1: " + action1.toString() + " and Action2: " + action2.toString();
	}
	
	
	public double getAuditScore() {
		return this.auditScore;
	}
	
	public void setAuditScore(double auditScore) {
		this.auditScore = auditScore;
	}
	
}
