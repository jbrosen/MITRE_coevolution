package interpreter.misc;

import interpreter.assets.Assets;
import interpreter.entities.Entity;
import interpreter.taxCode.TaxCode;
import interpreter.transfers.AssetToAsset;
import interpreter.transfers.FromAsset;
import interpreter.transfers.FromToAsset;
import interpreter.transfers.ToAsset;

import java.util.ArrayList;
import java.util.Iterator;

import evogpj.algorithm.Parameters;

public class Transfer_NEO {
	
	private Graph graph;
	private ArrayList<Entity> nodes; 
	private TaxCode taxCode;
	private boolean isTaxable = true;
	private boolean verbose = Parameters.Defaults.VERBOSE;
	private AssetToAsset ata;
	private FromAsset fa;
	private ToAsset ta;
	private FromToAsset fta;
	
	public Transfer_NEO(Graph graph, TaxCode taxCode) {
		this.graph = graph;
		this.nodes = this.graph.getNodes();
		this.taxCode = taxCode;
		ata = new AssetToAsset(this.graph, this.taxCode);
		fa = new FromAsset(this.taxCode);
		ta = new ToAsset(this.taxCode);
		fta = new FromToAsset(this.taxCode);
	}
	
	public boolean doTransfer(Transaction transaction) {
		this.isTaxable = true;
//		check if legal
		if(isLegal(transaction)){
			if (this.verbose) {
				System.out.println("___________TRANSACTION IS LEGAL____________");
			}
			//System.exit(0);

			transferAction(transaction.getAction1(),transaction.getAction2());
			transferAction(transaction.getAction2(),transaction.getAction1());
			return true;
		}
		else{
			if (this.verbose) {
				System.out.println("___________TRANSACTION IS ILLEGAL____________");
			}
			return false;
		}
		
	}
	
	
	public void transferAction(Actions action1,Actions action2){
		Entity from = getFrom(action1);
		Entity to = getTo(action1);
		if(this.isTaxable){
			from.getAssetToBeTransferred().calculateTax(from);
		}
		action1.getTransferableAssets().transfer(from, to, action2.getTransferableAssets());
	}
	
	
	public boolean isLegal(Transaction transaction) {
		Actions action1 = transaction.getAction1();
		Actions action2 = transaction.getAction2();
		Assets asset1 = action1.getTransferableAssets();
		Assets asset2 = action2.getTransferableAssets();
		
//		kludgy, fix later. determines if the transaction is taxable
		if (asset1.toString() == "Annuity" || asset2.toString() == "Annuity")
			this.isTaxable = false;
		if (asset1.toString() == "Share" || asset2.toString() == "Share")
			this.isTaxable = false;
		
		/*
		 * NOTE
		 * need some way to implicitly check the FMV of a PartnershipAsset before checking
		 * FROM's portfolio for what it's actually worth
		 */
		
		
		
//		can the two assets be exchanged?
		if (!ata.canTransferAssets(asset1, asset2))
			return false;
		
		Entity entity1 = getFrom(action1);
		Entity entity2 = getFrom(action2);
		
//		can entity2 receive asset1? can entity1 receive asset2?
		if (!(ta.canReceive(entity2, asset1) && ta.canReceive(entity1, asset2)))
			return false;
		
//		More specifically, can entity1 give asset1 to entity2
//		and can entity2 give asset2 to entity1
		if (!(fta.canBeTransferred(entity1, entity2, asset1) && fta.canBeTransferred(entity2, entity1, asset2)))
			return false;
		
		
//		can entity1 give away asset1? can entity2 give away asset2?
		Assets transferredAsset1 = fa.canGive(entity1, asset1);
		Assets transferredAsset2 = fa.canGive(entity2, asset2);
		
		if (transferredAsset1 == null || transferredAsset2 == null) {
			return false;
			}
		

		
		return true;
	}
	
	
	
	/*
	 * @param action object that stores information about the action
	 * that needs to be performed.
	 * @return Entity from which the action needs to be performed.
	 */
	private Entity getFrom(Actions action){
		Entity from = null;
		Iterator<Entity> itr = nodes.iterator();
		while(itr.hasNext()){
			 from =  itr.next();
			 if (from.getName().equals(action.getFrom())){
				 break;
			 }
		}
		return from;
	}
	/*
	 * @param action object that stores information about the action
	 * that needs to be performed.
	 * @return Entity to which the action needs to be performed.
	 */
	private Entity getTo(Actions action){
		Entity to = null;
		Iterator<Entity> itr = nodes.iterator();
		while(itr.hasNext()){
			 to =  itr.next();
			 if (to.getName().equals(action.getTo())){
				 break;
			 }
		}
		return to;
	}
	
	
}
