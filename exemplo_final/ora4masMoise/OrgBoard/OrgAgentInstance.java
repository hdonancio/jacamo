/**
 * 
 */
package ora4masMoise.OrgBoard;

import java.util.ArrayList;
import java.util.List;

import ora4mas.OrgArts.OrgArt;

/**
 * @author kitio
 *
 */
public class OrgAgentInstance {
	
	private String orgAgName;
	private List<OrgArt> responsibleOrgArts = new ArrayList<OrgArt>();
	/**
	 * 
	 */
	public OrgAgentInstance(String agName) {
		orgAgName = agName;
		
	}
	
	/**
	 * @return the list of OrgArts that the OrgAgent is responsible within the OE 
	 * 
	 */
	public List<OrgArt> getResponsibleOrgArts(){
		return responsibleOrgArts;
	}
	
	public void addResponsibleOrgArt(OrgArt orgArt) {
		responsibleOrgArts.add(orgArt);
	}
	
	public String getAgName(){
		return orgAgName;
	}			
}


