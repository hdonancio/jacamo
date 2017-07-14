/**
 * 
 */
package ora4masMoise.SchemeBoard;

import static ora4mas.OrgArts.Ora4masConstants.*;
import ora4mas.OrgArts.OrgArtState;

import java.util.*;


import moise.common.MoiseConsistencyException;
import moise.os.fs.FS;
import moise.os.fs.Goal;
import moise.os.fs.Mission;
import moise.os.fs.Plan;
import moise.os.fs.Scheme;
import ora4mas.OrgArts.OrgArt;
import ora4masMoise.SchemeBoard.GoalInstance.State;
import alice.cartago.ArtifactId;


/**
 * @author kitio
 *
 */
public class SchemeBoardControler {
	
	
	
	Map<String, MissionInstance> missionsInstance = new HashMap<String, MissionInstance>();
	Map<String,PlanInstance> plansInstance = new LinkedHashMap<String, PlanInstance>();	
	
	Map<String, Integer> playableMissions = new HashMap<String, Integer>();
	Map<String, Set<String>> playersOfMission = new HashMap<String, Set<String>>();
	Map<String, GoalInstance.State> goalsStatus = new LinkedHashMap<String, GoalInstance.State>();
		
	Set<ArtifactId> responsibleGroupBoards = new HashSet<ArtifactId>();	
	Map<String, ArtifactId> dependentGroupBoards = new HashMap<String, ArtifactId>();
	Map<String, GoalInstance> goalsInstance = new LinkedHashMap<String, GoalInstance>();
	
	Set<String> currentAchievableGoals = new HashSet<String>();
	//String currentAchievableGoals;
	String schemeBoardType;
	FS schemeBoardSpecification;
	ArtifactId orgBoardId;
	static int schemeBoardStatus;		
	
	ArtifactId mySchemeBoardEntityId;
	
	GoalInstance rootGoal = null;	
	Scheme schemeSpec = null;
	ArtifactId firstResponsibleGroupBoardId;
	ArtifactId normativeBoardId;
		
	 /********************************************************************************************
	 * This operation initialize the SchemeBoard 
	 * by setting its the Scheme-Spec and the name to the SchemeBoard
	 * 
	 * @param schName
	 * @param sch 
	 *********************************************************************************************/
	 
	public SchemeBoardControler(String schSpecName, ArtifactId orgBoardId, 
			ArtifactId respGrpBoardId) {	
		
		
		this.orgBoardId = orgBoardId;
		firstResponsibleGroupBoardId = respGrpBoardId;
		schemeBoardType = schSpecName;	
		schemeBoardStatus = OrgArtState.created;
	}
	
	protected void setEntityId(ArtifactId entityId) {
		mySchemeBoardEntityId = entityId;
	}
	
	protected void setNormativeBoardId(ArtifactId normId) {
		this.normativeBoardId = normId;
	}
	
	protected void setSchemeBoardSpec(Scheme sch) {		
		this.schemeSpec = sch;
		this.schemeBoardSpecification = sch.getFS();
	}
	
	
	protected void initMissionsGoals(ArtifactId entityId) {
		
		MissionInstance missionInst;
		Set<String> players;
		for(Mission m: schemeSpec.getMissions()) {
			try {
				playableMissions.put(m.getId(), schemeSpec.getMissionCardinality(m).getMax());					
				
				players = new HashSet<String>();
				playersOfMission.put(m.getId(), players);
				
				missionInst = new MissionInstance(m, entityId, schemeSpec);						
				missionsInstance.put(m.getId(), missionInst);
			
			} catch (MoiseConsistencyException e) {						
				e.printStackTrace();
			}
			
	}			
		rootGoal = new GoalInstance(schemeSpec.getRoot(), entityId);
		initGoals(schemeSpec.getRoot(), null, entityId);
		
	}
	
	
	/************************************************************************************************
	 * 
	 * @param grpBoardName
	 * @param grpBoardId
	 * 
	 *************************************************************************************************/
	protected void addFirstResponsibleGroupBoard() {		
		responsibleGroupBoards.add(this.firstResponsibleGroupBoardId);				
	}
	
	
	protected boolean addResponsibleGroupBoard(ArtifactId grpBoardId) {			
		
		if(!this.responsibleGroupBoards.contains(grpBoardId)) {
			responsibleGroupBoards.add(grpBoardId);
			return true;
		}
		else
			return false;
				
	}
	
	/************************************************************************************************
	 *  
	 *  
	 * @param grpBoardId
	 *************************************************************************************************/
	protected boolean removeResponsibleGroupBoardOp(ArtifactId grpBoardId) {
		
		if(responsibleGroupBoards.contains(grpBoardId)) {			
			responsibleGroupBoards.remove(grpBoardId);	
			return true;
		}
		else
			return false;			
			
	}
	
	/************************************************************************
	 * Private method called to update OBSPROP playableRoles 
	 * and playersOfRole after a leave mission.
	 * 
	 * @param missionId
	 **************************************************************************/
    protected void updateMissionsPropertiesValues(String missionId, String agName, String op){ 
    	int newMaxCardMission ;
		if(op.compareTo("commitMission") == 0) {
			newMaxCardMission = playableMissions.get(missionId) - 1;		        	
			if(newMaxCardMission == 0)
				playableMissions.remove(missionId);
			else 
			    playableMissions.put(missionId, newMaxCardMission);		        	
			    		    
			this.missionsInstance.get(missionId).addPlayer(agName);		    
		    playersOfMission.get(missionId).add(agName);	
		}
		else if(op.compareTo("leaveMission") == 0) {
			if(playableMissions.containsKey(missionId)) {
				newMaxCardMission = playableMissions.get(missionId) + 1;	        	
		    	playableMissions.put(missionId, newMaxCardMission);
		    	
			}
			else {
				playableMissions.put(missionId, 1);			
			}
			
			this.missionsInstance.get(missionId).getCommittedAgents().remove(agName);		       	
			playersOfMission.get(missionId).remove(agName);
		}			
	}
	
	/*************************************************************************************************
	 * 
	 * @param missionId
	 **************************************************************************************************/
	@SuppressWarnings("unchecked")
	
	protected boolean checkAgentInPlayersOfMission(String missionId, String agName) {
		
		if(missionsInstance.get(missionId).getCommittedAgents().contains(agName))
				return true;
		else
			return false;
	}
	
	 
    
    
	/***********************************************************************************************************
	 * 
	 * @param goalId
	 * 
	 *************************************************************************************************************/
	protected boolean updateGoalState(String goalId, String agName, String state){ //(states={"wellFormed"})
		
	
		String missionOfGoal = this.getMissionOfGoal(this.goalsInstance.get(goalId).getGoalSpec());	
		//System.out.println("this.thisOpId.getOpName class: "+ this.thisOpId.getOpName().getClass());
		
		if(this.missionsInstance.get(missionOfGoal).getCommittedAgents().contains(agName)) {
			if(this.goalsInstance.get(goalId).isPossible()){
				//Map<String, State> newGoalsStatus = goalsStatus;
				
				if(state.equals(ACHIEVED)) { 
					this.goalsInstance.get(goalId).setStateOfGoal(agName, State.achieved);
					goalsStatus.put(goalId, GoalInstance.State.achieved);					
					
					updateGoalsStatus(this.goalsInstance.get(goalId));
				}
				else if(state.equals(IMPOSSIBLE)) {
					this.goalsInstance.get(goalId).setStateOfGoal(agName, State.impossible);
					goalsStatus.put(goalId, GoalInstance.State.impossible);
									
					updateGoalsStatus(this.goalsInstance.get(goalId));
				}
			}
			return true;
		}
			else
				return false;
	}
	
	/**
	 * 
	 * @param goalId
	 */
	protected void setGoalImpossible(String goalId, String agName){
		
		if((this.schemeBoardStatus== OrgArtState.active) 
		   || (this.schemeBoardStatus == OrgArtState.wellFormed)) 
			
			setGoalState(agName, goalId, IMPOSSIBLE);
		
		
	}
	
	/**************************************************************************
	 * 
	 * @param agName
	 * @param goalId
	 * @param state
	 ***************************************************************************/
	protected boolean setGoalState(String agName, String goalId, String state){
		// Verify that the agent who is calling the operation belong to the responsible group of the scheme
		String event;
		/*if(state.equals(ACHIEVED))
			event = SET_GOAL_ACHIEVED_EVENT;
		else
			event = SET_GOAL_IMPOSSIBLE_EVENT;*/
		
		if(!goalsInstance.containsKey(goalId)) {
			return false;
		}
		else if (goalsInstance.get(goalId).getGoalSpec().hasPlan()) {
			return false; 
		}
		else 
			if(goalsInstance.get(goalId).getGoalState().equals(GoalInstance.State.waiting)) {			
				return false;
			}   
			else if(goalsInstance.get(goalId).getGoalState().equals(GoalInstance.State.achieved)) {		   
					return false; 
			}
			else if (goalsInstance.get(goalId).getGoalState().equals(GoalInstance.State.impossible)) {
						return false;  
			}  
			else {
			GoalInstance goalInstance = goalsInstance.get(goalId);
			String missionOfGoal = this.getMissionOfGoal(goalInstance.getGoalSpec());	
			//System.out.println("this.thisOpId.getOpName class: "+ this.thisOpId.getOpName().getClass());
			
			if(this.missionsInstance.get(missionOfGoal).getCommittedAgents().contains(agName)) {
				if(this.goalsInstance.get(goalId).isPossible()){
					Map<String, State> newGoalsStatus = goalsStatus;
					
					if(state.equals(ACHIEVED)) { 
						this.goalsInstance.get(goalId).setStateOfGoal(agName, State.achieved);
						newGoalsStatus.put(goalId, GoalInstance.State.achieved);
						//updateObsProperty(SCHEMEBOARD_OBS_PROP6, newGoalsStatus);
						
						updateGoalsStatus(goalInstance);
					}
					else if(state.equals(IMPOSSIBLE)) {
						this.goalsInstance.get(goalId).setStateOfGoal(agName, State.impossible);
						newGoalsStatus.put(goalId, GoalInstance.State.impossible);
						//updateObsProperty(SCHEMEBOARD_OBS_PROP6, newGoalsStatus);
						
						updateGoalsStatus(goalInstance);
					}
					
					return true;				
			}
			}	else {	//|| !this.getResponsibleOrgAgents().contains(this.thisOpId.getOpName())){
					
					return false;  
				}
			}	
			 
		return true;		
	}
	
	/****************************************************************************************
     * 
     * private method called in @OPSTEP(tguard=1000) void initSchemeBoardStep3()
     * 
     ****************************************************************************************/
    protected void initGoals(Goal g, String inPlanName, ArtifactId id) {
    	
    	GoalInstance gi = new GoalInstance(g, id);
    	if(inPlanName != null) gi.setInPlanName(inPlanName);
    	
    	this.goalsInstance.put(g.getFullId(), gi);
		goalsStatus.put(g.getFullId(), GoalInstance.State.waiting);		
		//log("Goal: "+g.getFullId());
		
		if(g.hasPlan()) {
			Iterator <Goal> goalsPlanIt = g.getPlan().getSubGoals().iterator();		
			while (goalsPlanIt.hasNext()) {				
				initGoals(goalsPlanIt.next(), g.getFullId(), id);
			}			
		}		
	}    
    
	
	
	/****************************************************************************************
     * 
     * private method called in OPERATION "setGoalState"
     * 
     * @param goal
     * @return
     ****************************************************************************************/
    
    protected String getMissionOfGoal(Goal goal) {
    	String missionId = null;
    	Mission mission = null;
    	boolean exist = false;
		Iterator<Mission> it = schemeSpec.getMissions().iterator();
		
		//schemeInstance.getSpec().getMissions())
		while(!exist && it.hasNext()){
			mission = it.next();
			if (mission.getGoals().contains(goal))				
				exist = true;			
		}   
		if(exist) missionId = mission.getId();
        return missionId;
    }
    
    
    
   /********************************************************************************
    * 
    * @param goalInstance
    * @param fatherPlanName
    ********************************************************************************/
    protected void setPossibleGoals(GoalInstance goalInstance, String fatherPlanName){  
    	String goalName = goalInstance.getGoalSpec().getId(); 
    	
    	this.goalsInstance.get(goalName).setStateOfGoal(State.possible);
		this.goalsStatus.put(goalName, State.possible);
		
    	if(goalInstance.getGoalSpec().hasPlan()){
    		
    		Plan plan = goalInstance.getGoalSpec().getPlan();
    		PlanInstance planInstance = new PlanInstance(plan, 
    				goalName, mySchemeBoardEntityId);
    		this.plansInstance.put(goalName, planInstance);
    		
    		if(fatherPlanName != null) planInstance.setFatherPlanInstanceName(fatherPlanName);
    		
    		if(plan.getOp().equals(Plan.PlanOpType.sequence)){ 
    			setPossibleGoalsOfSequentialPlan(planInstance);
    		} 
    		else { 
    			setPossibleGoalsOfParallelOrChoicePlan(planInstance);
    		}    		
    	}
    	else {
    		this.currentAchievableGoals.add(goalName);
    		//currentAchievableGoals = goalName;
    	}
    }
    
    /**************************************
     * 
     * @param planInstance
     **************************************/
    protected void setPossibleGoalsOfParallelOrChoicePlan(PlanInstance planInstance){
    	GoalInstance goalInstance;
    	for (String gName: planInstance.getListOfGoals()) {
    		goalInstance = this.goalsInstance.get(gName);
    		setPossibleGoals(goalInstance, planInstance.getTargetGoalName());
		}
    }
    
    /***********************************
     * 
     * @param planInstance
     ***********************************/
    protected void setPossibleGoalsOfSequentialPlan(PlanInstance planInstance) {
    	GoalInstance goalInstance = goalsInstance.get(planInstance.getCurrentGoalName());
    	setPossibleGoals(goalInstance, planInstance.getTargetGoalName());
    }
    
    /**********************************
     * 
     * @param goalInstance
     **********************************/
    protected void updateGoalsStatus(GoalInstance goalInstance) { 
	   
	   String gName = goalInstance.getGoalSpec().getId();
	   if(goalInstance.getInPlanName() == null && 
		  gName.equals(rootGoal.getGoalSpec().getId())) {
		   
		   this.goalsInstance.get(gName).setStateOfGoal(State.achieved);
		   goalsStatus.put(gName, State.achieved);
		   
	   }
	   else if(goalInstance.getInPlanName()!= null && 
		   goalInstance.getGoalState().equals(State.achieved)) {
		   String  inPlanName = goalInstance.getInPlanName(); //log("precondition 1 Ok");
		   
		   if(this.plansInstance.get(inPlanName).getPlanSpec().getOp().equals(Plan.PlanOpType.parallel)) {
				  Iterator<String> planGoalsIt = this.plansInstance.get(inPlanName).getListOfGoals().iterator();
				  boolean b = true;
				  
				  while(b && planGoalsIt.hasNext())
					  if(this.goalsInstance.get(planGoalsIt.next()).getGoalState().equals(State.possible))
						  b = false;
				  
				  if(b) {
					  this.goalsInstance.get(inPlanName).setStateOfGoal(State.achieved);
					   goalsStatus.put(inPlanName, State.achieved);
					   this.plansInstance.get(inPlanName).setPlanState(State.achieved);
					   /*GoalInstance nextGoalInstance = goalsInstance.get(inPlanName);
					   setPossibleGoals(nextGoalInstance, inPlanName);*/
				  }
			  }
			  else  if(this.plansInstance.get(inPlanName).getPlanSpec().getOp().equals(Plan.PlanOpType.choice)) {
				  
				  this.goalsInstance.get(inPlanName).setStateOfGoal(State.achieved);
				   goalsStatus.put(inPlanName, State.achieved);
				   this.plansInstance.get(inPlanName).setPlanState(State.achieved);
				   /*GoalInstance nextGoalInstance = goalsInstance.get(inPlanName);
				   setPossibleGoals(nextGoalInstance, inPlanName);*/
			  }
			  else if(this.plansInstance.get(inPlanName).getPlanSpec().getOp().equals(Plan.PlanOpType.sequence)) {
				  if(this.plansInstance.get(inPlanName).isFinished()) 
					  this.plansInstance.get(inPlanName).setPlanState(State.achieved);
				  else {
					  String nextGoalName = this.plansInstance.get(inPlanName).getNextGoalName();
					   if(nextGoalName != null) {
						   GoalInstance nextGoalInstance = goalsInstance.get(nextGoalName);
						   setPossibleGoals(nextGoalInstance, inPlanName);
					   }
				  }
			  }
		   
		   if(this.plansInstance.get(inPlanName).getPlanState().equals(State.achieved)) { 
			   this.goalsInstance.get(inPlanName).setStateOfGoal(State.achieved);
			   goalsStatus.put(inPlanName, State.achieved);
			  
			   if(inPlanName.equals(this.rootGoal.getGoalSpec().getId())) {
				   this.rootGoal.setStateOfGoal(State.achieved);				   
				   this.schemeBoardStatus = OrgArtState.finished;
			   }
			   else { 
				   
				   String fatherPlanName = this.plansInstance.get(inPlanName).getFatherPlanInstanceName();
			   
				   if((fatherPlanName != null) && (!this.plansInstance.get(fatherPlanName).isFinished())) { 
					   
					   if(this.plansInstance.get(fatherPlanName).getPlanSpec().getOp().equals(Plan.PlanOpType.sequence)) {
							  String nextGoalName = this.plansInstance.get(fatherPlanName).getNextGoalName();
							  
							  if(nextGoalName != null) { 
								    GoalInstance nextGoalInstance = goalsInstance.get(nextGoalName);
								    setPossibleGoals(nextGoalInstance, fatherPlanName);
							  }
					   }					 						
				   }
			   }
		   }
	   }		   
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
   protected void updateCommittedMissionsForLeaveRole(String agentName, List<String> missionsId) {
	    
	   for(String mId : missionsId) {		   
		   if(this.missionsInstance.get(mId).getCommittedAgents().contains(agentName)) {
			   updateMissionsPropertiesValues(agentName, mId, "leaveMission");
		   }			   
	   }
   }
   
   /**
    * 
    * @param missionId
    * @param op
    * @return
    */
   protected boolean updateSchemeBoardState(String missionId, String op) {
	   
	   boolean result = false; 
	   if(op.compareTo("commitMission") == 0) { 
		   if(missionsInstance.get(missionId).minCardinalityReached()) { 
			   if(checkMissionsWellFormed() && this.schemeBoardStatus != OrgArtState.wellFormed) {
				   setPossibleGoals(rootGoal, null);		            		
				   this.schemeBoardStatus = OrgArtState.wellFormed;
				   result = true;
				   
           	}
		   }
		   
	   }
	   else if(op.compareTo("leveMission") == 0) {
			   if(!missionsInstance.get(missionId).minCardinalityReached() && 	 
				   this.schemeBoardStatus == OrgArtState.wellFormed) {
				   
				   this.schemeBoardStatus = OrgArtState.active;
				   result = true;
			   }
			   
	   }
	return result;
   }
   
   
   /************************************
    * 
    * @return
    ************************************/
   protected boolean checkMissionsWellFormed() {
   	boolean result = true;
	    Iterator<MissionInstance> missionInstanceIt = this.missionsInstance.values().iterator();
	    	
	    while(result && missionInstanceIt.hasNext())
	    	if(!missionInstanceIt.next().minCardinalityReached())
	    		result = false;	    	
	    
	    return result;   	
   }
	
   /***********************************************************
    * 
    * @param normativeBoardId
    ***********************************************************/
   protected void setNormativeBoard(ArtifactId normativeBoardId){   	
   		this.normativeBoardId = normativeBoardId;   	   		 
   }


   protected void updateSchemeBoardStatus(int state) {
	   this.schemeBoardStatus = state;
   }
   
   protected void updateCurrentAchievableGoal(String gName) {
	   this.currentAchievableGoals.remove(gName);
	   //currentAchievableGoals = "";
   }

}
