/**
 * 
 */
package ora4masMoise.NormBoard;

import java.util.*;


/**
 * @author kitio
 *
 */
public class MemberAgentInstance {
	
	private String agentName;
	private ArrayList<RoleWithNormStatus> rolesWithNormStatus = new ArrayList<RoleWithNormStatus>();
	
	/**
	 * 
	 * @param agName
	 */
	public MemberAgentInstance(String agName) {
		agentName = agName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAgentName() {
		return agentName;
	}
	
	/**
	 * 
	 * 
	 * @param roleWithNormStatus
	 */
	public void addRoleWithNormStatus(RoleWithNormStatus roleWithNormStatus) {
		rolesWithNormStatus.add(roleWithNormStatus);
	}
	
	/**
	 * 
	 * @param index
	 */
	public void removeCommittedMission(int index) {
		rolesWithNormStatus.get(index).setNormStatusValue(RoleWithNormStatus.NormStatusValue.notCommited);
	}
	
	/**
	 * 
	 * @param roleWithNormStatus
	 */
	public void removeRoleWithNormStatus(RoleWithNormStatus roleWithNormStatus) {
		System.out.println("size21: " + getAgentRolesWithNormStatus().size());
		rolesWithNormStatus.remove(roleWithNormStatus);
		System.out.println("size22: " +getAgentRolesWithNormStatus().size());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<RoleWithNormStatus> getAgentRolesWithNormStatus() {
		return rolesWithNormStatus;
	}

}
