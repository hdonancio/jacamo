package ora4masMoise.NormBoard;

import alice.cartago.ArtifactId;

/**
 * @author kitio
 *
 */
public class RoleWithNormStatus {
	public enum Norm {Obligation, Permission, No_Norm};
	public enum NormStatusValue {committed, notCommited, nullValue, violationOfNorm};
	
	RolePlayed rolePlayed;
	Norm normType = null;
	String missionId = null;
	NormStatusValue normStatusValue = null;
	
	/**
	 * 
	 * @param rolePlayed
	 * @param norm
	 * @param missionId
	 */
	public RoleWithNormStatus(RolePlayed rolePlayed, Norm norm, String missionId) {		
		this.rolePlayed = rolePlayed;
		normType = norm;
		this.missionId = missionId;		
	}

	public void setNormStatusValue(NormStatusValue value) {
		normStatusValue = value;
	}

	public Norm getNormType() {
		return normType;
	}
	
	public String getMissionId() {
		return missionId;
	}

	public NormStatusValue getNormStatusValue() {
		return normStatusValue;
	}
	
	public RolePlayed getRolePlayed() {
		return rolePlayed;
	}
	
	/**
	 * 
	 * @param roleName
	 * @param grpBoardId
	 * @return
	 */
	public boolean isRolePlayedParameters(String roleName, ArtifactId grpBoardId) {
		System.out.println("enter check");
		if(this.rolePlayed.getRoleName().equals(roleName) && 
				this.rolePlayed.getGroupBoardId().equals(grpBoardId))
			return true;		
		else
			return false;
		
		
	}
}
