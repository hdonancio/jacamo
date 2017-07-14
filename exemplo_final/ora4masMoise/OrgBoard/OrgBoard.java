/**
 * 
 */
package ora4masMoise.OrgBoard;

import java.util.*;

import ora4mas.OrgArts.OrgArt;
import ora4mas.OrgArts.OrgArtState;


import moise.os.*;
import moise.os.ds.*;
import moise.os.fs.*;
import moise.os.ss.*;
import moise.xml.DOMUtils;


import static ora4mas.OrgArts.Ora4masConstants.*;
import alice.cartago.*;

/**
 * @author kitio
 *
 */

@ARTIFACT_MANUAL(
		states = {"created", "error", "active", "wellFormed"},
		start_state = "created",
		
		oi = @OPERATING_INSTRUCTIONS(""),
		fd = @FUNCTION_DESCRIPTION("Organisational artifact where is registering the global" +
				"state of an organisation entity. This global state is constituted by:" +
				" the specification of the organization" +
				" the list of current OrgAgent(s) supervising the organisational entity (OE) " +
				" the list of current instances of GroupBoard created in the OE " +
				" the list of current instances of SchemeBoard created in the OE " +				
				" the list of membAgent(s) belonging in the OE ")
)

public class OrgBoard extends OrgArt{

	/**
	 * Observable Property 
	 */	
	
	OrgBoardControler myControler; 
	//static int orgBoardStatus;
	

	
	/*************************************************************************
	 * This operation set the initial value of the Org. Specification
	 * @param os is this value
	 **************************************************************************/
	@OPERATION void init(String orgSpecURI) {  
		
		myControler = new OrgBoardControler (orgSpecURI);
		
		defineObsProperty("orgBoardStatus", OrgArtState.created);	

	
	}
	
	@OPERATION void configure() {
		
		//this.setOrgArtName(this.getId().getName());
				
		//this.addResponsibleOrgAgents(this.getOpUserId().toString());		
		try {
			if (this.myControler.orgBoardSpecification != null) {				
				defineObsProperty("orgBoardSpecification", this.myControler.orgBoardSpecification);
			}
		}
		catch(Exception e) {
			updateObsProperty("orgBoardStatus", OrgArtState.error);
			signal(INIT_ORGBOARD_EVENT, this.getId().getName(), this.thisOpId.getOpName()+"_op", 
					"failed", "An exception occured during load of orgSpec XML file");
			log("Error durring load of OS ");
			e.printStackTrace();
		}
		
		HashMap<String, ArtifactId>  groupBoards = new HashMap<String, ArtifactId>();
		defineObsProperty("groupBoards", groupBoards  );
		
		HashMap<String, ArtifactId>  schemeBoards = new HashMap<String, ArtifactId>();
		defineObsProperty("schemeBoards", schemeBoards  );
				
		updateObsProperty("orgBoardStatus", OrgArtState.active);
		switchToState("active");	
		
		
	}	
	
	/***********************************************************************************
	 * This operation return the list of member agents of the organisation entity
	 * 
	 * @return a map where key are the name of each agent and the values are the 
	 * list of roles played by the agent, each role with the associate GroupBoard 
	 * in which it is played 
	 ************************************************************************************/	
	/*@SuppressWarnings("unchecked")
	@OPERATION(states={"active"})  Map<String, MemberAgentInstance> getMemberAgents(){
		Map<String, MemberAgentInstance> memberAgents = new HashMap<String, MemberAgentInstance>() ;
		
		for(ArtifactId grpBoardId:groupBoards.values()) {
			try {
				Map<String, Set<String>> result = (Map<String, Set<String>>)invokeOp(grpBoardId, 
						new Op(GetMemberOfGroupBoard_LinkOp, this.getOpUserName()));
				
				for(String agName: result.keySet()) {
					if(!memberAgents.containsKey(agName)) {
						MemberAgentInstance memAgInstance = null;
							/*new MemberAgentInstance(agName, 
								grpBoardId, result.get(agName));*/
						//memberAgents.put(agName, memAgInstance);
					//}
					/*else {
						memberAgents.get(agName).addRolePlayed(grpBoardId, result.get(agName));
					}*/
				/*}
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
	
	/**
	 * 
	 * @param grpSpecName
	 * @param retunGrpBoardId
	 */
	@LINK(states={"active"}) Group getGroupBoardSpecLinkOp(String grpSpecName){	
		
		return this.myControler.getGroupBoardSpec(grpSpecName);
		
	}
	
	/******************************************************************
	 * 
	 * @param grpBoardName
	 * 
	 ******************************************************************/
	@LINK(states={"active"}) Boolean existGroupBoardLinkOp(ArtifactId grpBoardId){		
		
		return this.myControler.existGroupBoard(grpBoardId);
	}	
	
	/************************************************************************
	 * This operation is provided to OrgAgents to register 
	 * the reference (Id) of a new instance of a GroupBoard
	 * within the OrgBoard 
	 * 
	 * @param grpBoardName
	 * @param grpBoardId	 
	 ************************************************************************/
	@LINK(states={"active"}) Boolean registerGroupBoardLinkOp(String grpBoardName, ArtifactId grpBoardId){
		
		if(this.myControler.registerGroupBoard(grpBoardName, grpBoardId)) {
			updateObsProperty(ORGBOARD_OBS_PROP2, myControler.groupBoards);
			return true;
		} 
		else 
		
		return false;
	}
	
	/*********************************************************************************************
	 * This operation is provided to OrgAgents to register 
	 * the reference (Id) of a new instance of a  SchemeBoard
	 * within the OrgBoard  	 
	 * 
	 * @param schBoardName
	 * @param schBoardId
	 ***********************************************************************************************/
	@LINK(states={"active"}) Boolean registerSchemeBoardLinkOp(String schBoardName, ArtifactId schBoardId){
		
		if(this.myControler.registerSchemeBoard(schBoardName, schBoardId)) {
			updateObsProperty(ORGBOARD_OBS_PROP3, myControler.schemeBoards);
			return true;
		} 
		else
		 return false;
	}
	
	/****************************************************************
	 * 
	 * @param schBoardId
	 * @return
	 ****************************************************************/
	@LINK(states={"active"}) Boolean existSchemeBoardLinkOp(ArtifactId schBoardId){		
		
		return this.myControler.existSchemeBoard(schBoardId);
	}
	
	/******************************************************************
	 * 
	 * @param schSpecName
	 * @param retunSchemeBoardId
	 *******************************************************************/
	@LINK(states={"active"}) Scheme getSchemeBoardSpecLinkOp(String schSpecName){	
		
		return this.myControler.getSchemeBoardSpec(schSpecName);
	}
	
	/*************************************************************
	 * 
	 * @return
	 **************************************************************/
	@LINK(states={"active"}) DS getNormativeBoardSpecLinkOp() {
		
		return this.myControler.getNormativeBoardSpec();
	}
			
	
	/****************************************************************************
	 * This linked operation is exposed to the GroupBoard 
	 * and is triggered when an agent adopt a new Role
	 * @param "op" equals "add" means that the update will be 
	 * an adding operation of a new agent or a new role of an existing agent
	 * When @param "op" equals "remove" will be a removing operation 
	 * a role of an existing agent
	 * 
	 * @param op
	 * @param agName
	 * @param r
	 * @param grpBoardName
	 ******************************************************************************/		
	/*@LINK(states={"active"}) void updateAgRoleInOrgBoard(String op, 
			String agName, Role r, String grpBoardName){
		
		if(op.equals("add")) {
			if( currentMemberAgents.containsKey(agName)){			
				currentMemberAgents.get(agName).addMemberAgRole(r, grpBoardName);
				/*signal(UpdateAgRoleInOrgBoard_LinkOp_EVENT,  this.getId().getName(),
						this.thisOpId.getOpName()+"_op", "succeeded", "agent name: " 
						+ agName + " new role: "+r.getId());*/
				//log("Linked Operation finished");
			/*}
			else{				
				MemberAgentInstance ag = new MemberAgentInstance(agName);
				ag.addMemberAgRole(r, grpBoardName);
				currentMemberAgents.put(agName, ag);
				/*signal(UpdateAgRoleInOrgBoard_LinkOp_EVENT,  this.getId().getName(), 
						this.thisOpId.getOpName()+"_op", "succeeded", "agent name: " 
						+ agName + " new role: " + r.getId());*/			
			/*}
		}
		if(op.equals("remove")) {
			if( currentMemberAgents.containsKey(agName)){			
				currentMemberAgents.get(agName).removeMemberAgRole(r, grpBoardName);
				/*signal("LinkOp_updateAgRole_ev",  this.getId().getName(), this.thisOpId.getOpName()+"_op", 
						"succeeded", "agent name: " +agName+ " new role: "+r.getId());*/
				//log("Linked Operation finished");
			/*}
			else{
			signal("LinkOp_updateAgRole_ev",  this.getId().getName(), 
    			this.thisOpId.getOpName()+"_op", "failed", "agent: " 
    			+ agName + " is not registered in the OrgBoard"); 
			
			}
		}
	}*/
	
	
	
	/**
	 * Registering of an OrgAgent within the Org. entity through the OrgBoard 
	 * this operation is provided to OrgAgents only 	 
	 */
	/*@OPERATION(states={"active"})  void registerOrgAgent(String orgAgName){
		OrgAgentInstance orgAgInstance = new OrgAgentInstance(orgAgName);
		Map<String, OrgAgentInstance> currentOrgAg = currentOrgAgents;
		currentOrgAg.put(this.getOpUserName(), orgAgInstance);
		
		signal("addNewOrgAgent_ev",  this.getId().getName(), 
    			this.thisOpId.getOpName()+"_op", "succeeded");
		
		updateProperty(ORGBOARD_OBS_PROP4, currentOrgAg);
		//log("Add new  OrgAgent operation finished. " + "Agent User Name " + orgAgName);
		
	}*/
	
	/**
	 * Remove an OrgAgent within the Org. entity through the OrgBoard 
	 * this operation is provided to OrgAgents only 	 
	 */
	/*@OPERATION(states={"active"})  void removeOrgAgent(String orgAgName){
		if(this.currentOrgAgents.containsKey(orgAgName)) {
			OrgAgentInstance orgAgInstance = new OrgAgentInstance(orgAgName);
			Map<String, OrgAgentInstance> currentOrgAg = currentOrgAgents;
			currentOrgAg.put(this.getOpUserName(), orgAgInstance);
			
			signal("addNewOrgAgent_ev",  this.getId().getName(), 
	    			this.thisOpId.getOpName()+"_op", "succeeded");
			
			updateProperty(ORGBOARD_OBS_PROP4, currentOrgAg);
		}
		else {
			signal("addNewOrgAgent_ev",  this.getId().getName(), 
	    			this.thisOpId.getOpName()+"_op", "failded", 
	    			"Agent "+ orgAgName +" is not registering " +
	    			"as an OrgAgent in this OrgBoard");
		}
		//log("Add new  OrgAgent operation finished. " + "Agent User Name " + orgAgName);
		
	}*/
	
	
	/**
	 * enter of an agent within the Org. entity through the OrgBoard 
	 * this operation is provided to all agents   	 
	 */
	/*@OPERATION  void enterOrg(){ //MemberAgent membAg
		if(currentMemberAgents.containsKey(this.getOpUserName()))
			signal(ENTER_ORG_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+"_op", "failed",
					"The agent is already registered in the organisation.");
		else {
			MemberAgentInstance membAgInstance = new MemberAgentInstance(this.getOpUserName());
			Map<String, MemberAgentInstance> currentMemberAg = new HashMap<String, MemberAgentInstance>();
			currentMemberAg = currentMemberAgents;
			currentMemberAg.put(this.getOpUserName(), membAgInstance);
			
			updateProperty(ORGBOARD_OBS_PROP5, currentMemberAg);
			signal(ENTER_ORG_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+"_op", "succeeded");
							
		}
	}*/
	
	
	/**  
	 * This operation is provided to all agents  
	 * to give up the Org. entity.  	 
	 */
	/*@OPERATION(states={"stateB"})  void leaveOrg(){
		if(currentMemberAgents.containsKey(this.getOpUserName())){
			currentMemberAgents.remove(this.getOpUserName());
			
			Map<String, MemberAgentInstance> currentMemberAg = currentMemberAgents;
			updateProperty(ORGBOARD_OBS_PROP5, currentMemberAg);		
			
			signal(LEAVE_ORG_EVENT,  this.getId().getName(), 
    			this.thisOpId.getOpName()+"_op", "succeeded");
		}else{
			signal(LEAVE_ORG_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+"_op", 
					"failed", "The agent " + this.getOpUserId() + " is not registered in the OrgBoard");
		}
	}*/
}


