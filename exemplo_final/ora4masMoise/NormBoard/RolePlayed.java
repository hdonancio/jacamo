package ora4masMoise.NormBoard;

import alice.cartago.ArtifactId;

public class RolePlayed {
	
	private String roleName; 
	private ArtifactId groupBoardId;

	/**
	 * 
	 * @param roleName
	 * @param grpBoardId
	 **/
	public RolePlayed(String roleName, ArtifactId grpBoardId) {
		this.roleName = roleName;
		groupBoardId = grpBoardId;
	}
	
	/**
	 * 
	 * @return
	 **/
	public String getRoleName() {
		return this.roleName;
	}
	
	/**
	 * 
	 * @return
	 **/
	public ArtifactId getGroupBoardId() {
		return this.groupBoardId;
	}

}
