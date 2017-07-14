package ora4masMoise.SchemeBoard;

import java.util.Collection;
import java.util.HashMap;

import java.util.Map;
import alice.cartago.*;
import moise.common.*;
import moise.os.fs.Goal;



/**
 * Represents a goal instance (in a scheme instance)
 *
 * @author kitio
 */
@SuppressWarnings("serial")
public class GoalInstance {
    
	//private static final long serialVersionUID = 1L;
	public enum State {waiting, possible, achieved, impossible};
	
	protected Goal goalSpec = null;
	protected ArtifactId schemeBoardId;
	private State goalState; 
    //protected PlanInstance inPlan = null; // the plan this goal belongs to
    //protected PlanInstance goalPlanInstance = null;   // the plan to achieve this goal
    protected String inPlanName = null;
   
    
    protected Map<String, GoalInstance.State> satisfiedAgents;  // committed agents that satisfied this goal

    public GoalInstance(Goal gSpec, ArtifactId  schBoardId) {
    	
        this.schemeBoardId  = schBoardId;
        this.goalSpec = gSpec;        
        satisfiedAgents = new HashMap<String, GoalInstance.State>();
        goalState = State.waiting; 
        //inPlan = pInstance;        
    }
    
    
    /**
     * sets that this goal is achieved by the agent a.
     * @param agId
     */
    public void setStateOfGoal(String agName, State state) {        
		satisfiedAgents.put(agName, state);
		goalState = state;			
    }
    
    /**
     * This method is used to set the state of goals which has plan
     * since state of these goals are automatically set according to 
     * the type of their plan operation (sequence, choice, parallel) 
     * @param state
     */
    public void setStateOfGoal(State state) { 	
		goalState = state;			
    }
   
    
	/**
	 * 
	 * a goal is satisfied if enough committed agents have set it as satisfied 
	 * */
   public boolean isAchieved() {  
	   if (goalState.equals(State.achieved))
   		return  true;
   	else     		 
   		return false;
   }
	
    public boolean isPossible() { 
    	if (goalState.equals(State.possible))
    		return  true;
    	else     		 
    		return false;
    }
    
   /**
    * 
    */
    public boolean isImpossible() {
    	if (goalState.equals(State.impossible))
    		return  true;
    	else     		 
    		return false;
    }    
     
    /**
     * 
     * @return
     */
    public Collection<String> getAgents() { 
    	return satisfiedAgents.keySet(); 
    }
    
    /**
     * 
     * @return
     */
    public Map<String, GoalInstance.State> getSatisfiedAgents(){
    	return satisfiedAgents;
    }
    
    /**
     * 
     * @return
     */
    /*public boolean hasSuperGoalSatisfied() {
        if (inPlan != null) {
            GoalInstance head = inPlan.getCurrentExecutedGoal();
            if (head != null) {
                if (head.isAchieved()) {
                    return true;
                } else {
                    return head.hasSuperGoalSatisfied();
                }
            }
        }
        return false;
    }
    
    /**
     * 
     * @return
     */
    /*public boolean hasSuperGoalImpossible() {
        if (inPlan != null) {
            GoalInstance head = inPlan.getCurrentExecutedGoal();
            if (head != null) {
                if (head.isImpossible()) {
                    return true;
                } else {
                    return head.hasSuperGoalImpossible();
                }
            }
        }
        return false;
    }*/
    
   
	/*public String getXMLTag() {
		return "goal";
	}

    public String toString() {
        return goalSpec.toString();
    }*/

	/*public PlanInstance getGoalPlanInstance() {
		return goalPlanInstance;
	}

	public void setGoalPlanInstance(PlanInstance plan) {
		this.goalPlanInstance = plan;
	}*/

	public Goal getGoalSpec() {
        return goalSpec;
    }
  
    public void setInPlanName(String inPlanName) {        
        this.inPlanName = inPlanName;
    }
    
    public String getInPlanName() {
    	return this.inPlanName;
    }
        
  	public State  getGoalState() {
		return goalState;
	}
  	
  	
  	
  	


	/*public PlanInstance getInPlan() {
		return inPlan;
	}*/


}
