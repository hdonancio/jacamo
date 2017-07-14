/**
 * 
 */
package ora4masMoise.NormBoard;

import static ora4mas.OrgArts.Ora4masConstants.*;

import ora4mas.OrgArts.OrgArtState;

import java.util.*;

import moise.os.ds.DS;
import moise.os.ds.DeonticRelation;
//import ora4mas.OrgArts.OrgArt;
import alice.cartago.*;

/**
 * @author kitio
 *
 */
public class NormativeBoardControler {


	public enum NormStatus {committed, notCommited, no_Obligation, no_Permission};
		
	DS normativeBoardSpecification = null;
	ArtifactId orgBoardId;
	ArtifactId schemeBoardId;
	Set<ArtifactId> groupBoards = new HashSet<ArtifactId>();
	
	Map<String, MemberAgentInstance> agentsStatus = new HashMap<String, MemberAgentInstance>();
	static int normativeBoardStatus = OrgArtState.created;
	
	Set<ArtifactId> firstGroupBoardId = new HashSet<ArtifactId>();
	
	/***********************************************************************
	 * This operation initialize the NormativeBoard by  setting
	 * the initial values of its attributes and @OBSPROPERTY
	 * 
	 * @param orgBoardId
	 * @param grpBoardId
	 * @param schBoardId
	 ***********************************************************************/
	public NormativeBoardControler (ArtifactId orgBoardId,  
			Set<ArtifactId> grpBoardId, ArtifactId schBoardId) {	
		
		this.orgBoardId = orgBoardId; 
		this.firstGroupBoardId = grpBoardId;
		this.schemeBoardId = schBoardId;		
		
	}
	
	/***************************************************************
	 * init NormativeBoard Step1
	 * 
	 * 
	 ***************************************************************/
	//@OPSTEP void initNormativeBoardStep1(){
	protected void setNormativeBoardSpec(DS ds){
		
		normativeBoardSpecification = ds;
			
	}
	
	/***************************************************************
	 * init NormativeBoard Step 2
	 * 
	 * 
	 ***************************************************************/
	//@OPSTEP void initNormativeBoardStep2(){
	//protected void addGtoupBoard(ArtifactId grpId){
		
			//groupBoards.add(firstGroupBoardId..get(i));	
		
	//}
	
	
	
	/*************************************************
	 * 
	 * @param grpBoardId
	 *************************************************/
	@OPERATION(states={"active"}) void addGroupBoard(ArtifactId grpBoardId) {		
		this.groupBoards.add(grpBoardId);		
	}
	

	
	/****************************************************************************
	 * private method called to update norms status of an agent in the case 
	 * its action in the corresponding GroupBoard is ADOPT_ROLE_OP  
	 *  
	 * @param agName
	 * @param roleName
	 * @param grpBoardId
	 ****************************************************************************/
	protected void updateAgentStatusWithGroupBoardOp1(String agName, String roleName, ArtifactId grpBoardId) {
		
		RolePlayed newRolePlayed = new RolePlayed(roleName, grpBoardId);
		RoleWithNormStatus newRoleWithNormStatus;
		
		Iterator<DeonticRelation> dsIterator = this.normativeBoardSpecification.getDeonticRelations().iterator();
		boolean roleHasDeonticRelation = false;
		DeonticRelation dr;
		String missionId = null;
		RoleWithNormStatus.Norm norm = null;
		
		while(dsIterator.hasNext()) {
			dr = dsIterator.next();
			
			if(dr.getRole().getId().equals(roleName)) {
				roleHasDeonticRelation = true;
				missionId = dr.getMission().getId();
				
				if(dr.getType().equals("permission"))
					norm = RoleWithNormStatus.Norm.Permission;
				else if(dr.getType().equals("obligation"))
					norm = RoleWithNormStatus.Norm.Obligation;
				
				newRoleWithNormStatus  = new RoleWithNormStatus(newRolePlayed, norm, missionId);					
				newRoleWithNormStatus.setNormStatusValue(RoleWithNormStatus.NormStatusValue.notCommited);
				agentsStatus.get(agName).addRoleWithNormStatus(newRoleWithNormStatus);
			}
		}
		if(!roleHasDeonticRelation) {
			norm = RoleWithNormStatus.Norm.No_Norm;
			newRoleWithNormStatus  = new RoleWithNormStatus(newRolePlayed, norm, missionId);
			newRoleWithNormStatus.setNormStatusValue(RoleWithNormStatus.NormStatusValue.nullValue);
			agentsStatus.get(agName).addRoleWithNormStatus(newRoleWithNormStatus);
		}
	}
	
	/*****************************************************************************
	 * private method called to update norms status of an agent in the case 
	 * its action in the corresponding GroupBoard is LEAVE_ROLE_OP  
	 * 
	 * @param agName
	 * @param roleName
	 * @param grpBoardId
	 * @return
	 ******************************************************************************/	
	@SuppressWarnings("unchecked")
	protected List<String> updateAgentStatusWithGroupBoardOp2(String agName, String roleName, ArtifactId grpBoardId) {
		ArrayList<RoleWithNormStatus> roleWithNormStatus = (ArrayList)agentsStatus.get(agName).getAgentRolesWithNormStatus().clone();
		List<String> missionsId = new ArrayList<String>();		
		String mId; 		
		
		for(RoleWithNormStatus it: roleWithNormStatus) {			
			if(it.isRolePlayedParameters(roleName, grpBoardId)) { 
				mId = it.getMissionId();
				
				if(it.getNormStatusValue().equals(RoleWithNormStatus.NormStatusValue.committed))
					missionsId.add(mId);				
				
				agentsStatus.get(agName).removeRoleWithNormStatus(it); 			
			}				
		}
		return missionsId;
	}
	
	/***********************************************************************************
	 * Link operation called by a SchemeBoard to update norms status of an agent 
	 * according to its action in the SchemeBoard
	 * 
	 * @param agName
	 * @param missionName
	 * @param operation
	 ************************************************************************************/
	protected void updateAgentStatusFromSchemeBoard(String agName, 
			String missionName, String operation) {
		if(operation.equals(COMMIT_MISSION_OP))
			if(!this.agentsStatus.containsKey(agName)) {
				/*MemberAgentInstance newAgentStatus = new MemberAgentInstance(agName);
				this.agentsStatus.put(agName, newAgentStatus);
				udateAgentStatusWithSchemeBoardOp1(agName, missionName);*/
			
			} else {
				udateAgentStatusWithSchemeBoardOp1(agName, missionName);
			}
		else if(operation.equals(LEAVE_MISSION_OP))
			if(this.agentsStatus.containsKey(agName)) {
				udateAgentStatusWithSchemeBoardOp2(agName, missionName);
			}
	}
	
	/*************************************************************************************
	 * private operation called to update agentsStaus when 
	 * an agent execute operation commitMission
	 * 
	 * @param agName
	 * @param missionName
	 *************************************************************************************/
	protected void udateAgentStatusWithSchemeBoardOp1(String agName, String missionName) {
		MemberAgentInstance newAgentStatus = this.agentsStatus.get(agName);
		Iterator<RoleWithNormStatus> it = newAgentStatus.getAgentRolesWithNormStatus().iterator();
		boolean missionExist = false;
		RoleWithNormStatus roleWithNormStatus;
		
		while(!missionExist && it.hasNext()) {
			roleWithNormStatus = it.next();
			if(roleWithNormStatus.getMissionId().equals(missionName)) {
				roleWithNormStatus.setNormStatusValue(RoleWithNormStatus.NormStatusValue.committed);
				missionExist = true;
			}			
		}
		
		if(!missionExist) {
			RolePlayed firstRolePlayed = newAgentStatus.getAgentRolesWithNormStatus().get(0).getRolePlayed();
			roleWithNormStatus = new RoleWithNormStatus(firstRolePlayed, 
					RoleWithNormStatus.Norm.No_Norm, missionName);
			roleWithNormStatus.setNormStatusValue(RoleWithNormStatus.NormStatusValue.violationOfNorm);
			this.agentsStatus.get(agName).addRoleWithNormStatus(roleWithNormStatus);
		}
	}
	
	/**********************************************************************
	 * private operation called to update agentsStaus when 
	 * an agent execute operation leaveMission
	 * 
	 * @param agName
	 * @param missionName
	 **********************************************************************/
	protected void udateAgentStatusWithSchemeBoardOp2(String agName, String missionName) {
		MemberAgentInstance newAgentStatus = this.agentsStatus.get(agName);
		Iterator<RoleWithNormStatus> it = newAgentStatus.getAgentRolesWithNormStatus().iterator();
		boolean missionExist = false;
		int index = 0;
		//RoleWithNormStatus roleWithNormStatus;
		
		while(!missionExist && it.hasNext()) {			
			if(!it.next().getMissionId().equals(missionName)) {
				index++;
				missionExist = true;
			}			
		}
		
		if(missionExist) {
			this.agentsStatus.get(agName).removeCommittedMission(index);
		}
	}
	
		
	/*private boolean checkDS(String agName, String missionId) {
    	Set<Role> agRoles = playersOfRespGroupBoard.get(agName);
    	boolean checkDSValue = true;
    	log(String.valueOf(agRoles.size()));
    	Iterator<Role> agRoleIt = agRoles.iterator();
    	// for all roles, try to find a permission for the mission  
    	Role role;
        while(checkDSValue && agRoleIt.hasNext()) {
        	role = agRoleIt.next();     log(role.getFullId() + " "+ missionId) ;  	
            if (!role.hasDeonticRelation("obligation", missionId) || 
            		!role.hasDeonticRelation("permission", missionId)) {
            	
            	checkDSValue = false;             	
	        }           
        }             
        return checkDSValue;
    }*/
}
