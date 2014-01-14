package calculator;

import interpreter.assets.Assets;
import interpreter.entities.Entity;

import java.util.ArrayList;
import java.util.Iterator;
/*
 * calculates tax of each node in the graph
 * by calling calculateTax method on the Assets.
 * 
 * @author Osama Badar
 */
public class Calculator {
	
	ArrayList<Entity> nodes = new ArrayList<Entity>();
	public Calculator(ArrayList<Entity> nodes){
		this.nodes = (ArrayList<Entity>) nodes;
	}

	public void calculateTax(){
		/*Iterator<Entity> it = nodes.iterator();
		while(it.hasNext()){
			Entity e = it.next();
			double tax = 0;
			Iterator<Assets> i = e.getPortfolio().iterator();
			while(i.hasNext()){
				tax += i.next().calculateTax();
			}
			e.setTotalTax(tax);
		}*/
	}

	
}
