package ora4masMoise.SchemeBoard;

import java.util.*;

import ora4masMoise.SchemeBoard.GoalInstance.State;
import alice.cartago.*;

import moise.os.fs.Goal;
import moise.os.fs.Plan;



/**
 * Represents a plan instance inside a scheme
 * 
 * @author  kitio
 */
public class PlanInstance {	

	protected ArtifactId schemeBoardId;
	protected Plan planSpec = null;
	protected String targetGoalName;
	protected String currentGoalName = null;
    protected int currentGoalIndex;
    protected List<String> GoalsOfPlan = new LinkedList<String>();
    protected String fatherPlanInstanceName = null;
    protected State planState = State.waiting;
	//protected boolean planExecutionFinished = false;
	
	/*protected GoalInstance targetGoalInstance;
	protected PlanInstance fatherPlanInstance;    
    protected GoalInstance  currentExecutedGoal = null;
    protected List<GoalInstance> planGoals = new LinkedList<GoalInstance>(); */
    
    public PlanInstance(Plan p, String targetGoalInstName, ArtifactId  schBoardId) {
    	this.planSpec = p;
        schemeBoardId = schBoardId; 
        targetGoalName = targetGoalInstName;
        
        for(Goal g:planSpec.getSubGoals()) 
        	GoalsOfPlan.add(g.getId());
        
        currentGoalIndex = 0;
        currentGoalName = GoalsOfPlan.get(currentGoalIndex);
        
    }   
    
    public Plan getPlanSpec() {
        return planSpec;
    }
    
    public void setFatherPlanInstanceName(String planName) {
    	fatherPlanInstanceName = planName;
    }
    
    public String getFatherPlanInstanceName() {
        return fatherPlanInstanceName;
    }
    
    protected String getTargetGoalName() {
        return this.targetGoalName;
    }  
       
    public List<String> getListOfGoals(){
		return this.GoalsOfPlan;
	}
    
    protected String getCurrentGoalName() {
        return this.currentGoalName;
    } 
    
    protected String getNextGoalName(){
    	this.currentGoalIndex++;
    	if(this.currentGoalIndex < this.GoalsOfPlan.size()) 
    		return this.GoalsOfPlan.get(currentGoalIndex);    	
    	else 
    		return null;    	
    }
    
    public void setPlanState(State state) { 	
		planState = state;			
    }
    
    public State getPlanState() { 	
		return planState;			
    }
    
    protected boolean isFinished() {
    	if((this.currentGoalIndex + 1) == this.GoalsOfPlan.size())
    		return true;
    	else
    		return false;
    }
    
    /*public PlanInstance(Plan p, GoalInstance targetGoalInst, ArtifactId  schBoardId) {
        this.planSpec = p;
        schemeBoardId = schBoardId; 
        targetGoalInstance = targetGoalInst; 
        
        for(Goal g:planSpec.getSubGoals()) {        	
        	GoalInstance gInstance = new GoalInstance(g, schemeBoardId);
        	gInstance.setInPlan(this);
        	planGoals.add(gInstance);
        	
        }
        currentExecutedGoalIndex = 0;
        //currentExecutedGoal = planGoals.get(currentExecutedGoalIndex);
    }
    
    public void setFatherPlanInstance(PlanInstance pInst) {
    	fatherPlanInstance = pInst;
    }
    
    public PlanInstance getFatherPlanInstance() {
        return fatherPlanInstance;
    }
        
    protected GoalInstance getCurrentExecutedGoal() {
        return currentExecutedGoal;
    }  
    
    protected GoalInstance getTargetGoalInstance() {
        return this.targetGoalInstance;
    }  
       
    public List<GoalInstance> getGoals(){
		return planGoals;
	}    
    
    protected GoalInstance getNextGoalToExecute(){
    	
    	/*if(planGoals.get(currentExecutedGoalIndex).isAchieved() || 
    	   planGoals.get(currentExecutedGoalIndex).isImpossible()){ 
    		
    		currentExecutedGoalIndex++; 
    		System.out.println("NextExecutedGoalIndex " + currentExecutedGoalIndex);
    		
    		if(currentExecutedGoalIndex < planGoals.size()) {
    			this.currentExecutedGoal = planGoals.get(currentExecutedGoalIndex);
    			return this.currentExecutedGoal;    			
    		}else {
    			System.out.println("null");
    			return null;
    		}
    	}
    	else {
    		return this.currentExecutedGoal;
    	}
    	return null;
    }*/
    
    /*protected boolean isFinished() {
    	System.out.println(currentExecutedGoalIndex + " " + this.planGoals.size());
    	if(this.currentExecutedGoalIndex == this.planGoals.size())
    		return true;
    	else
    		return false;
    }*/
    
    public String toString() {
        return "*"+planSpec.toString();
    }    
}
