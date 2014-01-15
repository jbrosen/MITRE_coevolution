package evogpj.Parser;
/***
 * Token consists of Terminals and Non-Terminals.
 * 
 * @author Badar
 *
 */
public class Symbol {

	private Type type = null;
	private String value = null;
	public Symbol(Type type,String value){
		this.type = type;
		this.value = value;
	}
	
	public Type getType(){
		return this.type;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Symbol)){
			return false;
		}
		else if(o == this){
			return true;
		}
		else{
			Symbol compare = (Symbol) o;
			if(compare.getType().equals(this.type) && compare.getValue().equals(this.getValue())){
				return true;
			}
		}
		return false;
	}
	
	public int hashCode()
	{
		int hash=7;
		for (int i=0; i < value.length(); i++) {
		    hash = hash*31+value.charAt(i);
		}
		return hash;
	}
	
}
