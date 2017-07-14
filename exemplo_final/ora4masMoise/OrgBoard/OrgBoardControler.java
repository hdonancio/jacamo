/**
 * 
 */
package ora4masMoise.OrgBoard;

import static ora4mas.OrgArts.Ora4masConstants.GetMemberOfGroupBoard_LinkOp;
import static ora4mas.OrgArts.Ora4masConstants.INIT_ORGBOARD_EVENT;
import static ora4mas.OrgArts.Ora4masConstants.ORGBOARD_OBS_PROP2;
import static ora4mas.OrgArts.Ora4masConstants.ORGBOARD_OBS_PROP3;

import ora4mas.OrgArts.OrgArtState;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import moise.os.OS;
import moise.os.ds.DS;
import moise.os.fs.Scheme;
import moise.os.ss.Group;
import moise.xml.DOMUtils;
import ora4mas.OrgArts.OrgArt;
import ora4masMoise.NormBoard.MemberAgentInstance;
import alice.cartago.*;


/**
 * @author kitio
 *
 */
public class OrgBoardControler {
	
	String orgSpecURI;
	OS  orgBoardSpecification = null;
	Map<String, ArtifactId>  groupBoards = new HashMap<String, ArtifactId>();
	Map<String, ArtifactId>  schemeBoards = new HashMap<String, ArtifactId>();
	OrgArtState orgBoardStatus; 
	
	
	
	/*************************************************************************
	 * Constructor
	 * @param osXmlFile 
	 **************************************************************************/

	public OrgBoardControler(String osXmlFile) {
		orgSpecURI = osXmlFile;
		orgBoardSpecification = OS.loadOSFromURI(orgSpecURI);	
		
	}
	
	
	/***********************************************************************************
	 * This operation return the list of member agents of the organisation entity
	 * 
	 * @return a map where key are the name of each agent and the values are the 
	 * list of roles played by the agent, each role with the associate GroupBoard 
	 * in which it is played 
	 ************************************************************************************/	
	/*@SuppressWarnings("unchecked")
	protected  Map<String, MemberAgentInstance> getMemberAgents(){
		Map<String, MemberAgentInstance> memberAgents = new HashMap<String, MemberAgentInstance>() ;
		
		for(ArtifactId grpBoardId:groupBoards.values()) {
			try {
				Map<String, Set<String>> result = (Map<String, Set<String>>)invoke(grpBoardId, 
						new Op(GetMemberOfGroupBoard_LinkOp, this.getOpUserName()));
				
				for(String agName: result.keySet()) {
					if(!memberAgents.containsKey(agName)) {
						MemberAgentInstance memAgInstance = null;
							/*new MemberAgentInstance(agName, 
								grpBoardId, result.get(agName));
						memberAgents.put(agName, memAgInstance);
					}
					else {
						memberAgents.get(agName).addRolePlayed(grpBoardId, result.get(agName));
					}
				}
			} catch (CartagoException e) {
				e.printStackTrace();
			}
		}
		
		return memberAgents;
	}*/
	
	
	/**
	 * 
	 * @param grpBoardName
	 */	
	/*@LINK(states={"active"}) void removeGroupBoard(String grpBoardName){
		if(groupBoards.containsKey(grpBoardName)) {
			Map<String, ArtifactId> currentGrp = groupBoards;
			currentGrp.remove(grpBoardName);
		
			signal(REMOVE_GROUPBOARD_EVENT,  this.getId().getName(), 
    			this.thisOpId.getOpName()+"_op", "succeeded");
				
			updateProperty("currentGroups", currentGrp);
		}
		else {
			signal(REMOVE_GROUPBOARD_EVENT,  this.getId().getName(), 
	    			this.thisOpId.getOpName()+"_op", "failed", 
	    			"This GroupBoard do not registered in the OrgBoard");
		}				
	}*/
	
	
	/**
	 * 
	 * @param schBoardName
	 */
	/*@LINK(states={"active"})  void removeSchemeBoard(String schBoardName){
		if(schemeBoards.containsKey(schBoardName)) {
			Map<String, ArtifactId> currentSch = schemeBoards;
			currentSch.remove(schBoardName);
			
			signal(REMOVE_SCHEMEBOARD_EVENT,  this.getId().getName(), 
	    			this.thisOpId.getOpName()+"_op", "succeeded");
			
			updateProperty("currentSchemes", currentSch);
		}
		else {
			signal(REMOVE_SCHEMEBOARD_EVENT,  this.getId().getName(), 
	    			this.thisOpId.getOpName()+"_op", "failed",
	    			"This SchemeBoard do not registered in the OrgBoard");
		}
	}*/
	

	
	
	/********************************************************************************************************************************
	 * 
	 * LINK OPERATIONS
	 * 
	 ********************************************************************************************************************************/    
	
	/*************************************
	 * 
	 * @param grpSpecName
	 * @param retunGrpBoardId
	 *************************************/
	protected Group getGroupBoardSpec(String grpSpecName){	
		Group group = null;
						
		if(orgBoardSpecification.getSS().getRootGrSpec().getFullId().equals(grpSpecName)) {
			group = orgBoardSpecification.getSS().getRootGrSpec();			
		}
		else {
			group = orgBoardSpecification.getSS().getRootGrSpec().findSubGroup(grpSpecName);				
		}
		
		return group;
	}
	
	/******************************************************************
	 * 
	 * @param grpBoardName
	 * 
	 ******************************************************************/
	protected Boolean existGroupBoard(ArtifactId grpBoardId){		
		Boolean returnValue = false;
		
		String grpBoardName = grpBoardId.getName();
		
		if(groupBoards.containsKey(grpBoardName) &&
				groupBoards.get(grpBoardName).equals(grpBoardId)) {			
			returnValue = true;
		}
		
		return returnValue; //Boolean.toString(returnValue);
	}	
	
	/************************************************************************
	 * This operation is provided to OrgAgents to register 
	 * the reference (Id) of a new instance of a GroupBoard
	 * within the OrgBoard 
	 * 
	 * @param grpBoardName
	 * @param grpBoardId	 
	 ************************************************************************/
	protected Boolean registerGroupBoard(String grpBoardName, ArtifactId grpBoardId){
		Boolean returnValue =true;
		
		try {
			groupBoards.put(grpBoardName, grpBoardId);
			 	
		} catch (Exception e) {
			returnValue = false; 
			//e.printStackTrace();
		}
		
		return returnValue;
	}
	
	/*********************************************************************************************
	 * This operation is provided to OrgAgents to register 
	 * the reference (Id) of a new instance of a  SchemeBoard
	 * within the OrgBoard  	 
	 * 
	 * @param schBoardName
	 * @param schBoardId
	 ***********************************************************************************************/
	protected Boolean registerSchemeBoard(String schBoardName, ArtifactId schBoardId){
		boolean returnValue =true;
		
		try {
			schemeBoards.put(schBoardName, schBoardId); 		
		} 
		catch (Exception e) {	
			returnValue = false;
			//e.printStackTrace();
		}	
		
		return returnValue;
	}
	
	/****************************************************************
	 * 
	 * @param schBoardId
	 * @return
	 ****************************************************************/
	protected Boolean existSchemeBoard(ArtifactId schBoardId){		
		boolean returnValue = false;		
		String schBoardName = schBoardId.getName();
		
		if(schemeBoards.containsKey(schBoardName) &&
				schemeBoards.get(schBoardName).equals(schBoardId)) {			
			returnValue = true;
		}
		
		return returnValue; //Boolean.toString(returnValue);
	}
	
	/******************************************************************
	 * 
	 * @param schSpecName
	 * @param retunSchemeBoardId
	 *******************************************************************/
	protected Scheme getSchemeBoardSpec(String schSpecName){	
		Scheme scheme = null;
		try {
			scheme = this.orgBoardSpecification.getFS().findScheme(schSpecName);	
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		return scheme;
	}
	
	/*************************************************************
	 * 
	 * @return
	 **************************************************************/
	protected DS getNormativeBoardSpec() {
		
		return this.orgBoardSpecification.getDS();
	}


}
