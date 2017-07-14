/**
 * 
 */
package ora4masMoise.SchemeBoard;

import static ora4mas.OrgArts.Ora4masConstants.*;

import java.util.*;

import ora4mas.OrgArts.Ora4masConstants;
import ora4mas.OrgArts.OrgArt;
import ora4mas.OrgArts.OrgArtState;

import moise.os.fs.*;
import alice.cartago.*;

/**
 * @author kitio
 *
 */
@ARTIFACT_MANUAL(
		states = {"created", "error", "active", "wellFormed", "finished"},
		start_state = "created",
		
		outports = {
				@OUTPORT(name = "SchemeBoard_out-1"),
				@OUTPORT(name = "SchemeBoard_out-2"),
				@OUTPORT(name = "SchemeBoard_out-3")
		},
		
		oi = @OPERATING_INSTRUCTIONS(""),
		fd = @FUNCTION_DESCRIPTION("")
)
public class SchemeBoard extends OrgArt{

	SchemeBoardControler myControler;
	
	@OPERATION void init(){
		HashMap<String, Integer> playableMissions = new HashMap<String, Integer>();
		defineObsProperty("playableMissions", playableMissions );
		 
		 HashMap<String, Set<String>> playersOfMission = new HashMap<String, Set<String>>();
		 defineObsProperty("playersOfMission", playersOfMission);
		
		 LinkedHashMap<String, GoalInstance.State> goalsStatus = new LinkedHashMap<String, GoalInstance.State>();
		 defineObsProperty("goalsStatus", goalsStatus );
		 
		
		 HashSet<ArtifactId> responsibleGroupBoards = new HashSet<ArtifactId>();	
		 defineObsProperty("responsibleGroupBoards", responsibleGroupBoards );
		
		 HashMap<String, ArtifactId> dependentSchemeBoards = new HashMap<String, ArtifactId>();
		 defineObsProperty("dependentSchemeBoards", dependentSchemeBoards );
		
		 LinkedHashMap<String, GoalInstance> goalsInstance = new LinkedHashMap<String, GoalInstance>();
		 defineObsProperty("goalsInstance", goalsInstance);

	}
	
	
    @OPERATION void configure(Object orgBoardName, Object schSpecName, Object firstRespGrpBoardName){
	
     
      if (orgBoardName != null && schSpecName != null && firstRespGrpBoardName != null) {
			
    	  ArtifactId orgBoardId = null;
    	  ArtifactId respGrpBoardId = null;	      
	      //String schSpecName = schSpec.toString();
	 	  
	 	  //log("Configuring new SchemeBoard with: " +orgBoard + " " + schSpecName + " " + respGrpBoard);
	      try {
		    	ArtifactId registry = getRegistryId();
		    	ArtifactId[] list = (ArtifactId[])invokeOp(registry, new Op("_getCurrentArtifactsIds"));
		    	for (ArtifactId elt: list){
			    	if(elt.getName().equals(orgBoardName))
			    		orgBoardId = elt;
			    	
				    else if(elt.getName().equals(firstRespGrpBoardName))
				    	respGrpBoardId = elt;
			    }
		   } catch (Exception ex){
			   signal(INIT_SCHEMEBOARD_EVENT,  this.getId().getName(),"error in setting referenced artifacts");
			  ex.printStackTrace();
		   }
	    	
	    	myControler = new SchemeBoardControler(schSpecName.toString(), orgBoardId, respGrpBoardId);
			
			defineObsProperty("schemeBoardType", myControler.schemeBoardType);
			
			defineObsProperty("orgBoardId", this.myControler.orgBoardId );	
			
			defineObsProperty("schemeBoardStatus", this.myControler.schemeBoardStatus);
			
			initSchemeBoard();
			
      }
		else
			 signal(INIT_SCHEMEBOARD_EVENT,  this.getId().getName(),"error : null configuration parameters");
			
    }
		
	
	
		
	 /********************************************************************************************
	 * This operation initialize the SchemeBoard 
	 * by setting its the Scheme-Spec and the name to the SchemeBoard
	 * 
	 * @param schName
	 * @param sch 
	 *********************************************************************************************/
	 
	private void initSchemeBoard() { //@OPERATION(states={"created"})	
		
		this.myControler.setEntityId(this.getId());
		
		try {
			Scheme schemeSpec = (Scheme)(invokeOp(this.myControler.orgBoardId, 
					new Op(GetSchemeBoardSpecInOrgBoard_LinkOp,	this.myControler.schemeBoardType)));
						
			if(!schemeSpec.equals(null)){	
				myControler.setSchemeBoardSpec(schemeSpec);
				defineObsProperty("schemeBoardSpecification", myControler.schemeBoardSpecification);
				initSchemeBoardStep2();
			}
			else {
				signal(INIT_SCHEMEBOARD_EVENT, this.getId().getName(), 
						this.thisOpId.getOpName()+"_op", "failed", "There is no Scheme named \"" 
						+ myControler.schemeBoardType + "\" in the OS of the OrgBoard \"" 
						+ myControler.orgBoardId.getName()+"\"");				
			}
			
		} catch (Exception e) {			
			e.printStackTrace();
		}	
		
		initSchemeBoardStep2();
	}
	
	
	/***************************************************************
	 * 
	 * init SchemeBoard Step2
	 *
	 ***************************************************************/
	
	private void initSchemeBoardStep2(){	
		try {
			Boolean GrpBoardExit = (Boolean)invokeOp(this.myControler.orgBoardId, 
						new Op(ExistGroupBoardIdInOrgBoard_LinkOp, 
								this.myControler.firstResponsibleGroupBoardId)); 				
				
			if(GrpBoardExit) {
				this.myControler.addFirstResponsibleGroupBoard();
				initSchemeBoardStep3();
			}
			else {
				signal(INIT_SCHEMEBOARD_EVENT, this.getId().getName(), 
						this.thisOpId.getOpName()+"_op", "failed",
						"There is no GroupBoard Id equals\"" + 
						this.myControler.firstResponsibleGroupBoardId + 
						"\" registred in the OrgBoard \"" + 
						this.myControler.orgBoardId.getName()+"\"");
			}
		} catch (Exception e) {				
			e.printStackTrace();
		}	
	}
	
		
	/***************************************************************
	 * 
	 * init SchemeBoard Step3
	 *
	 ***************************************************************/
	private void initSchemeBoardStep3() { 
		try {
			Boolean registrationSucceeded = (Boolean)invokeOp(this.myControler.orgBoardId, 
					new Op(RegisterSchemeBoard_LinkOp, this.getId().getName(), this.getId()));
		
			
			if(registrationSucceeded){			
				myControler.initMissionsGoals(this.getId());					
						
				invokeOp(this.myControler.firstResponsibleGroupBoardId, new 
						Op(ADD_RESP_SCHEMEBOARD_LinkOp, this.getId()));
				
				signal(INIT_SCHEMEBOARD_EVENT,  this.getId().getName(), 
		    			this.thisOpId.getOpName()+"_op", "succeeded");
				
				this.myControler.updateSchemeBoardStatus(OrgArtState.active);
				updateObsProperty("schemeBoardStatus", this.myControler.schemeBoardStatus);	
				switchToState("active");
			}
			else{
				signal(INIT_SCHEMEBOARD_EVENT,  this.getId().getName(), 
						this.thisOpId.getOpName() + "_op", "failed", 
						"the registering in the OrgBoard named \"" + 
						this.myControler.orgBoardId.getName(), " failed");
			
			}
		} catch (CartagoException e1) {			
			e1.printStackTrace();
		}
	}	
	
	
	
	/************************************************************************************************
	 * 
	 * @param grpBoardName
	 * @param grpBoardId
	 * 
	 *************************************************************************************************/
	@OPERATION(states={"active"}) void addResponsibleGroupBoard(ArtifactId grpBoardId) {			
		
		if(this.myControler.addResponsibleGroupBoard(grpBoardId)) {
			updateObsProperty("responsibleGroupBoards", this.myControler.responsibleGroupBoards);
			try {
				invokeOp(grpBoardId, new Op(ADD_RESP_SCHEMEBOARD_LinkOp, this.getId()));
			} catch (Exception e) {					
				e.printStackTrace();
			}
			
			signal(ADD_RESP_GROUPBOARD_EVENT,  this.getId().getName(), 
	    			this.thisOpId.getOpName()+"_op", "succeeded", this.getOpUserName());				
		} else {
			signal(ADD_RESP_GROUPBOARD_EVENT,  this.getId().getName(), 
					this.thisOpId.getOpName()+"_op", "failed", this.getOpUserName(), 
					"this GroupBoard is already set as responsible of this SchemeBoard");
		}
		
	}
	
	/************************************************************************************************
	 *  
	 *  
	 * @param grpBoardId
	 *************************************************************************************************/
	@OPERATION(states={"active"}) void removeResponsibleGroupBoardOp(ArtifactId grpBoardId) {
		
		if(this.myControler.removeResponsibleGroupBoardOp(grpBoardId)) {			
			updateObsProperty("responsibleGroupBoards", this.myControler.responsibleGroupBoards);
			try {
				invokeOp(grpBoardId, new Op(REMOVE_RESP_SCHEMEBOARD_LinkOp, this.getId()));
			} catch (Exception e) {					
				e.printStackTrace();
			}
			
			signal(REMOVE_RESP_GROUPBOARD_EVENT,  this.getId().getName(), 
	    			this.thisOpId.getOpName()+"_op", "succeeded", this.getOpUserName());
		}
		else {
			signal(REMOVE_RESP_GROUPBOARD_EVENT,  this.getId().getName(), 
					this.thisOpId.getOpName()+"_op", "failed", this.getOpUserName(), 
					"this GroupBoard is already set as responsible of this SchemeBoard");
		}
			
	}
	
	/*************************************************************************************************
	 * 
	 * @param missionId
	 **************************************************************************************************/
	@SuppressWarnings("unchecked")
	@OPERATION(states={"active"}) void commitMission(Object mission){ //(states={"active"})
		
		String missionId = mission.toString();
		
		if((this.myControler.schemeBoardStatus==OrgArtState.active) 
			|| (this.myControler.schemeBoardStatus==OrgArtState.wellFormed)) {
				
		if (this.myControler.rootGoal.isAchieved() || this.myControler.rootGoal.isImpossible()) {            
            signal(COMMIT_MISSION_EVENT,  this.getId().getName(), this.thisOpId.getOpName() + "_op", 
            		missionId, "failed", this.getOpUserName(), 
            		"the goal " + this.myControler.rootGoal.getGoalSpec().getFullId() +
	            	" is satisfied or Impossible");	           
        }	
		else 
		if(!this.myControler.playableMissions.containsKey(missionId)) {
			signal(COMMIT_MISSION_EVENT,  this.getId().getName(), 
					this.thisOpId.getOpName() + "_op", missionId, "failed", this.getOpUserName(), 
					"\"the mission does not exist in the SchemeBoard\"" +
					" or \"the max cardinality for this mission reached\"");
		}
		else 
		if(this.myControler.missionsInstance.get(missionId).getCommittedAgents().contains(this.getOpUserName())){			
			signal(COMMIT_MISSION_EVENT,  this.getId().getName(), this.thisOpId.getOpName() + "_op", 
					missionId, "failed", this.getOpUserName(), "the agent has already committed to the mission");
		}
		else { 
			
			boolean b = false; int i=0;
			Iterator<ArtifactId> respGrpBoardsIt = this.myControler.responsibleGroupBoards.iterator();
			ArtifactId artId ;
			while(!b && respGrpBoardsIt.hasNext()) {
				i++; 
				artId = respGrpBoardsIt.next();				
				try {
					Boolean result = (Boolean)invokeOp(artId, new Op(IsMemberOfGroupBoard_LinkOp, 
							this.getOpUserName()));					
					
					if(result)
						b = true;
				} catch (CartagoException e) {					
					e.printStackTrace();
				}
			}
			if(!b) {
				signal(COMMIT_MISSION_EVENT, this.getId().getName(), this.thisOpId.getOpName() + "_op", 
						missionId, "failed", this.getOpUserName(), 
						"the agent does not belong to the responsible group"); 
						 
			}			
			else {
				this.myControler.updateMissionsPropertiesValues(missionId, this.getOpUserName(), 
						this.thisOpId.getOpName());	  
				
		        updateObsProperty(SCHEMEBOARD_OBS_PROP4, this.myControler.playableMissions);//"playableMissions";
		        updateObsProperty(SCHEMEBOARD_OBS_PROP5, this.myControler.playersOfMission);//"playersOfMission";
		        
		        if(myControler.updateSchemeBoardState(missionId, this.thisOpId.getOpName())) {
		        	updateObsProperty(SCHEMEBOARD_OBS_PROP9, this.myControler.schemeBoardStatus);
		        	
		        	
		        }
		        try {
						invokeOp(this.myControler.normativeBoardId, new Op(UpdateAgentStatusFromSchemeBoard_LinkOp, 
								this.getOpUserName(), missionId, COMMIT_MISSION_OP));
					} catch (Exception e) {					
						e.printStackTrace();
					}
		            
		            signal(COMMIT_MISSION_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+"_op", 
		            		missionId, "succeeded", this.getOpUserName());    
		            
		            if( this.myControler.schemeBoardStatus == OrgArtState.wellFormed) {
		        		
		            	for(String s:this.myControler.currentAchievableGoals)
							signal(NEW_POSSIBLE_GOAL_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+"_op", s);
		        	}
		    }
		}	
		} else {
			signal(COMMIT_MISSION_EVENT,  this.getId().getName(), this.thisOpId.getOpName() + "_op", 
					missionId, "failed", this.getOpUserName(), "operation commitMission is not activated");
		}
	
	}
	
	/*************************************************************************************************
	 * 
	 * @param missionId
	 **************************************************************************************************/
	@SuppressWarnings("unchecked")
	@OPERATION(states={"active"}) void leaveMission(String missionId){ //(states={"active"})
		
		if((this.myControler.schemeBoardStatus==OrgArtState.active) 
				|| (this.myControler.schemeBoardStatus==OrgArtState.wellFormed)) {			
		
		if(this.myControler.playersOfMission.containsKey(missionId)) {
			if(!this.myControler.checkAgentInPlayersOfMission(missionId, this.getOpUserName())){			
				signal(LEAVE_MISSION_EVENT, this.getId().getName(), this.thisOpId.getOpName() + "_op", 
						missionId, "failed", this.getOpUserName(), "the agent has not committed to this mission");
			}
			else {				            
				this.myControler.updateMissionsPropertiesValues(missionId, 
						this.getOpUserName(), this.thisOpId.getOpName());		
				
				 updateObsProperty(SCHEMEBOARD_OBS_PROP4, this.myControler.playableMissions);
			     updateObsProperty(SCHEMEBOARD_OBS_PROP5, this.myControler.playersOfMission);
			       
			     if(myControler.updateSchemeBoardState(missionId, this.thisOpId.getOpName()))
			        	updateObsProperty(SCHEMEBOARD_OBS_PROP9, this.myControler.schemeBoardStatus);
			       
				try {
					invokeOp(this.myControler.normativeBoardId, new Op(UpdateAgentStatusFromSchemeBoard_LinkOp, 
							this.getOpUserName(), missionId, LEAVE_MISSION_OP));
				} catch (Exception e) {					
					e.printStackTrace();
				}
				
			    signal(LEAVE_MISSION_EVENT,  this.getId().getName(), 
			      		this.thisOpId.getOpName()+"_op", missionId, "succeeded", this.getOpUserName());            
				}   
			}
		else {
			signal(LEAVE_MISSION_EVENT,  this.getId().getName(), 
					this.thisOpId.getOpName() + "_op", missionId, "failed", this.getOpUserName(), 
					"this mission does not exist in the specification of this SchemeBoard");
		}
		}
		else {
			signal(LEAVE_MISSION_EVENT,  this.getId().getName(), this.thisOpId.getOpName() + "_op", 
            		missionId, "failed" , this.getOpUserName(), "operation leaveMission is not activated");
		}
	
	}    
    
    
	/***********************************************************************************************************
	 * 
	 * @param goalId
	 * 
	 *************************************************************************************************************/
	@OPERATION(states={"active"}) void setGoalAchieved(String goalId){ //(states={"wellFormed"})
		
		if((this.myControler.schemeBoardStatus==OrgArtState.active) 
		   || (this.myControler.schemeBoardStatus==OrgArtState.wellFormed)) {
				
			this.myControler.updateCurrentAchievableGoal(goalId);
			setGoalState(this.getOpUserName(), goalId, ACHIEVED);	
		
		}else		
			signal(SET_GOAL_ACHIEVED_EVENT,  this.getId().getName(), this.thisOpId.getOpName() + "_op", 
            		goalId, "failed", this.getOpUserName(), "operation setGoalAchieved is not activated");
		
	}
	
	/**
	 * 
	 * @param goalId
	 */
	@OPERATION(states={"active"}) void setGoalImpossible(String goalId){
		
		if((this.myControler.schemeBoardStatus== OrgArtState.active) 
		   || (this.myControler.schemeBoardStatus==OrgArtState.wellFormed)) {
			
			this.myControler.updateCurrentAchievableGoal(goalId);
			setGoalState(this.getOpUserName(), goalId, IMPOSSIBLE);
		
		}else		
			signal(SET_GOAL_IMPOSSIBLE_EVENT,  this.getId().getName(), this.thisOpId.getOpName() + "_op", 
					goalId, "failed", this.getOpUserName(), "operation setGoalImpossible is not activated");
		
	}
	
	/**************************************************************************
	 * 
	 * @param agName
	 * @param goalId
	 * @param state
	 ***************************************************************************/
	private void setGoalState(String agName, String goalId, String state){
		// Verify that the agent who is calling the operation belong to the responsible group of the scheme
		String event;
		if(state.equals(ACHIEVED))
			event = SET_GOAL_ACHIEVED_EVENT;
		else
			event = SET_GOAL_IMPOSSIBLE_EVENT;
		
		if(!this.myControler.goalsInstance.containsKey(goalId)) {
			signal(event, this.getId().getName(), this.thisOpId.getOpName() + "_op", goalId, 
					"failed", this.getOpUserName(), "the goal does not exist " + "in this SchemeBoard"); 
		
		}
		else if (this.myControler.goalsInstance.get(goalId).getGoalSpec().hasPlan()) {
			signal(event, this.getId().getName(), this.thisOpId.getOpName() + "_op", goalId, 
					"failed", this.getOpUserName(), "you cannot set the state of a plan"); 
		}
		else 
			if(this.myControler.goalsInstance.get(goalId).getGoalState().equals(GoalInstance.State.waiting)) {			
				signal(event, this.getId().getName(), this.thisOpId.getOpName() + "_op", goalId, 
						   "failed", this.getOpUserName(), "the goal state is \"waiting\"" ); 
			
			}   
			else if(this.myControler.goalsInstance.get(goalId).getGoalState().equals(GoalInstance.State.achieved)) {		   
					signal(event, this.getId().getName(), this.thisOpId.getOpName() + "_op", goalId, 
				   "failed", this.getOpUserName(), "the goal state is \"achieved\""); 
			}
			else if (this.myControler.goalsInstance.get(goalId).getGoalState().equals(GoalInstance.State.impossible)) {
						signal(event, this.getId().getName(), this.thisOpId.getOpName() + "_op", goalId, 
								"failed", this.getOpUserName()," the goal state is \"impossible\"");  
			}  
			else {
			
				if(this.myControler.updateGoalState(goalId, agName, state)) {
					updateObsProperty(SCHEMEBOARD_OBS_PROP6, this.myControler.goalsStatus);
					
					signal(event,  this.getId().getName(), this.thisOpId.getOpName() + "_op",
							goalId, "succeeded", this.getOpUserName(),"State = " + state);	
					
					for(String s:this.myControler.currentAchievableGoals)
						signal(NEW_POSSIBLE_GOAL_EVENT,  this.getId().getName(), this.thisOpId.getOpName()+"_op", s);
					}
				else {	//|| !this.getResponsibleOrgAgents().contains(this.thisOpId.getOpName())){
					
					signal(event, this.getId().getName(), this.thisOpId.getOpName() + "_op",
							goalId , "failed", this.getOpUserName(),
							"the agent has not commited the mission managing this goal");  
				}
			}	
			 
		//}		
	}
	
    
    /********************************************************************************************************************************
     * 
     * LINK OPERATIONS
     * 
     ********************************************************************************************************************************/    
     
   /************************************************************************
    * 
    * @param agentName
    * @param missionsId
    ************************************************************************/
   @LINK(states={"active"}) void updateCommittedMissionsForLeaveRoleLinkOp(String agentName, List<String> missionsId) {
	    
	   for(String mId : missionsId) {		   
		   if(this.myControler.missionsInstance.get(mId).getCommittedAgents().contains(agentName)) {
			   this.myControler.updateMissionsPropertiesValues(agentName, mId, "leaveMission");
		   }			   
	   }
   }
	
   /***********************************************************
    * 
    * @param normativeBoardId
    ***********************************************************/
   @LINK(states={"active"}) void setNormativeBoardLinkOp(ArtifactId normativeBoardId){   	
   		this.myControler.normativeBoardId = normativeBoardId;   
   		defineObsProperty(SCHEMEBOARD_OBS_PROP10, this.myControler.normativeBoardId);
   }
	
}
