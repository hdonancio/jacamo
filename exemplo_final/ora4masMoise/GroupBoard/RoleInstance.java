package ora4masMoise.GroupBoard;

import java.util.*;

//import agents.MemberAgent;

import moise.os.Cardinality;
import moise.os.ss.*;
import alice.cartago.ArtifactId;

/**
 * @author kitio
 *
 */
@SuppressWarnings("serial")
public class RoleInstance extends Role{
	
	
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	ArtifactId myGroupBoardId;
	Cardinality roleCardinality;	
	Set<String> rolePlayers = new HashSet<String>();
	
	public RoleInstance(String id, SS ss, ArtifactId grpBoardId) {
		super(id, ss);		
		myGroupBoardId = grpBoardId;
	}
	
	protected void setRoleCardinality(Cardinality c){
		roleCardinality = c;
	}
	
	public Cardinality getRoleCardinality(){
		return roleCardinality;
	}
	
	public Set<String> getPlayersOfRole(){
		return rolePlayers;
	}	
	
	protected void addPlayer(String agName) { 
		rolePlayers.add(agName); 
	}
	
	protected void removePlayer(String agName) {
		rolePlayers.remove(agName);
	}
	
	public ArtifactId getMyGroupBoardId() {
		return myGroupBoardId;
	}
	
	protected boolean minCardinalityReached() {
		if(this.rolePlayers.size() == this.roleCardinality.getMin())
			return true;		
		else 
			return false;
	}
}
