/**
 * 
 */
package ora4masMoise.GroupBoard;

import java.util.*;

import static ora4mas.OrgArts.Ora4masConstants.*;
import ora4mas.OrgArts.OrgArt;
import ora4mas.OrgArts.OrgArtState;
//import static ora4mas.OrgArts.OrgArtState.*;
import ora4masMoise.SchemeBoard.MissionInstance;


import moise.os.CardinalitySet;
import moise.os.ss.*;
import moise.xml.DOMUtils;
//import moisePlusOrgArts.OrgBoard.MemberAgentInstance;
import alice.cartago.*;

/**
 * @author kitio
 *
 */

@ARTIFACT_MANUAL(
		states = {"created", "error", "active", "wellFormed"},
		start_state = "created",
				
		oi = @OPERATING_INSTRUCTIONS("instructions."),
		fd = @FUNCTION_DESCRIPTION("functions.")
)

public class GroupBoard extends OrgArt{
	
	GroupBoardControler myControler; 
	
	@OPERATION void init() {  
					
		
		Map<String, Integer> playableRoles = new HashMap<String, Integer>();
		defineObsProperty("playableRoles", playableRoles );	
		
		Map<String, Set<String>> playersOfRole = new HashMap<String, Set<String>>();
		defineObsProperty("playersOfRole", playersOfRole );	
		
		Set<ArtifactId> superGroupBoards = new HashSet<ArtifactId>();
		defineObsProperty("superGroupBoards", superGroupBoards );	
		
		Map<String, List<ArtifactId>>  subGroupBoards = new HashMap<String, List<ArtifactId>>();
		defineObsProperty("subGroupBoards", subGroupBoards );	
		
		Set<ArtifactId> dependentGroupBoards = null;
		defineObsProperty("dependentGroupBoards", dependentGroupBoards );	
		
		Set<ArtifactId> schemeBoards = new HashSet<ArtifactId>(); 
		defineObsProperty("schemeBoards", schemeBoards );	
		
		Set<ArtifactId> normativeBoards = new HashSet<ArtifactId>(); 
		defineObsProperty("normativeBoards", normativeBoards );			
		
		defineObsProperty("groupBoardStatus", OrgArtState.created);	
	}
	
	//
	
	@OPERATION void configure(Object orgBoardName, Object grpSpecName, Object superGrpBoardName){ 
		
			
		if (orgBoardName != null && grpSpecName != null) {
			ArtifactId orgBoardId = null, superGrpBoardId = null;	
		    
	   	   
		   try {
		    	ArtifactId registry = getRegistryId(); 
		    	ArtifactId[] list = (ArtifactId[])invokeOp(registry, new Op("_getCurrentArtifactsIds"));
		    	
		    	for (ArtifactId elt: list){
			    	if(elt.getName().equals(orgBoardName))
			    		orgBoardId = elt; 
			    	
				    else if(elt.getName().equals(superGrpBoardName))
					  superGrpBoardId = elt;
			    }
		    	
		   } catch (Exception ex){
			  signal(INIT_GROUPBOARD_EVENT,  this.getId().getName(),"error in setting referenced artifacts");
			  ex.printStackTrace();
		   }
		
		   myControler = new GroupBoardControler (orgBoardId, grpSpecName.toString(), superGrpBoardId);
		   
		   defineObsProperty("groupBoardType", myControler.groupBoardType );			
			
		   defineObsProperty("orgBoardId", myControler.orgBoardId );		
			
		   initGroupBoard();
		}
		else
			 signal(INIT_GROUPBOARD_EVENT,  this.getId().getName(),"error : null configuration parameters");
			
	}
	
	/***********************************************************************
	 * This operation initialize the GroupBoard by  setting
	 * its the group-Spec and the name of the group
	 * 
	 * @param n
	 * @param grp
	 ***********************************************************************/
	private void initGroupBoard() { //@OPERATION(states={"created"})
		
		try {
			Group grp = (Group)invokeOp(this.myControler.orgBoardId, new 
					Op(GetGroupBoardSpecInOrgBoard_LinkOp, this.myControler.groupBoardType));			
			
			if(!grp.equals(null)){
				this.myControler.setGroupBoardSpec(grp);	
				defineObsProperty("groupBoardSpecification", this.myControler.groupBoardSpecification);
				
				initGroupBoardStep2();
			}
			else{
				signal(INIT_GROUPBOARD_EVENT,  this.getId().getName(), 
						this.thisOpId.getOpName()+"_op", "failed", "There is no Group named \"" 
						+ this.myControler.groupBoardType + "\" in the OS of the OrgBoard \"" 
						+ this.myControler.orgBoardId.getName()+"\"");			
			}	
			
		} catch (Exception e) {			
			e.printStackTrace();
		}	
		
		
	}
	
	
	/***************************************************************
	 * 
	 * init GroupBoard Step2
	 * 
	 * if the group specification managed in the GroupBoard is the 
	 *  root group specification, switch to step 4
	 * else
	 *  trigger the registering of the GroupBoard in its super 
	 *  GroupBoard and switch in step 3
	 *
	 ***************************************************************/
	
	private void initGroupBoardStep2(){ 
		if(!this.myControler.groupBoardSpecification.getRootGrSpec().equals(this.myControler.groupSpec)) { 
			
			if(!this.myControler.firstSuperGroupBoardId.equals(null)) { 				
				try {
					Boolean checkGroupBoardIdExitence = (Boolean)invokeOp(this.myControler.orgBoardId, 
							new Op(ExistGroupBoardIdInOrgBoard_LinkOp, this.myControler.firstSuperGroupBoardId)); 
					
					
					if(checkGroupBoardIdExitence) {
						
						this.myControler.superGroupBoards.add(this.myControler.firstSuperGroupBoardId);
						updateObsProperty("superGroupBoards", this.myControler.superGroupBoards);
						
						initGroupBoardStep3();
					}
					else {
						signal(INIT_GROUPBOARD_EVENT,  this.getId().getName(), 
				    			this.thisOpId.getOpName()+"_op", "failed",
				    			"The provided super GroupBoard does not exist");
					}
				}catch(Exception e) {
					e.printStackTrace();	
				}
			}
			else {
				signal(INIT_GROUPBOARD_EVENT,  this.getId().getName(), 
		    			this.thisOpId.getOpName()+"_op", "failed",
		    			"The group specification is not the root group " +
		    			"Thus, the name of the superGroup can not be null");
			}
		}
		else {	
			if(this.myControler.initSubGroupBoard())
				updateObsProperty("subGroupBoards", this.myControler.subGroupBoards);
			
			initGroupBoardStep4();
			
		}	
		
	}
	
	/***************************************************************
	 * 
	 * init GroupBoard Step3
	 *
	 ***************************************************************/
	
	private void initGroupBoardStep3() {
		
		if(this.myControler.superGroupBoards.contains(this.myControler.firstSuperGroupBoardId)) { 
			try {
				
				Boolean registerInSuperGroupBoardResult = (Boolean)invokeOp(this.myControler.firstSuperGroupBoardId, 
						new Op(AddSubGroupBoard_LinkOp, this.getId(), this.myControler.groupSpec));
				
				if(registerInSuperGroupBoardResult) {
					this.myControler.initSubGroupBoard();
					updateObsProperty("subGroupBoards", this.myControler.subGroupBoards);
					
					initGroupBoardStep4();
				}
				else {
					signal(INIT_GROUPBOARD_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+ 
						"_op", "failed", "The max cardinality of sub-group named \"" + 
						" reached for the firstSuperGroupBoardId " + this.myControler.firstSuperGroupBoardId);
				}
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			signal(INIT_GROUPBOARD_EVENT,  this.getId().getName(), 
					this.thisOpId.getOpName()+"_op", "failed", 
					"The super GroupBoard named \"" + this.myControler.firstSuperGroupBoardId 
					+ "\" is not yet created or registered in the OrgBoard \"" 
					+ this.myControler.orgBoardId.getName()+"\"");		
		}		
	}
	
	/***************************************************************
	 * 
	 * init GroupBoard Step4
	 *
	 ***************************************************************/
	
	private void initGroupBoardStep4() { 
		try {
			Boolean registerInOrgBoardResult = (Boolean)invokeOp(this.myControler.orgBoardId,
				new Op(RegisterGroupBoard_LinkOp, this.getId().getName(), this.getId()));
										
			if(registerInOrgBoardResult)
				initGroupBoardStep5();
			else {
				signal(INIT_GROUPBOARD_EVENT, this.getId().getName(), 
			    		this.thisOpId.getOpName()+"_op", "failed", 
			    		"The registering in the OrgBoard failed");					
			}
					 
		} catch (Exception e) {	
			signal(INIT_GROUPBOARD_EVENT, this.getId().getName(), this.thisOpId.getOpName()+ 
				"_op", "failed", "The registering in OrgBoard failed" );
						
			e.printStackTrace();
		}			
			
	}
	
	

	/***************************************************************
	 * 
	 * init GroupBoard Step5
	 *
	 ***************************************************************/
	
	private void initGroupBoardStep5() { 
		
		this.myControler.initGroupBoardRoles(this.getId());		
		updateObsProperty("playableRoles", this.myControler.playableRoles);
		updateObsProperty("playersOfRole", this.myControler.playersOfRole);
		//int status  = ;
		updateObsProperty("groupBoardStatus", OrgArtState.active); 
		switchToState("active");
		
		//this.addResponsibleOrgAgents(this.getOpUserId().toString());
		signal(INIT_GROUPBOARD_EVENT,  this.getId().getName(), 
    			this.thisOpId.getOpName()+"_op", "succeeded");		
		
		
	}
	
		
	
	/*************************************************************************
	 * This operation is provided to member agents  
	 * to adopt a role in an instance of a GroupBoard  	 
	 * 
	 * @param rName 
	 **************************************************************************/
	@OPERATION(states={"active"}) void adoptRole(String rName){	
		 
		//log("Operation Adopt Role Started");
		
		if (!this.myControler.playableRoles.containsKey(rName)) {	        
	        try {
	            signal(ADOPT_ROLE_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+"_op",  
	            		rName ,"failed",  "\"the role does not exist in the group specification\" " +
	            				"or \"its max Cardinality reached\" or \"the role is abstract\"");
				//throw new MoiseConsistencyException("the role "+rName+" does not exist in group.");					
	        } catch (Exception e) {
				e.printStackTrace();
			}
	    }else {
	    	Role role = this.myControler.groupBoardSpecification.getRoleDef(rName);
	    	if (checkMembAgAlreadyPlaysRole(this.getOpUserName(), rName)) { 	    	
	    		signal(ADOPT_ROLE_EVENT, this.getId().getName(), 
	    				this.thisOpId.getOpName() + "_op", rName ,"failed", 
	            		"agent " +this.getOpUserName()+ " already plays the role: "+ rName);				
	    		
	    	} 	    	
	    	else {	   
	    		boolean compatibilityValue = checkIntraGroupCompatibility(this.getOpUserName(), role);
	    		
	    		if(compatibilityValue == true) {
	    			updateRolesOfPlayer("add", this.getOpUserName(), role);
	    			//updateAgentRoleInLinkOrgArts("add", this.getOpUserName(), role);	
	    			
	    			
	    			if(this.myControler.playableRoles.get(rName)>1) {	    				
	    				int newMaxCardinality = this.myControler.playableRoles.get(rName) - 1;
	    				this.myControler.playableRoles.put(rName, newMaxCardinality);	  
	    				
	    			}
	    			else {
	    				this.myControler.playableRoles.remove(rName);	    				
	    			}
	    			
	    			updateObsProperty(GROUPBOARD_OBS_PROP3, this.myControler.playableRoles);
	    			
	    			Iterator<ArtifactId> it = this.myControler.normativeBoards.iterator();
	    			ArtifactId id;	    			
	    			while(it.hasNext()) {
	    				id = it.next();	    			
		    			try {
							invokeOp(id, new Op(UpdateAgentStatusFromGroupBoard_LinkOp,
									this.getOpUserName(), rName, this.getId(), ADOPT_ROLE_OP));
						} catch (CartagoException e) {
							e.printStackTrace();
						}
	    			}
	    			
	    			if(this.myControler.groupBoardStatus == OrgArtState.wellFormed) {
	    				updateObsProperty("groupBoardStatus", OrgArtState.wellFormed); 
	    			}
	    			
	    			signal(ADOPT_ROLE_EVENT, this.getId().getName(), this.thisOpId.getOpName()
	    					+ "_op", rName, "succeeded",this.getOpUserName()); 	
	    			
	    			
	    		}
	    	}	   
	    }
	}
	
	
	/************************************************************************
	 * This operation is provided to member agents  
	 * to give up a role within an instance of a GroupBoard  	 
	 * 
	 * @param rName 
	 ************************************************************************/
	@OPERATION(states={"active"}) void leaveRole(String rName){
		
		if(this.myControler.playersOfRole.containsKey(rName)) {
			Role role = this.myControler.groupSpec.getSS().getRoleDef(rName);
			
			if(this.myControler.playersOfRole.get(rName).contains(this.getOpUserName())){			
				updateRolesOfPlayer("remove", this.getOpUserName(), role);
				//updateAgentRoleInLinkOrgArts("remove",this.getOpUserName(), role);
			
				
				if(this.myControler.playableRoles.containsKey(rName)){
					int newMaxCardinality = this.myControler.playableRoles.get(rName) + 1;
					this.myControler.playableRoles.put(rName, newMaxCardinality);
					updateObsProperty(GROUPBOARD_OBS_PROP3, this.myControler.playableRoles);
				}
				else {
					this.myControler.playableRoles.put(rName, 1);
					updateObsProperty(GROUPBOARD_OBS_PROP3, this.myControler.playableRoles);
				}
				Iterator<ArtifactId> it = this.myControler.normativeBoards.iterator();
    			ArtifactId id;	    			
    			while(it.hasNext()) {
    				id = it.next();	    			
					try {
						invokeOp(id, new Op(UpdateAgentStatusFromGroupBoard_LinkOp,
									this.getOpUserName(), rName, this.getId(), LEAVE_ROLE_OP));
					} catch (CartagoException e) {
						e.printStackTrace();
					}					
    			}
    			
    			if(this.myControler.groupBoardStatus == OrgArtState.active) {
    				updateObsProperty("groupBoardStatus", OrgArtState.active); 
    			}
    			
				signal(LEAVE_ROLE_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+"_op", 
						rName, "succeeded", this.getOpUserName());	
			}	
			else {
				signal(LEAVE_ROLE_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+"_op", 
						rName, "failed", "this member agent do not plays the role: " + rName, 
						this.getOpUserName());
			}
		}
		else {
			signal(LEAVE_ROLE_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+ "_op", 
					rName, "failed", "the role " + rName + " does not exist in " + 
					"this GroupBoard", this.getOpUserName() );		
		}
		//log("op_leaveRole_completed");		
	}
	
	
	
	/*****************************************************************************
	 * Private method used to check the intra-group compatibility of a role
	 * 
	 * @param agName
	 * @param newRole	
	 ******************************************************************************/	
	private boolean checkIntraGroupCompatibility(String agName, Role newRole){
		
		if(!this.myControler.checkIntraGroupCompatibility(agName, newRole)) {
        			
        	signal(ADOPT_ROLE_EVENT, this.getId().getName(), 
        			this.thisOpId.getOpName()+"_op", "failed", "the role " + newRole.getFullId() + 
        			" is not compatible with another role played by " + agName +
    				" in the group " + this.myControler.groupSpec.getFullId());					
        		        		
			return false;
        }
		else 
			return true;
    }
	
	/********************************************************************************
	 * Private method used to check if the GroupBoard is well formed
	 * 
	 * @return "true" if for all the roles of the Group the min cardinality required 
	 * for the role reach and "false" if no
	 *********************************************************************************/
	private boolean checkGroupBoardWellFormed() {
    	boolean result = true;
    	Iterator<RoleInstance> roleInstanceIt = this.myControler.rolesInstance.values().iterator();
    	
    	while(result && roleInstanceIt.hasNext())
    		if(!roleInstanceIt.next().minCardinalityReached())
    			result = false;
    	
    	return result;
    }
    
	/**
	 * 
	 * @param newRole
	 * @throws MoiseConsistencyException
	 */
    /*private void interGroupCompatibilityCheck(Role newRole) throws MoiseConsistencyException {
        //Collection<Compatibility> newRoleCompats  = newRole.getCompatibilities(group);
        
        // all the current roles
        Iterator<RoleInstance> curRoles = playersOfRole.values().iterator();
        while (curRoles.hasNext()) {
            
           
        }
    }*/	
    
    
    /********************************************************************************
     * Private method used to check if an agent already plays a role
     * 
     * @param agName
     * @param rName
     * @return "true" if the member agent already plays the role named rName 
     * otherwise return "false"
     *********************************************************************************/
    private boolean checkMembAgAlreadyPlaysRole(String agName, String rName){
    	 
    	if(this.myControler.playersOfRole.get(rName).contains(agName))
    		return true;
    	else
    		return false;
    }
    
    /**************************************************************************************
     * Update the list of role that the agent named agName plays within the GroupBoard
     * 
     * @param op : the type of the update operation which can be "add" or "remove"
	 * @param agName : the name of the agent concerned for the update
	 * @param r : the concerned role
     ***************************************************************************************/
    private void updateRolesOfPlayer(String op, String agName, Role r){
    	this.myControler.updateRolesOfPlayer(op,agName, r);
    	updateObsProperty(GROUPBOARD_OBS_PROP4, myControler.playersOfRole);
    	//System.out.println("add role player finished");
    }
    

    
    
/********************************************************************************************************************************
 * 
 * LINK OPERATIONS
 * 
 ********************************************************************************************************************************/    
    
	
    /**************************************************************************************
     * 
     * @param schBoardName
     * @param schBoardId
     ***************************************************************************************/
    @LINK(states={"active"}) void addSchemeBoardLinkOp(ArtifactId schBoardId){
    	if(this.myControler.addSchemeBoard(schBoardId)) {
    		
	    	updateObsProperty(GROUPBOARD_OBS_PROP6, this.myControler.schemeBoards);
	    	
	    	signal(ADD_RESP_SCHEMEBOARD_LinkOp_EVENT,  this.getId().getName(), 
	    			this.thisOpId.getOpName()+"_op", "succeeded");
    	}
    	else {
    		signal(ADD_RESP_SCHEMEBOARD_LinkOp,  this.getId().getName(), 
    				this.thisOpId.getOpName()+"_op", "failed", "this SchemeBoard " 
    				+ schBoardId + "is already set as responsible of this GroupBoard");
    	}
    }
	
    /**************************************************************************************
     * 
     * 
     * @param schBoardId
     ***************************************************************************************/
    @LINK(states={"active"}) void removeSchemeBoardLinkOp(ArtifactId schBoardId){
    	
    	if(this.myControler.removeSchemeBoard(schBoardId)) {
    		updateObsProperty(GROUPBOARD_OBS_PROP6, this.myControler.schemeBoards);
    		signal(REMOVE_RESP_SCHEMEBOARD_LinkOp_EVENT,  this.getId().getName(), 
	    			this.thisOpId.getOpName()+"_op", "succeeded");
    	}
    	else {
    		signal(REMOVE_RESP_SCHEMEBOARD_LinkOp_EVENT,  this.getId().getName(), 
    				this.thisOpId.getOpName()+"_op", "failed", "this SchemeBoard " 
    				+ schBoardId + "is already set as responsible of this GroupBoard");
    	}
    }
   
	
	
	/**********************************************************************************
     * 
     * @param subGrpBoardName
     * @param subGrpBoardSS
     ***********************************************************************************/
    @LINK(states={"active"}) Boolean addSubGroupBoardLinkOp(ArtifactId grpBoardId, Group grpBoardSpec) {
    	
    	if(this.myControler.addSubGroupBoardLinkOp(grpBoardId, grpBoardSpec)) { 
    		
    	    	updateObsProperty(GROUPBOARD_OBS_PROP8, this.myControler.subGroupBoards);    	    	
	    		return true;	    		
    	}
    	else{
    		
    			/* signal(AddSubGroupBoard_LinkOp_EVENT,  this.getId().getName(), 
    			 * this.thisOpId.getOpName()+"_op", "failed", "The max cardinality 
    			 * of sub-group name \""+ grpBoardSpec.getFullId()+ "\" reached");*/
    		return false;
    	}
    	   	
    	
    }
    
    /**********************************************************************************
    * 
    * @param agentName
    * @return
    **********************************************************************************/
    @SuppressWarnings("unchecked")
	@LINK(states={"active"}) Boolean isMemberOfGroupBoardLinkOp(String agentName) { 
    	boolean b = false;
    	//Map<ArtifactId, Set<Role>> memberRolesInGrpBoardsHierarchy = new HashMap<ArtifactId, Set<Role>>();
    	
    	if(this.myControler.rolesOfMember.containsKey(agentName))
    		b = true;
    	else	
    		if(this.myControler.subGroupBoards.size()>0) {    			
    			
    			Iterator<String> subGrpNameIt = this.myControler.subGroupBoards.keySet().iterator();
    			String subGrpName;
    			while(!b && subGrpNameIt.hasNext()) {
    				subGrpName = subGrpNameIt.next();
    				Iterator<ArtifactId> subGrpIdIt = this.myControler.subGroupBoards.get(subGrpName).iterator();
    				ArtifactId orgArtId;
    				while(subGrpIdIt.hasNext() && !b) {
    					orgArtId = subGrpIdIt.next();    				
	    				try {
	    					b = (Boolean)invokeOp(orgArtId, new Op(IsMemberOfGroupBoard_LinkOp, agentName));	    					
						} catch (CartagoException e) {						
							e.printStackTrace();
						}	
    				}
    			}
    		}    	
    	
    	return b;
    }
    
    /**********************************************************************************
     * 
     * @param agentName
     * @return
     **********************************************************************************/
    @SuppressWarnings("unchecked")
 	@LINK(states={"active"}) Map<ArtifactId, Set<String>> rolesOfMemberLinkOp(String agentName) { 
    	 Map<ArtifactId, Set<String>> memberRolesInGrpBoardsHierarchy = this.myControler.rolesOfMember(agentName);
    	 
    	
    	 return memberRolesInGrpBoardsHierarchy;
     }
     
    	 
    
    /**********************************************************************************
     * 
     * @param agentName
     * @return
     **********************************************************************************/
    @LINK(states={"active"}) Set<String> getRolesOfMemberLinkOp(String agentName){
    	Set<String> memberRoles = new HashSet<String>();
    	
    	if(this.myControler.rolesOfMember.containsKey(agentName)) {
    		memberRoles.addAll(this.myControler.rolesOfMember.get(agentName));
    	}
    	return memberRoles;
    }
    
    /**********************************************************************************
     * 
     *
     * @return
     **********************************************************************************/
    @LINK(states={"active"}) Map<String, Set<String>> getMembersOfGroupBoardLinkOp(){    	
    	return this.myControler.rolesOfMember;
    }
    
    @LINK(states={"active"}) void addNormativeBoardLinkOp(ArtifactId normativeBoardId){    	
    	
    	this.myControler.addNormativeBoardLinkOp(normativeBoardId);
    	updateObsProperty("normativeBoards", this.myControler.normativeBoards);
    	 
    	//return this.playersOfRole;    		 
    }
    /****************************************************************************************
	 * Add the new role that an agent adopt successfully in the list of its role
	 * 
	 * @param op : the type of the update operation which can be "add" or "remove"
	 * @param agName : the name of the agent which has adopted the role
	 * @param r : the concerned role
	 ****************************************************************************************/
	/*private void updateAgentRoleInLinkOrgArts(String op, String agName, Role r){
		try {
			invoke(this.orgBoardId, new Op("updateAgRoleInOrgBoard", 
					op, agName, r, this.getId().getName())); 
			
			for(ArtifactId artId : this.schemeBoards) {
				invoke(artId, new Op("updateAgRoleInSchemeBoard", op, agName, r));
			}
			
			/*if(superGroupBoards.size()>0) {
				for(String orgArtName : superGroupBoards.keySet()) {
					linkOp(superGroupBoards.get(orgArtName), 
							new Op("updateAgRoleInSuperGroupBoard", 
									groupSpec.getFullId(), this.getId().getName(), 
									op, agName, r));
				}
			}
			
			signal("updateAgentRoleInLinkOrgArts_ev", this.getId().getName(), 
					"updateAgentRoleInLinkOrgArts_op", "succeeded");
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
	}*/
    

}
