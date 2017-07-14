/**
 * 
 */
package ora4masMoise.NormBoard;

import static ora4mas.OrgArts.Ora4masConstants.*;
import ora4mas.OrgArts.OrgArtState;

import java.util.*;
import moise.os.ds.DS;
import alice.cartago.*;

import ora4mas.OrgArts.OrgArt;

/**
 * @author kitio
 *
 */
@ARTIFACT_MANUAL(
		states = {"created", "error", "active", "wellFormed"},
		start_state = "created",
		
		outports = {
				@OUTPORT(name = "NormativeBoard_out-1"),
				@OUTPORT(name = "NormativeBoard_out-2"),
				@OUTPORT(name = "NormativeBoard_out-3")
		},
		
		oi = @OPERATING_INSTRUCTIONS(""),
		fd = @FUNCTION_DESCRIPTION("")
)

public class NormativeBoard extends OrgArt {
	
	NormativeBoardControler myControler ;	
	
	@OPERATION void init(){
		
        Map<String, MemberAgentInstance> agentsStatus = new HashMap<String, MemberAgentInstance>();
		defineObsProperty("agentsStatus", agentsStatus );
		
		defineObsProperty("normativeBoardStatus", OrgArtState.created);		

	}
	
	
	@OPERATION void configure(Object orgBoardName, Object schBoardName, Object grpBoardsName){
	   
		log("parse param ok");
		if (orgBoardName != null && schBoardName != null && grpBoardsName != null) {
			ArtifactId orgBoardId = null, schemeBoardId = null;
		    Set<ArtifactId> groupBoardsId = new HashSet<ArtifactId>();
		    ArtifactId registry;
		    ArtifactId[] list;
	
		   // need to find orgBoardId and schemeBoardId as ArtifactIds 
		    try { 
		    	registry = getRegistryId();
		    	list = (ArtifactId[])invokeOp(registry, new Op("_getCurrentArtifactsIds"));
		    	
		    	for (ArtifactId elt: list){ 
			    	if(elt.getName().equals(orgBoardName))
					 orgBoardId = elt;
			    	
				    else if(elt.getName().equals(schBoardName))
				    	schemeBoardId = elt;
				    
				    else if(elt.getName().equals(grpBoardsName))
			    			groupBoardsId.add(elt);			    			
			    	
			    }
		    } catch (Exception ex){
		    	signal("error in setting referenced artifacts");
		    	ex.printStackTrace();
		   }
			
			
			myControler = new NormativeBoardControler(orgBoardId, groupBoardsId, schemeBoardId);		
			
			defineObsProperty("orgBoardId", orgBoardId );
			
			defineObsProperty("schemeBoardId",schemeBoardId );
			
			defineObsProperty("groupBoards", groupBoardsId);
			
			initNormativeBoard();
		}
		else
			 signal(INIT_SCHEMEBOARD_EVENT,  this.getId().getName(),"error : null configuration parameters");
		
			
	}
	
	/***********************************************************************
	 * This operation initialize the NormativeBoard by  setting
	 * the initial values of its attributes and @OBSPROPERTY
	 * 
	 * @param orgBoardId
	 * @param grpBoardId
	 * @param schBoardId
	 ***********************************************************************/
	private void initNormativeBoard() { //@OPERATION(states={"created"})	
		
		try {
			DS ns = (DS)invokeOp(this.myControler.orgBoardId, new Op(GetNormativeBoardSpec_LinkOp));
			
			if(!ns.equals(null)) {
				this.myControler.normativeBoardSpecification = ns;				
				defineObsProperty("normativeBoardSpecification", this.myControler.normativeBoardSpecification);
				
				initNormativeBoardStep2();
			}
			else {
				signal(INIT_NORMATIVEBOARD_OPSTEP2, this.getId().getName(), 
						this.thisOpId.getOpName()+"_op", "failed", 
						"An error occured durring execution of " +
						"getNormativeBoardSpec on " + this.myControler.orgBoardId);	
			}
		} catch (CartagoException e) {			
			e.printStackTrace();
		}
		
	}
	
	/***************************************************************
	 * init NormativeBoard Step 2
	 * 
	 * 
	 ***************************************************************/
	//@OPSTEP void initNormativeBoardStep2(){
	private void initNormativeBoardStep2(){
		try {
			Boolean groupBoardIdExist;
			boolean b = false; 
			Iterator<ArtifactId> it = this.myControler.firstGroupBoardId.iterator();
			ArtifactId id;
			
			while(it.hasNext() && !b) {
				id = it.next();
				groupBoardIdExist = (Boolean)invokeOp(this.myControler.orgBoardId, new
					Op(ExistGroupBoardIdInOrgBoard_LinkOp, id)); 			
			
				if(groupBoardIdExist) {	
					this.myControler.groupBoards.add(id);	
					
				}
				else 
					b = true;
			}
			
			if(b == true){
				signal(INIT_NORMATIVEBOARD_EVENT, this.getId().getName(), 
					this.thisOpId.getOpName()+"_op", "failed",
					"There is no GroupBoard Id equals\"" + this.myControler.firstGroupBoardId + 
					"\" registred in the OrgBoard \"" + this.myControler.orgBoardId.getName()+"\"");
			}
			else {			
				initNormativeBoardStep3();
			}
			
		} catch (CartagoException e) {			
			e.printStackTrace();
		}
		
	}
	
	/***************************************************************
	 * init NormativeBoard Step 3
	 * 
	 * 
	 ***************************************************************/
	//@OPSTEP void initNormativeBoardStep3(){
	private void initNormativeBoardStep3(){
		try {
			Boolean schemeBoardIdExist = (Boolean)invokeOp(this.myControler.orgBoardId, 
					new Op(ExistSchemeBoardIdInOrgBoard_LinkOp, this.myControler.schemeBoardId)); 
			
			if(schemeBoardIdExist) {								
				invokeOp(this.myControler.schemeBoardId, new Op(SetNormativeBoard_LinkOp, this.getId())); 
				
				for(ArtifactId grpBoardId: this.myControler.groupBoards)
					invokeOp(grpBoardId, new Op(AddNormativeBoard_LinkOp, this.getId())); 
				
				this.myControler.normativeBoardStatus = OrgArtState.active;
				
				updateObsProperty("normativeBoardStatus", this.myControler.normativeBoardStatus);				
				switchToState("active"); 
				
				signal(INIT_NORMATIVEBOARD_EVENT, this.getId().getName(), 
						this.thisOpId.getOpName()+"_op", "succeeded");
			}
			else {
				signal(INIT_NORMATIVEBOARD_EVENT, this.getId().getName(), 
					this.thisOpId.getOpName()+"_op", "failed", 
					"There is no SchemeBoard Id equals\"" + this.myControler.schemeBoardId + 
					"\" registred in the OrgBoard \"" + this.myControler.orgBoardId.getName()+"\"");
			}
			
		} catch (CartagoException e) {			
			e.printStackTrace();
		}
	}
	
	/*************************************************
	 * 
	 * @param grpBoardId
	 *************************************************/
	@OPERATION(states={"active"}) void addGroupBoard(ArtifactId grpBoardId) {		
		this.myControler.groupBoards.add(grpBoardId);	
		updateObsProperty("groupBoards", this.myControler.groupBoards);	
	}
	
	/***************************************************************************
	 * Link operation called by a GroupBoard to update norms status of an agent 
	 * according to its action in the GroupBoard
	 * 
	 * @param agName
	 * @param roleName
	 * @param grpBoardId
	 * @param operation
	 ***************************************************************************/
	@LINK(states={"active"}) void updateAgentStatusFromGroupBoardLinkOp(String agName, 
			String roleName, ArtifactId grpBoardId, String operation) {
		
		if(operation.equals(ADOPT_ROLE_OP)) {
			if(!this.myControler.agentsStatus.containsKey(agName)) {
				MemberAgentInstance newAgentStatus = new MemberAgentInstance(agName);
				this.myControler.agentsStatus.put(agName, newAgentStatus);				
			}			
			this.myControler.updateAgentStatusWithGroupBoardOp1(agName, roleName, grpBoardId);
			updateObsProperty("agentsStatus", this.myControler.agentsStatus );	
				
		}
		else if(operation.equals(LEAVE_ROLE_OP))
			if(this.myControler.agentsStatus.containsKey(agName)) {
				
				List<String> missionsId = this.myControler.updateAgentStatusWithGroupBoardOp2(agName, roleName, grpBoardId);	
				updateObsProperty("agentsStatus", this.myControler.agentsStatus );	
				
				if(missionsId.size()>0) {
					try {
						log("enter invoke");
						invokeOp(this.myControler.schemeBoardId, new Op(UpdateCommittedMissionForLeaveRole_LinkOp,
								agName, missionsId));
					} catch (CartagoException e) {
						e.printStackTrace();
					}
				}
			}	
	}

	
	
	
	/***********************************************************************************
	 * Link operation called by a SchemeBoard to update norms status of an agent 
	 * according to its action in the SchemeBoard
	 * 
	 * @param agName
	 * @param missionName
	 * @param operation
	 ************************************************************************************/
	@LINK(states={"active"}) void updateAgentStatusFromSchemeBoardLinkOp(String agName, 
			String missionName, String operation) {
		if(operation.equals(COMMIT_MISSION_OP))
			if(this.myControler.agentsStatus.containsKey(agName)) {
				this.myControler.udateAgentStatusWithSchemeBoardOp1(agName, missionName);
				updateObsProperty("agentsStatus", this.myControler.agentsStatus );	
				
			}
		else if(operation.equals(LEAVE_MISSION_OP))
			if(this.myControler.agentsStatus.containsKey(agName)) {
				this.myControler.udateAgentStatusWithSchemeBoardOp2(agName, missionName);
				updateObsProperty("agentsStatus", this.myControler.agentsStatus );	
				
			}
	}
	

}
