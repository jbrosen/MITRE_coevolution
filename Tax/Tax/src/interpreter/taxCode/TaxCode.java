package interpreter.taxCode;

public class TaxCode {
	
	protected double annuityThreshold = 0;
	
	public TaxCode(){
	}
	
	
	public void setAnnuityThreshold(double annuityThreshold){
		this.annuityThreshold=annuityThreshold;
	}
	
	public double getAnnuityThreshold(){
		return annuityThreshold;
	}

}

/*
//check to see the other asset is a share.
if(otherAsset.toString().equals("Share")){

	
	while(fromItr.hasNext()){
		fromAsset = fromItr.next();
		if(fromAsset.toString().equals("Cash")){
			double fvalue = fromAsset.getCurrentFMV() - asset.getCurrentFMV();
			if(fvalue >= 0){						
				from.setAssetToBeTransferred(fromAsset);
				from.setAssetToBeTransferredClone(new Cash((Cash) fromAsset));
				fromFound = true;
				break;
				}
			}
	}
	if(fromFound){
		double fvalue = fromAsset.getCurrentFMV() - asset.getCurrentFMV();
		fromAsset.setCurrentFMV(fvalue);
		fromAsset.setInsideBasis(fvalue);
		for(String s : fromAsset.getOwners().keySet()){
			fromAsset.getOwners().put(s, fvalue);
		}
		if(fvalue == 0){
			fromPortfolio.remove(fromAsset);
		}
	}
	else{
		//now check if we have multiple cash objects that can satisfy the price
	    double tempFmv = 0;
	    fromItr = fromPortfolio.iterator();
		while(fromItr.hasNext()){
			fromAsset = fromItr.next();
			if(fromAsset.toString().equals("Cash")){
				//System.out.println("NOW I AM HERE 1:"+fromAsset.getCurrentFMV());
				
				tempFmv  += fromAsset.getCurrentFMV();
				//tempAssets.add(fromAsset);
				if(tempFmv - asset.getCurrentFMV() >= 0){
					//System.out.println("NOW I AM HERE 2:"+(tempFmv - asset.getCurrentFMV()));
					fromFound = true;
					break;
				}
				
			
			}
		}
		if(fromFound){
			Cash newCash = new Cash(asset.getCurrentFMV());
			tempFmv = 0;
			for(int i=0;i<from.getPortfolio().size();i++){
				if(from.getPortfolio().get(i).toString().equals("Cash")){
					tempFmv  += from.getPortfolio().get(i).getCurrentFMV();
					if(tempFmv - asset.getCurrentFMV() > 0){
						//System.out.println("NOW I AM HERE 3:"+(tempFmv - asset.getCurrentFMV()));
						
					
						newCash.getOwners().put(from.getName(), asset.getCurrentFMV());
						double fvalue = tempFmv - asset.getCurrentFMV();
						from.getPortfolio().get(i).setCurrentFMV(fvalue);
						from.getPortfolio().get(i).setInsideBasis(fvalue);
						for(String s : from.getPortfolio().get(i).getOwners().keySet()){
							from.getPortfolio().get(i).getOwners().put(s, fvalue);
						}
					}else{
						
						from.getPortfolio().remove(i);									
					}
					
				
				}
			}
			from.setAssetToBeTransferred(newCash);
			from.setAssetToBeTransferredClone(new Cash(newCash));
		}
	
		
	}
}
*/