/**
 * 
 */
package ora4masMoise.GroupBoard;

import static ora4mas.OrgArts.Ora4masConstants.*;

import java.util.*;

import moise.os.CardinalitySet;
import moise.os.ss.*;

import ora4mas.OrgArts.OrgArtState;

import ora4mas.OrgArts.OrgArt;
import alice.cartago.ArtifactId;
import alice.cartago.CartagoException;
import alice.cartago.LINK;

import alice.cartago.OPERATION;
import alice.cartago.Op;

/**
 * @author kitio
 *
 */
public class GroupBoardControler {

	
	String groupBoardType = null;
	SS groupBoardSpecification = null;	
	ArtifactId orgBoardId;		
	Map<String, Integer> playableRoles = new HashMap<String, Integer>();
	Map<String, Set<String>> playersOfRole = new HashMap<String, Set<String>>();
	Set<ArtifactId> superGroupBoards = new HashSet<ArtifactId>();
	Map<String, List<ArtifactId>>  subGroupBoards = new HashMap<String, List<ArtifactId>>();
	Set<ArtifactId> dependentGroupBoards = null;
	Set<ArtifactId> schemeBoards = new HashSet<ArtifactId>(); 
	Set<ArtifactId> normativeBoards = new HashSet<ArtifactId>(); 
	static int groupBoardStatus = OrgArtState.created; 	
	
	
	Group groupSpec;
	Map<String, RoleInstance> rolesInstance = new HashMap<String, RoleInstance>();
	Map<String, Set<String>> rolesOfMember = new HashMap<String, Set<String>>();		
	ArtifactId firstSuperGroupBoardId;
	
	
	/**
	 * Constructor
	 * @param orgId
	 * @param grpSpecName
	 * @param superGrpBoardId
	 */
	
	public GroupBoardControler(ArtifactId orgId, String grpSpecName, ArtifactId superGrpBoardId) {
			
		groupBoardType = grpSpecName;		
		orgBoardId = orgId;			
		firstSuperGroupBoardId = superGrpBoardId;					
		
	}
	
	
	
	/***************************************************************
	 * 
	 * init GroupBoard Step1
	 * Get the groupSpec from OrgBoard
	 * if this groupSpec is not equal null switch to step 2 
	 * else stop initGroupBoard operation with a failed signal.
	 *
	 ***************************************************************/
	
	protected void setGroupBoardSpec(Group grp){
		
		groupSpec = grp;
		groupBoardSpecification = groupSpec.getSS();
			
	}
	

	
	/***************************************************************
	 * 
	 * Private Method initSubGroupBoard
	 *
	 ***************************************************************/
	protected boolean initSubGroupBoard(){		
		if(groupSpec.getSubGroups().size()>0) {			
			CardinalitySet<Group> subGrp = groupSpec.getSubGroups();
			String subGrpName;
			for(Group grp: subGrp) { 
				subGrpName = grp.getFullId();
				List<ArtifactId> list = new ArrayList<ArtifactId>();
				//List<SubGroupBoardInstance> list = new ArrayList<SubGroupBoardInstance>();
				subGroupBoards.put(subGrpName, list);
			}
			return true;
		}
		else
			return false;
	}
	
	/***************************************************************
	 * 
	 * Private Method initGroupBoardRoles()
	 *
	 ***************************************************************/
	protected void initGroupBoardRoles(ArtifactId grpBoardId) {
		
		for(Role r: groupSpec.getRoles()){			
			if(!r.isAbstract()){				
				String rId = r.getFullId();	
				playableRoles.put(rId, groupSpec.getRoleCardinality(r).getMax());
				
				Set<String> agents = new HashSet<String>();
				playersOfRole.put(rId, agents);
				
				RoleInstance ri = new RoleInstance(rId, groupBoardSpecification, grpBoardId);
				ri.setRoleCardinality(groupSpec.getRoleCardinality(r));				
				rolesInstance.put(rId, ri);
			}
		}		
	}	
	
	/*************************************************************************
	 * This operation is provided to member agents  
	 * to adopt a role in an instance of a GroupBoard  	 
	 * 
	 * @param rName 
	 **************************************************************************/
	@OPERATION(states={"active"}) void adoptRole(String rName){	
		 
		//log("Operation Adopt Role Started");
		
		if (!this.playableRoles.containsKey(rName)) {	     
			
		}
	        
	}
	
	
	/************************************************************************
	 * This operation is provided to member agents  
	 * to give up a role within an instance of a GroupBoard  	 
	 * 
	 * @param rName 
	 ************************************************************************/
	private void leaveRole(String rName){
		
		if(playersOfRole.containsKey(rName)) {
			Role role = this.groupSpec.getSS().getRoleDef(rName);
		}
			
			
	}
	
	
	
	/*****************************************************************************
	 * Private method used to check the intra-group compatibility of a role
	 * 
	 * @param agName
	 * @param newRole	
	 ******************************************************************************/	
	protected boolean checkIntraGroupCompatibility(String agName, Role newRole){
		
		boolean b=true; 
		String rName;
		if(rolesOfMember.containsKey(agName)){ 
        	Iterator<String> agCurrentRoles = rolesOfMember.get(agName).iterator();         	
        	Role role;
        	while(b && agCurrentRoles.hasNext()){
        		rName = agCurrentRoles.next();
        		role = groupSpec.getRoles().get(rName);
        		Iterator<Compatibility> compIt = newRole.getCompatibilities(groupSpec).iterator();
        		if(!compIt.hasNext()) {
        			b = false;
        								
        		}
        		else  
        			while(b && compIt.hasNext()) {         			    
        				if (!compIt.next().areCompatible(newRole, role)){        				
        					b = false;        					
        			}	              
        		}
        	}
        }		
        return b;
    }
	
	/********************************************************************************
	 * Private method used to check if the GroupBoard is well formed
	 * 
	 * @return "true" if for all the roles of the Group the min cardinality required 
	 * for the role reach and "false" if no
	 *********************************************************************************/
	private boolean checkGroupBoardWellFormed() {
    	boolean result = true;
    	Iterator<RoleInstance> roleInstanceIt = this.rolesInstance.values().iterator();
    	
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
    	 
    	if(playersOfRole.get(rName).contains(agName))
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
    protected void updateRolesOfPlayer(String op, String agName, Role r){
    	Map<String, Set<String>> newPlayersOfRole = this.playersOfRole;
    	String rName = r.getFullId();
    	
    	if(op.equals("add")) {
    		this.rolesInstance.get(rName).addPlayer(agName);
    		newPlayersOfRole.get(rName).add(agName); 
    		
    		if(this.rolesInstance.get(rName).minCardinalityReached()) {
    			if(checkGroupBoardWellFormed())
    				this.groupBoardStatus = OrgArtState.wellFormed;
    		}
	    	
    		if(rolesOfMember.containsKey(agName)){
	    		rolesOfMember.get(agName).add(rName);    		
	    	}else{
	    		Set<String> roles = new HashSet<String>();
	    		roles.add(rName);
	    		rolesOfMember.put(agName, roles);  		
	    	}
    	} 
    	else if(op.equals("remove")) {
    		this.rolesInstance.get(r.getFullId()).removePlayer(agName);
    		newPlayersOfRole.get(r.getFullId()).remove(agName); 
    		
    		if(!this.rolesInstance.get(rName).minCardinalityReached() 
    			&& (this.groupBoardStatus == OrgArtState.wellFormed)) {
    			this.groupBoardStatus = OrgArtState.active;
    		}
    		
    		if(rolesOfMember.containsKey(agName)){
	    		rolesOfMember.get(agName).remove(r);    		
	    	}
    	}    	
    }   
    
	
    /**************************************************************************************
     * 
     * @param schBoardName
     * @param schBoardId
     ***************************************************************************************/
    protected boolean addSchemeBoard(ArtifactId schBoardId){
    	
    	if(!schemeBoards.contains(schBoardId)) {    		
	    	schemeBoards.add(schBoardId);	
	    	return true;
    	}
    	else {
    		return false;
    	}
    }
	
    /**************************************************************************************
     * 
     * 
     * @param schBoardId
     ***************************************************************************************/
    protected boolean removeSchemeBoard(ArtifactId schBoardId){
    	if(schemeBoards.contains(schBoardId)) {
	    	schemeBoards.remove(schBoardId);	    	
	    	return true;	    
    	}
    	else {
    		return false;
    	}
    }   
	
	
	/**********************************************************************************
     * 
     * @param subGrpBoardName
     * @param subGrpBoardSS
     ***********************************************************************************/
    protected Boolean addSubGroupBoardLinkOp(ArtifactId grpBoardId, Group grpBoardSpec) {
    	
    	boolean returnValue = true;
    	
    	if(this.groupBoardSpecification.equals(grpBoardSpec.getSS())) { 
    		if(subGroupBoards.get(grpBoardSpec.getFullId()).size() < 
    			groupSpec.getSubGroupCardinality(groupSpec.findSubGroup(grpBoardSpec.getFullId())).getMax()){
    			
    			subGroupBoards.get(grpBoardSpec.getFullId()).add(grpBoardId);	    			    		
    		}
    		else
    			returnValue = false;
    		
    	}
    	else 
    		returnValue = false;
    		
    	     	
    	return returnValue;
    }
    
    /**********************************************************************************
    * 
    * @param agentName
    * @return
    **********************************************************************************/
   
    
    /**********************************************************************************
     * 
     * @param agentName
     * @return
     **********************************************************************************/
    @SuppressWarnings("unchecked")
 	protected Map<ArtifactId, Set<String>> rolesOfMember(String agentName) { 
    	 Map<ArtifactId, Set<String>> memberRolesInGrpBoardsHierarchy = new HashMap<ArtifactId, Set<String>>();
    	 
    	 if(this.rolesOfMember.containsKey(agentName)) { 
     		Set<String> roles = new HashSet<String>();
     		roles.addAll(this.rolesOfMember.get(agentName));
     		//memberRolesInGrpBoardsHierarchy.put(this.getId(), roles);
     		
     		if(this.subGroupBoards.size()>0)
     		for(String subGrpBoardName: this.subGroupBoards.keySet())    			
     			for(ArtifactId orgArtId:this.subGroupBoards.get(subGrpBoardName)) {
     				roles = new HashSet<String>();
     				try {
     					
     					if(roles.size()>0)
     						memberRolesInGrpBoardsHierarchy.put(orgArtId, roles);
 					} catch (Exception e) {						
 						e.printStackTrace();
 					}					
     			}    		
     	}
    	 return memberRolesInGrpBoardsHierarchy;
     }
     
    	 
    
    /**********************************************************************************
     * 
     * @param agentName
     * @return
     **********************************************************************************/
    @LINK(states={"active"}) Set<String> getRolesOfMemberLinkOp(String agentName){
    	Set<String> memberRoles = new HashSet<String>();
    	
    	if(this.rolesOfMember.containsKey(agentName)) {
    		memberRoles.addAll(this.rolesOfMember.get(agentName));
    	}
    	return memberRoles;
    }
    
    /**********************************************************************************
     * 
     *
     * @return
     **********************************************************************************/
    @LINK(states={"active"}) Map<String, Set<String>> getMembersOfGroupBoardLinkOp(){    	
    	return this.rolesOfMember;
    }
    
    @LINK(states={"active"}) void addNormativeBoardLinkOp(ArtifactId normativeBoardId){    	
    	
    	this.normativeBoards.add(normativeBoardId);
    	 
    	//return this.playersOfRole;    		 
    }
}
