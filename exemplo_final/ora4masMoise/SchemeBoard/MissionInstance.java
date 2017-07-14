package ora4masMoise.SchemeBoard;

import java.util.*;

import moise.common.MoiseConsistencyException;
import moise.os.Cardinality;
import moise.os.fs.*;
import alice.cartago.*;

/**
 * Represents a mission an agent is playing.
 *
 * @author kitio
 */
public class MissionInstance {

	//private static final long serialVersionUID = 1L;

	//private Scheme missionScheme;
	private Mission  missionSpec;
    private ArtifactId schemeBoardId;
    Set<String> commitedAgents = new HashSet<String>();
    //Set<String> goalsOfMission = new HashSet<String>();
    Cardinality missionCardinality;	
    //Map<String, GoalInstance> goalsOfMission = new HashMap<String, GoalInstance>();
    
    protected MissionInstance(Mission mission, ArtifactId schBoardId, Scheme sch)
    throws MoiseConsistencyException {    
    	
        if (mission == null) {
            throw new MoiseConsistencyException("mission can not be null!");
        }
        
        this.missionSpec = mission; 
        this. schemeBoardId = schBoardId;
        missionCardinality = sch.getMissionCardinality(missionSpec);
        
        /*for(Goal g:this.missionSpec.getGoals())
        	this.goalsOfMission.add(g.getId());*/
    }

    public Mission getMissionSpec() { return missionSpec; }
    public ArtifactId getSchemeBoardId() { return  schemeBoardId; }
    //public boolean isCommitted(){  return commitedAgents.size() > 0; }
    public Set<String> getCommittedAgents() { return commitedAgents;  }
    public Cardinality getCardinality() {return this.missionCardinality;}
    
   
    
	public void addPlayer(String agName){
		commitedAgents.add(agName);
	}

	
	
	/*public void addGoalInstance(String goalId, GoalInstance goalInstance){
		goalsOfMission.put(goalId, goalInstance);
	}*/
	
	protected boolean minCardinalityReached() {
		if(this.commitedAgents.size() == missionCardinality.getMin())
			return true;		
		else 
			return false;
	}
	
	protected int getMinCardinality() {
		return missionCardinality.getMin();
	}
	
	protected int getMaxCardinality() {
		return missionCardinality.getMax();
	}
	
	protected boolean isGoalOfMission(Goal goal) {
		if (this.missionSpec.getGoals().contains(goal))
			return true;
		else return false;
	}
	
	/*******************************************************************************
	 * 
	 * 
	 *******************************************************************************/
	/*@Override
	public boolean equals(Object obj) {
		if (this == obj)        return true;
		if (!super.equals(obj))	return false;
		if (getClass() != obj.getClass()) return false;
		final MissionInstance other = (MissionInstance) obj;
		if (missionSpec == null) {
			if (other.missionSpec != null)
				return false;
		} else if (!missionSpec.equals(other.missionSpec))
			return false;
		if ( schemeBoardId == null) {
			if (other. schemeBoardId != null)
				return false;
		} else if (! schemeBoardId.equals(other. schemeBoardId))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((missionSpec == null) ? 0 : missionSpec.hashCode());
		result = PRIME * result + (( schemeBoardId == null) ? 0 :  schemeBoardId.hashCode());
		return result;
	}
	
	 public String getXMLTag() {
			return "mission-player";
	}

	public Element getAsDOM(Document document) {
		Element mpEle = (Element) document.createElement(getXMLTag());
		mpEle.setAttribute("mission", getMission().getId());
		for(String s: commitedAgents)
			mpEle.setAttribute("agent", s);
		return mpEle;
	}*/
}
