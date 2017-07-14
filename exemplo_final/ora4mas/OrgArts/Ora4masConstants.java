/**
 * 
 */
package ora4mas.OrgArts;

/**
 * @author kitio
 *
 */
public class Ora4masConstants implements java.io.Serializable {
	/**
	 * Cartago some Constants
	 */
	public final static String LOOKUP_ARTIFACT = "lookupArtifact";
	
	public enum  OrgArtTYPE {OrgBoard, GroupBoard, SchemeBoard};
	
	public final static String OrgArt_TYPE1 = "OrgBoard";
	public final static String OrgArt_TYPE2 = "GroupBoard";
	public final static String OrgArt_TYPE3 = "SchemeBoard";
	public final static String OrgArt_TYPE4 = "NormativeBoard";
	
	
	/**************************************************************
	 * 
	 * OrgBoard Constants
	 * 
	 ***************************************************************/
	
	/**
	 * OrgBoard Observable properties Constants
	 */
	public final static String ORGBOARD_OBS_PROP1 = "orgBoardSpecification";
	public final static String ORGBOARD_OBS_PROP2 = "groupBoards";	
	public final static String ORGBOARD_OBS_PROP3 = "schemeBoards";
	public final static String ORGBOARD_OBS_PROP4 = "orgBoardStatus";
	//public final static String ORGBOARD_OBS_PROP4 = "orgAgents";
	//public final static String ORGBOARD_OBS_PROP5 = "memberAgents";	
	
	/**
	 * OrgBoard Operations Constants
	 */
	public final static String INIT_ORGBOARD_OP = "init";
	public final static String INIT_ORGBOARD_EVENT = "initOrgBoard_ev";	

	public final String MODIFY_SPEC_OP = "modifySpec";
	public final String MODIFY_SPEC_EVENT = "modifySpec_ev";	
	
	public final static String ENTER_ORG_OP = "enterOrg";
	public final static String ENTER_ORG_EVENT = "enterOrg_ev";
	
	public final static String LEAVE_ORG_OP = "leaveOrg";
	public final static String LEAVE_ORG_EVENT = "leaveOrg_ev";		
	
	public final static String REMOVE_GROUPBOARD_OP = "removeGroupBoard";
	public final static String REMOVE_GROUPBOARD_EVENT = "removeGroupBoard_ev";		
	
	public final static String REMOVE_SCHEMEBOARD_OP = "removeSchemeBoard";
	public final static String REMOVE_SCHEMEBOARD_EVENT = "removeSchemeBoard_ev";
	
	public final static String Get_MemberAgents_OP = "getMemberAgents";
	public final static String Get_MemberAgents_EVENT = "getMemberAgents_ev";
	
	public final static String Get_OrgAgents_OP = "getMemberAgents";
	public final static String Get_OrgAgents_EVENT = "getMemberAgents_ev";
	
	/**
	 * OrgBoard Linked Operations Constants
	 */
	public final static String GetGroupBoardSpecInOrgBoard_LinkOp = "getGroupBoardSpecLinkOp";
	public final static String GetGroupBoardSpecInOrgBoard_LinkOp_EVENT = "LinkOp_getGroupBoardSpec_ev";
	
	public final static String GetSchemeBoardSpecInOrgBoard_LinkOp = "getSchemeBoardSpecLinkOp";
	public final static String GetSchemeBoardSpecInOrgBoard_LinkOp_EVENT = "LinkOp_getSchemeBoardSpec_ev";
	
	public final static String ExistGroupBoardIdInOrgBoard_LinkOp = "existGroupBoardLinkOp";	
	//public final static String ExistGroupBoardIdInOrgBoard_LinkOp_EVENT = "LinkOp_existGroupBoard_ev";
	
	public final static String GetSchemeBoardIdInOrgBoard_LinkOp = "existSchemeBoardLinkOp";
	public final static String GetSchemeBoardIdInOrgBoard_LinkOp_EVENT = "LinkOp_existSchemeBoard_ev";	
	
	public final static String RegisterGroupBoard_LinkOp = "registerGroupBoardLinkOp";
	public final static String RegisterGroupBoard_LinkOp_EVENT = "LinkOp_registerGroupBoard_ev";
	
	public final static String RegisterSchemeBoard_LinkOp = "registerSchemeBoardLinkOp";
	public final static String RegisterSchemeBoard_LinkOp_EVENT = "LinkOp_registerSchemeBoard_ev";
	
	public final static String ExistSchemeBoardIdInOrgBoard_LinkOp = "existSchemeBoardLinkOp";
	
	public final static String UpdateAgRoleInOrgBoard_LinkOp = "updateAgRoleInOrgBoardLinkOp";
	public final static String UpdateAgRoleInOrgBoard_LinkOp_EVENT = "LinkOp_updateAgRoleInOrgBoard_ev";
	

	
	/**********************************************************************************
	 * 
	 * GroupBoard Constants
	 * 
	 **********************************************************************************/
	
	/**
	 * GroupBoard Observable Properties
	 */
	public final static String GROUPBOARD_OBS_PROP1 = "groupBoardType";
	public final static String GROUPBOARD_OBS_PROP2 = "groupBoardSpecification";
	public final static String GROUPBOARD_OBS_PROP3 = "playableRoles";	
	public final static String GROUPBOARD_OBS_PROP4 = "playersOfRole";
	public final static String GROUPBOARD_OBS_PROP5 = "orgBoardId";
	public final static String GROUPBOARD_OBS_PROP6 = "schemeBoards";	
	public final static String GROUPBOARD_OBS_PROP7 = "superGroupBoards";
	public final static String GROUPBOARD_OBS_PROP8 = "subGroupBoards";	
	public final static String GROUPBOARD_OBS_PROP9 = "dependentGroupBoards";
	public final static String GROUPBOARD_OBS_PROP10 = "groupBoardStatus";	
	
	/**
	 * GroupBoard Operations
	 */
	
	public final static String INIT_GROUPBOARD_OP = "initGroupBoard";
	public final static String INIT_GROUPBOARD_OPSTEP1 = "initGroupBoardStep1";
	public final static String INIT_GROUPBOARD_OPSTEP2 = "initGroupBoardStep2";
	public final static String INIT_GROUPBOARD_OPSTEP3 = "initGroupBoardStep3";
	public final static String INIT_GROUPBOARD_OPSTEP4 = "initGroupBoardStep4";
	public final static String INIT_GROUPBOARD_OPSTEP5 = "initGroupBoardStep5";
	public final static String INIT_GROUPBOARD_OPSTEP6 = "initGroupBoardStep6";
		
	public final static String INIT_GROUPBOARD_EVENT = "initGroupBoard_ev";
	
	public final static String ADOPT_ROLE_OP = "adoptRole";
	public final static String ADOPT_ROLE_EVENT = "adoptRole_ev";
	
	public final static String LEAVE_ROLE_OP = "leaveRole";
	public final static String LEAVE_ROLE_EVENT = "leaveRole_ev";
	
	public final static String ADD_SUB_GROUBOARD_OP = "addSubGroupBoard";
	public final static String ADD_SUB_GROUPBOARD_EVENT = "addSubGroupBoard_ev";
	
	public final static String ADD_RESP_SCHEMEBOARD_OP = "addSchemeBoard";
	public final static String ADD_RESP_SCHEMEBOARD_EVENT = "addSchemeBoard_ev";
	
	public final static String REMOVE_RESP_SCHEMEBOARD_OP = "removeSchemeBoard";
	public final static String REMOVE_RESP_SCHEMEBOARD_EVENT = "removeSchemeBoard_ev";
	
		
	/**
	 * GroupBoard Linked Operations
	 */
		
	
	public final static String AddSubGroupBoard_LinkOp = "addSubGroupBoardLinkOp";
	public final static String AddSubGroupBoard_LinkOp_EVENT = "LinkOp_addSubGroupBoard_ev";
	
	public final static String AddDependentGroupBoard_LinkOp = "addDependentGroupBoardLinkOp";
	public final static String AddDependentGroupBoard_LinkOp_EVENT = "LinkOp_addDependentGroupBoard_ev";
	
	
	public final static String UpdateAgRoleInSuperGroupBoard_LinkOp = "updateAgRoleInSuperGroupBoard";
	public final static String UpdateAgRoleInSuperGroupBoard_LinkOp_EVENT = "LinkOp_updateAgRoleInSuperGroupBoard_ev";
	
	public final static String ADD_RESP_SCHEMEBOARD_LinkOp = "addSchemeBoardLinkOp";
	public final static String ADD_RESP_SCHEMEBOARD_LinkOp_EVENT = "linkOp_addSchemeBoard_ev";
	
	public final static String REMOVE_RESP_SCHEMEBOARD_LinkOp = "removeSchemeBoardLinkOp";
	public final static String REMOVE_RESP_SCHEMEBOARD_LinkOp_EVENT = "LinkOp_removeSchemeBoard_ev";
	
	public final static String ModifyGroupSS_LinkOp = "modifyGroupSS";
	public final static String ModifyGroupSS_LinkOp_EVENT = "LinkOp_modifyGroupSS_ev";
	
	public final static String IsMemberOfGroupBoard_LinkOp = "isMemberOfGroupBoardLinkOp";
	public final static String GetRolesOfMember_LinkOp = "getRolesOfMemberLinkOp";
	public final static String GetMemberOfGroupBoard_LinkOp = "getMemberOfGroupBoardLinkOp";
	public final static String AddNormativeBoard_LinkOp = "addNormativeBoardLinkOp";
	
	
	/********************************************************************************************
	 * 
	 * SchemeBoard Constants
	 * 
	 ********************************************************************************************/
	
	public final static String ACHIEVED = "achieved";
	public final static String IMPOSSIBLE = "impossible";
	
	/**
	 * SchemeBoard Observable Properties Constants
	 */
	
	public final static String SCHEMEBOARD_OBS_PROP1 = "schemeBoardType";
	public final static String SCHEMEBOARD_OBS_PROP2 = "schemeBoardSpecification";	
	public final static String SCHEMEBOARD_OBS_PROP3 = "orgBoardId";	
	public final static String SCHEMEBOARD_OBS_PROP4 = "playableMissions";
	public final static String SCHEMEBOARD_OBS_PROP5 = "playersOfMission";
	public final static String SCHEMEBOARD_OBS_PROP6 = "goalsStatus";
	public final static String SCHEMEBOARD_OBS_PROP7 = "responsibleGroupBoards";
	public final static String SCHEMEBOARD_OBS_PROP8 = "dependentSchemeBoards";
	public final static String SCHEMEBOARD_OBS_PROP9 = "schemeBoardStatus";
	public final static String SCHEMEBOARD_OBS_PROP10 = "normativeBoard";
	
	/**
	 * SchemeBoard Operations Constants
	 */
	
	public final static String INIT_SCHEMEBOARD_OP = "initSchemeBoard";
	public final static String INIT_SCHEMEBOARD_OPSTEP1 = "initSchemeBoardStep1";
	public final static String INIT_SCHEMEBOARD_OPSTEP2 = "initSchemeBoardStep2";
	public final static String INIT_SCHEMEBOARD_OPSTEP3 = "initSchemeBoardStep3";
	public final static String INIT_SCHEMEBOARD_EVENT = "initSchemeBoard_ev";	
	
	public final static String ADD_RESP_GROUPBOARD_OP = "addResponsibleGroupBoard";
	public final static String ADD_RESP_GROUPBOARD_EVENT = "addResponsibleGroupBoard_ev";	
	
	public final static String REMOVE_RESP_GROUPBOARD_OP = "removeResponsibleGroupBoard";
	public final static String REMOVE_RESP_GROUPBOARD_EVENT = "removeResponsibleGroupBoard_ev";	
	
	public final static String COMMIT_MISSION_OP = "commitMission";
	public final static String COMMIT_MISSION_EVENT = "commitMission_ev";
	
	public final static String LEAVE_MISSION_OP = "leaveMission";
	public final static String LEAVE_MISSION_EVENT = "leaveMission_ev";
	
	
	public final static String NEW_POSSIBLE_GOAL_EVENT = "newPossibleGoal_ev";
	
	
	public final static String SET_GOAL_ACHIEVED_OP = "setGoalAchieved";
	public final static String SET_GOAL_ACHIEVED_EVENT = "setGoalAchieved_ev";
	
	public final static String SET_GOAL_IMPOSSIBLE_OP = "setGoalImpossible";
	public final static String SET_GOAL_IMPOSSIBLE_EVENT = "setGoalImpossible_ev";
	
	public final static String SET_GOAL_STATE_OP = "setGoalState";
	public final static String SET_GOAL_STATE_EVENT = "setGoalState_ev";
	
	
	/**
	 * SchemeBoard Links Operations Constants
	 */
	
	public final static String AddResponsibleGroupBoard_LinkOp = "addResponsibleGroupBoardLiknOp";
	public final static String AddResponsibleGroupBoard_LinkOp_EVENT = "LinkOp_addResponsibleGroupBoard_ev";
	
	public final static String RemoveResponsibleGroupBoard_LinkOp = "removeResponsibleGroupBoardLiknOp";
	public final static String RemoveResponsibleGroupBoard_LinkOp_EVENT = "LinkOp_removeResponsibleGroupBoard_ev";
	
	public final static String SetMembersOfRespGroupBoard_LinkOp = "setMembersOfRespGroupBoard";
	public final static String SetMembersOfRespGroupBoard_LinkOp_EVENT = "LinkOp_setMembersOfRespGroupBoard_ev";
		
	public final static String ModifySchemeFS_LinkOp = "modifySchemeFS";
	public final static String ModifySchemeFS_LinkOp_EVENT = "LinkOp_modifySchemeFS_ev";

	public final static String UpdateCommittedMissionForLeaveRole_LinkOp = "updateCommittedMissionsForLeaveRoleLinkOp";
	public final static String UpdateCommittedMissionForLeaveRole_LinkOp_EVENT = "LinkOp_updateCommittedMissionForLeaveRole_ev";
	public final static String SetNormativeBoard_LinkOp = "setNormativeBoardLinkOp";
	
	/********************************************************************************************
	 * 
	 * NormativeBoard Constants
	 * 
	 ********************************************************************************************/
	
	/**
	 * 
	 * NormativeBoard Observable Properties Constants
	 * 
	 */
	
	public final static String NORMATIVEBOARD_OBS_PROP1 = "NormativeBoardSpecification";	
	public final static String NORMATIVEBOARD_OBS_PROP2 = "orgBoardId";
	public final static String NORMATIVEBOARD_OBS_PROP3 = "schemeBoardId";
	public final static String NORMATIVEBOARD_OBS_PROP4 = "groupBoards";
	public final static String NORMATIVEBOARD_OBS_PROP5 = "agentsStatus";
	public final static String NORMATIVEBOARD_OBS_PROP6 = "normativeBoardStatus";	
	
	/**
	 * NoarmativeBoard Operations Constants
	 */
	
	public final static String INIT_NORMATIVEBOARD_OP = "initNormativeBoard";	
	public final static String INIT_NORMATIVEBOARD_OPSTEP1 = "initNormativeBoardStep1";
	public final static String INIT_NORMATIVEBOARD_OPSTEP2 = "initNormativeBoardStep2";
	public final static String INIT_NORMATIVEBOARD_OPSTEP3 = "initNormativeBoardStep3";
	public final static String INIT_NORMATIVEBOARD_EVENT = "initNormativeBoard_ev";
	
	public final static String Add_GroupBoard_InNormativeBoard_OP = "addGroupBoard";	
	public final static String Add_GroupBoard_inNormativeBoard_EVENT = "addGroupBoard_ev";
	
	/**
	 * NoarmativeBoard Link Operations Constants
	 */
	
	/**
	 * Link Operations Constants of NoarmativeBoard exposed by the other OrgArts
	 */
	public final static String GetNormativeBoardSpec_LinkOp = "getNormativeBoardSpecLinkOp";
	public final static String UpdateAgentStatusFromGroupBoard_LinkOp = "updateAgentStatusFromGroupBoardLinkOp";	
	public final static String UpdateAgentStatusFromSchemeBoard_LinkOp = "updateAgentStatusFromSchemeBoardLinkOp";
	
	//public final static String INIT_NORMATIVEBOARD_OPSTEP3 = "initNormativeBoardStep3";
	
	/*****************************************************************************************
	 * 
	 * Integer Constants
	 * 
	 *****************************************************************************************/
	
	public final static int OrgBoard_Id = 1;
	public final static int EnterOrg_Op = 10;
	public final static int LeaveOrg_Op = 11;
	//public final static int OrgBoard_ObserveProperties = 12;

	public final static int GroupBoard_Id = 2;
	public final static int AdoptRole_Op = 20;
	public final static int LeaveRole_Op = 21;
	//public final static int GroupBoard_ObserveProperties = 22;

	public final static int SchemeBoard_Id = 3;
	public final static int CommitMission_Op = 30;
	public final static int LeaveMission_Op = 31;
	public final static int SetGoalState_Op = 32;	
	
	/**
	 * 
	 * 
	 */
	
	public final static int ObsProperty_Type_OrgArtState = 00000; 
	public final static int ObsProperty_Type_OrgBoardState = 00001; 
	public final static int ObsProperty_Type_GroupState = 00010; 
	public final static int ObsProperty_Type_SchemeBoardState = 00011; 
	
	public final static int ObsProperty_Type_OS = 00100;
	public final static int ObsProperty_Type_SS = 000101;
	public final static int ObsProperty_Type_FS = 00110;
	public final static int ObsProperty_Type_DS = 00111;
	
	public final static int ObsProperty_Type_String = 01001;
	public final static int ObsProperty_Type_List = 01010;
	public final static int ObsProperty_Type_Map = 01011;
	public final static int ObsProperty_Type_Interger = 01100;
	
	public final static int ObsProperty_Type_RoleInstance = 01101;
	public final static int ObsProperty_Type_MemberAgentInstance = 01110;
	public final static int ObsProperty_Type_OrgAgentInstance = 01111; 
	public final static int ObsProperty_Type_GoalInstanceState = 10000; 

}
