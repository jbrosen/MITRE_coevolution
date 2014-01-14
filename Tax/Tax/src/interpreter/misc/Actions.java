package interpreter.misc;

import interpreter.assets.Assets;


/*
 * Defines an action object that needs  
 * to be performed between two entities.
 * @author Osama Badar
 */
public class Actions {
	
	private String from;
	private String to;
	private Assets transferableAsset;

	public Actions(String from,String to, Assets transferableAsset){
		this.from = from;
		this.to = to;
		this.transferableAsset = transferableAsset;
	}
	
	public String getFrom(){
		return from;
	}
	public String getTo(){
		return to;
	}
	public Assets getTransferableAssets(){
		return transferableAsset;
	}
	public String toString(){
		return "from: " + from + " to: " + to + " Asset: " + getTransferableAssets().toString();
	}
}
