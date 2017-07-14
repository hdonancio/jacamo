/**
 * 
 */
package ora4mas.OrgArts;

import java.util.ArrayList;
import java.util.List;

import alice.cartago.Artifact;
//import alice.cartago.OBSPROPERTY;
import alice.cartago.OPERATION;

/**
 * @author kitio
 *
 */
public abstract class OrgArt extends Artifact{


	
	/*@OBSPROPERTY*/ String OrgArtName;
	/*@OBSPROPERTY*/ List <String> responsibleOrgAgents = new ArrayList <String>();
	
	 @OPERATION void init(){
	    	defineObsProperty("OrgArtName",OrgArtName);
	    	defineObsProperty("responsibleOrgAgents",responsibleOrgAgents);
	    	
	    	//OrgArtState OrgArtState = new OrgArtState();
	}
	
	
	/**
	 * 
	 * @OPERATION called to add a new agent in the list of OrgAgents of the artifact 
	 * @param agName
	 */
	@OPERATION protected void addResponsibleOrgAgents(String agName){
		responsibleOrgAgents.add(agName); 
	}
	
	
	/**
	 * 
	 * @OPERATION called to delete an agent in the list of OrgAgents of the artifact 
	 * @param agName
	 */
	@OPERATION protected void removeResponsibleOrgAgent(String agName){
		responsibleOrgAgents.remove(agName); 
	}

	/**
	 *  
	 * @return the list of all the OrgAgents of an artifact 
	 */
	public List<String> getResponsibleOrgAgents() {
		return responsibleOrgAgents;
	}

	/**
	 * 
	 * @return the name of the artifact
	 */
	public String getOrgArtName() {
		return OrgArtName;
	}


	/**
	 * assign the name set at the creation of the artifact 
	 * to the observable property "OrgArtName"
	 * @param orgArtName
	 */
	@SuppressWarnings("unused")
	protected void setOrgArtName(String orgArtName) {
		OrgArtName = orgArtName;
	}
	

	
}
