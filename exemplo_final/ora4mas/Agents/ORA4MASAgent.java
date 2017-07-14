/**
 * 
 */
package ora4mas.Agents;

import static ora4mas.OrgArts.Ora4masConstants.*;
/*import static ora4masMoise.GroupBoard.GroupBoardConstants.*;
import static ora4masMoise.OrgBoard.OrgBoardConstants.*;
import static ora4masMoise.SchemeBoard.SchemeBoardConstants.*;*/

import guiORA4MAS.AgentGUI;

import java.util.*;

import ora4mas.OrgArts.*;
import ora4masMoise.GroupBoard.*;
import ora4masMoise.OrgBoard.*;
import ora4masMoise.SchemeBoard.*;

import moise.common.MoiseConsistencyException;
import moise.os.CardinalitySet;
import moise.os.OS;
import moise.os.fs.Scheme;
import moise.os.ss.Group;
import moise.os.ss.SS;

import alice.cartago.*;
import alice.cartago.Artifact.*;

import alice.cartago.util.Agent;


/**
 * @author kitio
 *
 */
public class ORA4MASAgent extends Agent{

	/**
	 * ORA4MAS agent attributes
	 */
	//List<PlayerRole> membAgRoles = new ArrayList<PlayerRole>();
	
	//List of all the artifact that the agent 
	Map<String, ArtifactId> myOrgArtId = new HashMap<String, ArtifactId>();
	AgentGUI myFrame;
	
	/**
	 * Constructor
	 * @throws CartagoException 
	 */
	public ORA4MASAgent(String agName) throws CartagoException { // ICartagoWorkspace wsp
		super(agName);
		myFrame = new AgentGUI(this);
	}
	
	
	
	/**
	 * 
	 */
	public void run() {		
		try {			
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Creation of an OrgArt instance of type :
	 * "OrgBoard", "GroupBoard", "SchemeBoard", "NormativeBoard"
	 * @param opArguments
	 */
	public void createOrgArt(List<String> opArguments){
		//String orgArtType = opArguments.get(0);
		/**
		 * Case OrgArt_TYPE1: OrgBoard
		 */
		/*if(orgArtType.equals(OrgArt_TYPE1)) {
			createOrgBoard(opArguments.get(1), opArguments.get(2));
		}*/
		/**
		 * Case OrgArt_TYPE2: GroupBoard
		 */
		/*else if(orgArtType.equals(OrgArt_TYPE2)) {
			lunchCreateGroupBoard(opArguments);
		}*/
		/**
		 * Case OrgArt_TYPE3: SchemeBoard
		 */
		/*else if(orgArtType.equals(OrgArt_TYPE3)) {
			lunchCreateSchemeBoard(opArguments);			
		}
		else */
		
		/**
		 * Case OrgArt_TYPE3: NormativeBoard
		 */
		/*else if(orgArtType.equals(OrgArt_TYPE4)) {
			lunchCreateSchemeBoard(opArguments);			
		}
		else */
		
		myFrame.showErrorDialog("This operation is not yet implemented");			
	}	
	
	/**
	 * 
	 * Creation of an OrgBoard
	 */
	private void createOrgBoard(String osXmlURI, String orgBoardName){	
		
		ArtifactId orgBoardId;		
		String orgArtName;
		
		if(!orgBoardName.equals(""))
			orgArtName = "OrgBoard_" + orgBoardName;  
		else
			orgArtName = "OrgBoard"; 
		
		myFrame.setTextInPanel("\n\nCreation  & Initialisation of the " + orgArtName +" started");
		
		try {
			orgBoardId = makeArtifact(orgArtName, "ora4masMoise.OrgBoard.OrgBoard", new ArtifactConfig(osXmlURI));			
						
			if(!orgBoardId.equals(null)){
				use(orgBoardId, new Op("configure"));
				myOrgArtId.put(orgArtName, orgBoardId);
				myFrame.updateJList(orgArtName);
			}
			else{
				myFrame.showErrorDialog("An exception occured durring " +
						"Creation of OrgBoard \""+orgArtName+"\"");
			}
			myFrame.setTextInPanel("\n\n Creation  & Initialisation of the "
					+ orgArtName + " finished\n");			
		} catch (Exception e) {			
			myFrame.showErrorDialog("An exception occured durring " +
					"Creation of OrgBoard \""+orgArtName+"\"");
			e.printStackTrace();
		} 
	}
	
	/**
	 * Method called to Create a GroupBoard
	 * @param opArguments
	 */
	public void lunchCreateGroupBoard(List<String> opArguments) {
		
		String ownerOrgBoardName = opArguments.get(3);
		ArtifactId ownerOrgBoardId = null;
		//String superGroupBoardName  = null;
		ArtifactId superGroupBoardId  = null;
		//ArtifactId orgArtId = null;
		
		try {
			ownerOrgBoardId = lookUpOrgArt(opArguments.get(3));
			superGroupBoardId  = lookUpOrgArt(opArguments.get(5));
		} catch (Exception e1) {
			myFrame.showErrorDialog("An exception occured durring lookup of OrgBoard named " + ownerOrgBoardName);
			e1.printStackTrace();
		}
		
		if(!ownerOrgBoardId.equals(null)){
			
			String groupSpecName = opArguments.get(4);
			String groupBoardName = opArguments.get(2);
			
			try {
				createGroupBoard(groupSpecName, groupBoardName, ownerOrgBoardId, superGroupBoardId);				
			} catch (Exception e) {	
				myFrame.showErrorDialog("An exception occured durring GroupBoard creation execution");
				e.printStackTrace();
			}			
		}
		else{
			myFrame.showErrorDialog("Incorrect OrgBoard Name");
		}
	}
	
	/**
	 * Creation of a GroupBoard
	 * 
	 * @throws MoiseConsistencyException 	
	 * @param groupSpecName
	 * @param groupBoardName
	 * @param ownerOrgBoardId
	 * @param superGrpBoardId
	 * 
	 */
	private void createGroupBoard(String groupSpecName, String groupBoardName, 
			String ownerOrgBoardName, String superGrpBoardName) {
		
		ArtifactId groupBoardId;		
		String orgArtName;
		
		SensorId orgSensorId = null;
		Perception orgPerception;		
		try {
			orgSensorId = linkDefaultSensor();
		} catch (CartagoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(!groupBoardName.equals(""))
			orgArtName = "GroupBoard_" + groupSpecName + "_" +groupBoardName; //group.getFullId() + "_" +
		else
			orgArtName = "GroupBoard_" + groupSpecName;
		
		myFrame.setTextInPanel("\n\nCreation & Initialisation of the " + orgArtName + " started");
		
		try {
			groupBoardId = makeArtifact(orgArtName, "ora4masMoise.GroupBoard.GroupBoard");
			//new ArtifactConfig(ownerOrgBoardId, groupSpecName, superGrpBoardId)
			
			if(!groupBoardId.equals(null)) {
				
				use(groupBoardId, new Op("configure", ownerOrgBoardName, groupSpecName, superGrpBoardName), orgSensorId);
				//use(groupBoardId, new Op(INIT_GROUPBOARD_OP), orgSensorId);
				orgPerception = sense(orgSensorId, INIT_GROUPBOARD_EVENT);
				myFrame.printEvent(orgPerception); 
				
				if(orgPerception.getContent(2).equals("succeeded")) { //!groupBoardId.equals(null)
					myOrgArtId.put(orgArtName, groupBoardId);
					myFrame.updateJList(orgArtName);
				}
				else {
					myFrame.showErrorDialog("An exception occured durring initialisation " +
							"of GroupBoard");
					//this.env.disposeArtifact(groupBoardId);
				}
			}
			myFrame.setTextInPanel("\n\nCreation & Initialisation of the " 
					+ orgArtName + " finished\n");
			
		} catch (Exception e) {			
			e.printStackTrace();
		}         
	}	
	/**
	 * Creation of GroupBoard with names of linked OrgArt although of ArtifactId OrgtArt,
	 * @param groupSpecName
	 * @param groupBoardName
	 * @param ownerOrgBoardId
	 * @param superGrpBoardId
	 */
	private void createGroupBoard(String groupSpecName, String groupBoardName, 
			ArtifactId ownerOrgBoardId, ArtifactId superGrpBoardId) {
		
		ArtifactId groupBoardId;		
		String orgArtName;
		
		SensorId orgSensorId = null;
		Perception orgPerception;		
		try {
			orgSensorId = linkDefaultSensor();
		} catch (CartagoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(!groupBoardName.equals(""))
			orgArtName = "GroupBoard_" + groupSpecName + "_" +groupBoardName; //group.getFullId() + "_" +
		else
			orgArtName = "GroupBoard_" + groupSpecName;
		
		myFrame.setTextInPanel("\n\nCreation & Initialisation of the " + orgArtName + " started");
		
		try {
			groupBoardId = makeArtifact(orgArtName, "ora4masMoise.GroupBoard.GroupBoard");
			//new ArtifactConfig(ownerOrgBoardId, groupSpecName, superGrpBoardId)
			
			if(!groupBoardId.equals(null)) {
				
				use(groupBoardId, new Op("configure", ownerOrgBoardId, groupSpecName, superGrpBoardId), orgSensorId);
				//use(groupBoardId, new Op(INIT_GROUPBOARD_OP), orgSensorId);
				orgPerception = sense(orgSensorId, INIT_GROUPBOARD_EVENT);
				myFrame.printEvent(orgPerception); 
				
				if(orgPerception.getContent(2).equals("succeeded")) { //!groupBoardId.equals(null)
					myOrgArtId.put(orgArtName, groupBoardId);
					myFrame.updateJList(orgArtName);
				}
				else {
					myFrame.showErrorDialog("An exception occured durring initialisation " +
							"of GroupBoard");
					//this.env.disposeArtifact(groupBoardId);
				}
			}
			myFrame.setTextInPanel("\n\nCreation & Initialisation of the " 
					+ orgArtName + " finished\n");
			
		} catch (Exception e) {			
			e.printStackTrace();
		}         
	}	
	
	/**
	 * method called t lunch creation of a schemeboard
	 * @param opArguments
	 */
	public void lunchCreateSchemeBoard(List<String> opArguments) {		
		
		String ownerOrgBoardName = opArguments.get(3);
		ArtifactId ownerOrgBoardId = null;
		
		String firstRespGroupBoardName = opArguments.get(5);
		ArtifactId firstRespGroupBoardId = null;
		//Perception orgPerception = null;
		//ArtifactId orgArtId = null;
		
		try {
			ownerOrgBoardId = lookUpOrgArt(ownerOrgBoardName);
			firstRespGroupBoardId =  lookUpOrgArt(firstRespGroupBoardName);
		} catch (Exception e1) {
			myFrame.showErrorDialog("An exception occured durring lookup of " +
					"OrgBoard named " + ownerOrgBoardName);
			e1.printStackTrace();
		}
		
		if(!ownerOrgBoardId.equals(null)){
			try {
				
				String schemeSpecName = opArguments.get(4);
				String schemeBoardName = opArguments.get(2);
			
				createSchemeBoard(schemeSpecName, schemeBoardName, 
						ownerOrgBoardId, firstRespGroupBoardId);
				
			} catch (Exception e) {
				myFrame.showErrorDialog("An exception occured durring lunch of " +
						"SchemeBoard creation");
				e.printStackTrace();
			}
		}
		else{
			myFrame.showErrorDialog("Incorrect OrgBoard Name");
		}
	}
	
	/**
	 *  Creation of a SchemeBoard
	 *  
	 * @param schemeSpecName
	 * @param schemeBoardName
	 * @param ownerOrgBoardId
	 * @param respGroupBoardId
	 * 
	 */
	private void createSchemeBoard(String schemeSpecName, String schemeBoardName, 
			ArtifactId ownerOrgBoardId, ArtifactId respGroupBoardId) {
		
		ArtifactId schemeBoardId;
		String orgArtName;
		if(!schemeBoardName.equals(""))
			orgArtName = "SchemeBoard_" + schemeSpecName + "_" + schemeBoardName;
		else
			orgArtName = "SchemeBoard_" + schemeSpecName;
		
		myFrame.setTextInPanel("\nCreation & Initialisation of the " + orgArtName + " started\n");		
		SensorId orgSensorId = null;
		try {
			orgSensorId = linkDefaultSensor();
		} catch (CartagoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Perception orgPerception;		
		
		try {
			schemeBoardId = makeArtifact(orgArtName, "ora4masMoise.SchemeBoard.SchemeBoard");
					
			use(schemeBoardId,new Op("configure", ownerOrgBoardId, schemeSpecName, respGroupBoardId), orgSensorId);
			
			orgPerception = sense(orgSensorId, INIT_SCHEMEBOARD_EVENT);
			myFrame.printEvent(orgPerception);		
			
			myOrgArtId.put(orgArtName, schemeBoardId);
			myFrame.updateJList(orgArtName);
			
			myFrame.setTextInPanel("\nCreation & Initialisation of the " + orgArtName + " finished\n");
			
		} catch (Exception e) {	
			myFrame.showErrorDialog("An exception occured durring creation of SchemeBoard");
			e.printStackTrace();
		}         
	}
	
	/**
	 * Creation of SchemeBoard with names of linked OrgArt although of ArtifactId OrgtArt,
	 * @param schemeSpecName
	 * @param schemeBoardName
	 * @param ownerOrgBoardName
	 * @param respGroupBoardName
	 */
	private void createSchemeBoard(String schemeSpecName, String schemeBoardName, 
			String ownerOrgBoardName, String respGroupBoardName) {
		
		ArtifactId schemeBoardId;
		String orgArtName;
		if(!schemeBoardName.equals(""))
			orgArtName = "SchemeBoard_" + schemeSpecName + "_" + schemeBoardName;
		else
			orgArtName = "SchemeBoard_" + schemeSpecName;
		
		myFrame.setTextInPanel("\nCreation & Initialisation of the " + orgArtName + " started\n");		
		SensorId orgSensorId = null;
		try {
			orgSensorId = linkDefaultSensor();
		} catch (CartagoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Perception orgPerception;		
		
		try {
			schemeBoardId = makeArtifact(orgArtName, "ora4masMoise.SchemeBoard.SchemeBoard");
					
			use(schemeBoardId,new Op("configure", ownerOrgBoardName, schemeSpecName, respGroupBoardName), orgSensorId);
			
			orgPerception = sense(orgSensorId, INIT_SCHEMEBOARD_EVENT);
			myFrame.printEvent(orgPerception);		
			
			myOrgArtId.put(orgArtName, schemeBoardId);
			myFrame.updateJList(orgArtName);
			
			myFrame.setTextInPanel("\nCreation & Initialisation of the " + orgArtName + " finished\n");
			
		} catch (Exception e) {	
			myFrame.showErrorDialog("An exception occured durring creation of SchemeBoard");
			e.printStackTrace();
		}         
	}
	
	/**
	 * Creation of a NormativeBoard 
	 * 
	 * @param normativeBoardName
	 * @param ownerOrgBoardId
	 * @param schemeBoardId
	 * @param groupBoardId
	 */
	private void createNormativeBoard(String normativeBoardName, 
			ArtifactId ownerOrgBoardId, ArtifactId schemeBoardId, 
			Set<ArtifactId> groupBoardId) {
		
		ArtifactId normativeBoardId;
		String orgArtName = "NormativeBoard_" + normativeBoardName;
		
		myFrame.setTextInPanel("\nCreation & Initialisation of the " + orgArtName + " started\n");		
		SensorId orgSensorId = null;
		Perception orgPerception;	
		try {
			orgSensorId = linkDefaultSensor();
			
			//normativeBoardId = makeArtifact(orgArtName, "ora4masMoise.NormBoard.NormativeBoard", orgSensorId);
			
			normativeBoardId = makeArtifact(orgArtName, "ora4masMoise.NormBoard.NormativeBoard");
			
			use(normativeBoardId, new Op("configure", ownerOrgBoardId, schemeBoardId, groupBoardId), orgSensorId);
			
			orgPerception = sense(orgSensorId, INIT_NORMATIVEBOARD_EVENT);
			myFrame.printEvent(orgPerception);		
						
			myOrgArtId.put(orgArtName, normativeBoardId);
			myFrame.updateJList(orgArtName);
			
			myFrame.setTextInPanel("\nCreation & Initialisation of the " + orgArtName + " finished\n");
			
		} catch (Exception e) {	
			myFrame.showErrorDialog("An exception occured durring creation of NormativeBoard");
			e.printStackTrace();
		}         
	}
	/**
	 * Creation of NormativeBoard with names of linked OrgArt although of ArtifactId OrgtArt, 
	 * @param normativeBoardName
	 * @param ownerOrgBoardName
	 * @param schemeBoardName
	 * @param groupBoardsName
	 */
	private void createNormativeBoard(String normativeBoardName, 
			String ownerOrgBoardName, String schemeBoardName, 
			Set<String> groupBoardsName) {
		
		ArtifactId normativeBoardId;
		String orgArtName = "NormativeBoard_" + normativeBoardName;
		
		myFrame.setTextInPanel("\nCreation & Initialisation of the " + orgArtName + " started\n");		
		SensorId orgSensorId = null;
		Perception orgPerception;	
		try {
			orgSensorId = linkDefaultSensor();
			
			//normativeBoardId = makeArtifact(orgArtName, "ora4masMoise.NormBoard.NormativeBoard", orgSensorId);
			
			normativeBoardId = makeArtifact(orgArtName, "ora4masMoise.NormBoard.NormativeBoard");
			
			use(normativeBoardId, new Op("configure", ownerOrgBoardName, schemeBoardName, groupBoardsName), orgSensorId);
			
			orgPerception = sense(orgSensorId, INIT_NORMATIVEBOARD_EVENT);
			myFrame.printEvent(orgPerception);		
						
			myOrgArtId.put(orgArtName, normativeBoardId);
			myFrame.updateJList(orgArtName);
			
			myFrame.setTextInPanel("\nCreation & Initialisation of the " + orgArtName + " finished\n");
			
		} catch (Exception e) {	
			myFrame.showErrorDialog("An exception occured durring creation of NormativeBoard");
			e.printStackTrace();
		}         
	}
	
	
	/**
	 * Execution of the lookup of an OrgArt in the registry of the workspace
	 * @param orgArtName is the name of the OrgArt that the agent want to look up
	 * 
	 * @return
	 */
	private ArtifactId lookUpOrgArt(String orgArtName) {	
		ArtifactId orgArtId = null;
		try {
			orgArtId = lookupArtifact(orgArtName);
		} catch (Exception e) {
			myFrame.showErrorDialog("An exception occured durring lookup of OrgArt \"" 
					+ orgArtName + "\" in the REGISTRY of the workspace");
			e.printStackTrace();
		}
			/*
		Perception p = null;
		try {
			SensorId orgSensorId = linkDefaultSensor();
			use(REGISTRY,new Op(LOOKUP_ARTIFACT,orgArtName), orgSensorId);
			
			p = sense(orgSensorId, "locate_failed|lookup_succeeded");
			myFrame.printEvent(p);
		} catch (Exception e) {
			myFrame.showErrorDialog("An exception occured durring lookup of OrgArt \"" 
					+ orgArtName + "\" in the REGISTRY of the workspace");
			e.printStackTrace();
		}*/				
			
		return orgArtId;
	}
	
	/**
	 * Method called to lunch the lookup of an OrgArt with its given name
	 * @param orgArtName
	 */
	public void execLookupOrgArt(String orgArtName) {
		//SensorId orgSensorId = linkDefaultSensor();
		//Perception p = null;
		
		ArtifactId orgArtId = null;
		
		if(!myOrgArtId.containsKey(orgArtName)) {
			try {
				orgArtId = lookUpOrgArt(orgArtName);	
				
			} catch (Exception e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}					
	
			if(!orgArtId.equals(null)) {
				myOrgArtId.put(orgArtName, orgArtId);
				myFrame.updateJList(orgArtName);
			}						
		}
		//else 
			myFrame.setTextInPanel("Lookup OrgArt succeeded, OrgArtName: " + orgArtName 
					+ " OrgArtId: " + String.valueOf(myOrgArtId.get(orgArtName)));
	}
	
	/**
	 * Method called to lunch the execution of an OrgArt operation 
	 * 
	 * @param opName
	 * @param opArguments
	 * @param eventToSense
	 */
	public void execOrgArtOperation(String opName, 
			List<String> opArguments, String eventToSense) {
		
		ArtifactId orgArtId = null;
		
		if(opName.equals(ADD_RESP_SCHEMEBOARD_OP) || opName.equals(ADD_RESP_GROUPBOARD_OP))
			lunchSetRespOrgArt(opName, opArguments, eventToSense);
		
		else {
			String orgArtName = opArguments.get(0);
			opArguments.remove(orgArtName);
			//ArtifactId orgArtId = null;
			//Perception p= null;
			
			if(!myOrgArtId.containsKey(orgArtName)) {
				
				orgArtId = this.lookUpOrgArt(orgArtName);			
				if(!orgArtId.equals(null)) {					
					myOrgArtId.put(orgArtName, orgArtId);
					//myFrame.printEvent(p);	
					execOpOrgArt(orgArtId, opName, opArguments, eventToSense);
				}
				else {
					myFrame.showErrorDialog("An exception occured durring lookup OrgArt execution");
				}
			}
			else {
				orgArtId = myOrgArtId.get(orgArtName); //log("lookup succeeded " + String.valueOf(orgArtId));
				execOpOrgArt(orgArtId, opName, opArguments, eventToSense);
			}
		}		
	}
	
	/**
	 * Execution of an OrgArt operation 
	 * this operation may return the perception of the returned event 
	 * 
	 * 
	 * @param orgArtId
	 * @param opName
	 * @param opArguments
	 * @param eventToSense
	 */
	private Perception execOpOrgArt(ArtifactId orgArtId, String opName, 
			List<String> opArguments, String eventToSense) {
		
		Perception orgPerception= null;	
		try {
			SensorId orgSensorId = linkDefaultSensor();		
				
			Op op;
			if(opArguments.size()>=1) { 
				Object[] params = new Object[opArguments.size()];
				int i=0;
				for(String s: opArguments) {
					params[i] = s;  
					i++;
				}
				op = new Op(opName, params); 
			}
			else {
				op = new Op(opName); 
			}		
				
			use(orgArtId, op, orgSensorId); 
			orgPerception = sense(orgSensorId, eventToSense); 
			myFrame.printEvent(orgPerception);
				
		} catch (Exception e) {
			myFrame.showErrorDialog("An exception occured durring execOpOrgArt execution");	
			e.printStackTrace();
		}
		return orgPerception;
	}
	
	/**
	 * Method called to set the responsible GrouoBoard of a SchemeBoard 
	 * 
	 * @param opArguments
	 * @param eventToSense
	 */
	public void lunchSetRespOrgArt(String opName, List<String> opArguments, String eventToSense) {
		
		//Perception p1, p2;		
		String orgArtName1 = opArguments.get(0);
		ArtifactId orgArtId1=null;
		String orgArtName2 = opArguments.get(1);
		ArtifactId orgArtId2 = null;
		
		if(!myOrgArtId.containsKey(orgArtName1)) {
			orgArtId1 = this.lookUpOrgArt(orgArtName1);
			if(!orgArtId1.equals(null)) {			
				
				myOrgArtId.put(orgArtName1, orgArtId1);
				//myFrame.printEvent(p1);
			
			}else {
				myFrame.showErrorDialog("An exception occured durring lookup OrgArt execution");
			}
		}
		else {
			orgArtId1 = myOrgArtId.get(orgArtName1);
		}
		
		if(!orgArtId1.equals(null))
			if(!myOrgArtId.containsKey(orgArtName2)) {
				orgArtId2 = this.lookUpOrgArt(orgArtName2);
			
				if(!orgArtId2.equals(null)) {				
					
					myOrgArtId.put(orgArtName2, orgArtId2);
					//p1 = linkOrgArts(orgArtId1, orgArtId2); 
					//myFrame.printEvent(p1);
					
				
					execSetRespOrgArt(opName, orgArtName1, orgArtName2, orgArtId1 , orgArtId2, eventToSense);
				}
				else {
					myFrame.showErrorDialog("An exception occured durring lookup OrgArt execution");				
				}
			}
			else {
				orgArtId2 = myOrgArtId.get(orgArtName2);
				execSetRespOrgArt(opName, orgArtName1, orgArtName2, orgArtId1 , orgArtId2, eventToSense);
			}
	}
	
	/**
	 * Setting of the the responsible GrouoBoard of a SchemeBoard 
	 * 
	 * @param opName
	 * @param orgArtName1
	 * @param orgArtName2
	 * @param orgArtId1
	 * @param orgArtId2
	 * @param eventToSense
	 */
	private void execSetRespOrgArt(String opName, String orgArtName1, String orgArtName2, 
			ArtifactId orgArtId1, ArtifactId orgArtId2, String eventToSense){
		
		//ArtifactProperty orgArtProperty;
		SensorId orgSensorId = null;
		try {
			orgSensorId = linkDefaultSensor();
		} catch (CartagoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Perception p1= null;			
		Op op;		
		p1 = linkOrgArts(orgArtId1, orgArtId2); 
		myFrame.printEvent(p1);
		
		if(opName.equals(ADD_RESP_SCHEMEBOARD_OP)) {
			
			Object[] params = new Object[2];
			params[0] = orgArtName2;
			params[1] = orgArtId2;
			op = new Op(opName, params);
			
			try {
				use(orgArtId1, op, orgSensorId); 
				p1 = sense(orgSensorId, eventToSense);
				myFrame.printEvent(p1);
				
				//p1 = linkOrgArts(orgArtId1, orgArtId2); 
				//myFrame.printEvent(p1);
				
				/*orgArtProperty = observeProperty(orgArtId1, "groupSpec");
				//log(orgArtProperty.getContent(0).getClass().getSimpleName());
				Group group = (Group)orgArtProperty.getContent(0);
					
				use(orgArtId2, new Op(SET_RESP_GROUPBOARD_OP, orgArtName1, group, orgArtId1), orgSensorId);
				p2 = sense(orgSensorId, SET_RESP_GROUPBOARD_EVENT); 
				myFrame.printEvent(p2);*/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
		}else if(opName.equals(ADD_RESP_GROUPBOARD_OP)){
			Object[] params = new Object[2];
			params[0] = orgArtName2;
			params[1] = orgArtId2;
			try {
				//orgArtProperty = observeProperty(orgArtId2, "groupSS");
				//log(orgArtProperty.getContent(0).getClass().getSimpleName());
				//Group group = ((SS)orgArtProperty.getContent(0)).getRootGrSpec();
				//params[2] = group;
				op = new Op(opName, params);
				
				use(orgArtId1, new Op(opName, orgArtName2, orgArtId2), orgSensorId); 
				p1 = sense(orgSensorId, eventToSense);
				myFrame.printEvent(p1);
				
				//p1 = linkOrgArts(orgArtId1, orgArtId2); 
				//myFrame.printEvent(p1);
				
				/*use(orgArtId2, new Op(SET_RESP_SCHEMEBOARD_OP, orgArtName1, orgArtId1), orgSensorId);
				p2 = sense(orgSensorId, SET_RESP_SCHEMEBOARD_EVENT); 
				myFrame.printEvent(p2);*/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 	
		}		
	}
	
	
	
	/**
	 * method called to observe the @OBSPROPERTY of an OrgArt
	 *  
	 * @param arguments
	 * @return
	 */
	public ArtifactObsProperty observeOrgArtProperty(List<String> arguments) {	
		
		String orgArtName = arguments.get(0); 
		String propName = arguments.get(1);
		ArtifactId orgArtId = null;
		ArtifactObsProperty orgArtProperty = null;		
		
		if(!myOrgArtId.containsKey(orgArtName)) {
			orgArtId = this.lookUpOrgArt(orgArtName);
			
			if(!orgArtId.equals(null)) {			
				
				myOrgArtId.put(orgArtName, orgArtId);				
				try { 
					orgArtProperty = observeProperty(orgArtId, propName); 					
					myFrame.printObservableProperty(orgArtProperty, orgArtId);
				} catch (CartagoException e) {
					myFrame.showErrorDialog("An exception occured durring Observe Property execution");
					e.printStackTrace();
				}
			}
			else {
				myFrame.showErrorDialog("An exception occured durring lookup OrgArt execution");
			}				
		}
		else {
			orgArtId = myOrgArtId.get(orgArtName);		
			try {
				orgArtProperty = observeProperty(orgArtId, propName); 
				myFrame.printObservableProperty(orgArtProperty, orgArtId);
			} catch (CartagoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return orgArtProperty;
	}
	
	/**
	 * Method called to focus an OrgArt
	 * @param orgArtName
	 */
	public void focusOrgArt(String orgArtName) {
		myFrame.showErrorDialog("This operation is not yet implemented");
	}
	
	/**
	 * Execution of linking two OrgArts
	 * @param orgArtId1
	 * @param orgArtId2
	 * @return
	 */
	public Perception linkOrgArts(ArtifactId orgArtId1, ArtifactId orgArtId2) {
		SensorId orgSensorId = null;
		try {
			orgSensorId = linkDefaultSensor();
		} catch (CartagoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Perception orgPerception = null;
		
		try {
			use(orgArtId1, new Op("linkTo",orgArtId2), orgSensorId);
			orgPerception = sense(orgSensorId, "link_succeeded"); //|link_failed
		} catch (Exception e) {
			e.printStackTrace();
		} 
		myFrame.printEvent(orgPerception);
		return orgPerception;
	}
	
	/**
	 * Method called to link two OrgArts
	 * @param orgArtName1
	 * @param orgArtName2
	 */
	public void linkOrgArts(String orgArtName1, String orgArtName2) {
		
		//Perception p1, p2; //orgPerception;		
		ArtifactId orgArtId1 = null;
		ArtifactId orgArtId2 = null;
		
		if(!myOrgArtId.containsKey(orgArtName1)) {
			
			orgArtId1 = this.lookUpOrgArt(orgArtName1);
			
			if(!orgArtId1.equals(null)) {			
				myOrgArtId.put(orgArtName1, orgArtId1);
				//myFrame.printEvent(p1);
			
			}else {
				myFrame.showErrorDialog("An exception occured durring lookup OrgArt execution");
			}
		}
		else {
			orgArtId1 = myOrgArtId.get(orgArtName1);
		}
		
		if(!orgArtId1.equals(null))
			if(!myOrgArtId.containsKey(orgArtName2)) {
				orgArtId2 = this.lookUpOrgArt(orgArtName2);
			
				if(!orgArtId2.equals(null)) {
				
					myOrgArtId.put(orgArtName2, orgArtId2);
					//myFrame.printEvent(p2);	
				
					linkOrgArts(orgArtId1, orgArtId2);
					//myFrame.printEvent(orgPerception);
				}
				else {
					myFrame.showErrorDialog("An exception occured durring lookup OrgArt execution");				
				}
			}
			else {
				orgArtId2 = myOrgArtId.get(orgArtName2);
				linkOrgArts(orgArtId1, orgArtId2);
				//myFrame.printEvent(orgPerception);
			}
	}
	
	/**
	 * 
	 * @param orgArt
	 */
	public void addResponsibleOrgArt(OrgArt orgArt){
		responsibleOrgArts.add(orgArt);
	}
	List<OrgArt> responsibleOrgArts = new ArrayList<OrgArt>();
	

	String OSxmlURI;  
	/**
	 * Method called to set the URI of the XML specification of the organisation
	 * @param osFileName
	 */
	public void setOS_FileName(String osFileName){
		OSxmlURI = osFileName;
	}
	
	/**
	 * 
	 * @param orgBoardId
	 */
	public void modifyOS_ofOrgBoard(ArtifactId orgBoardId) {
		
		OS os = OS.loadOSFromURI(OSxmlURI);
		SensorId orgSensorId = null;
		try {
			orgSensorId = linkDefaultSensor();
		} catch (CartagoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Perception orgPerception;
		
		try {
			use(orgBoardId, new Op("modifyOS", os), orgSensorId);
			orgPerception = sense(orgSensorId,"modifyOS_Event");	
			myFrame.printEvent(orgPerception);	
			
			//log("Event " + eventsCounter1 + " : " + p1.getContent(0));
			//eventsCounter1++;
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, ArtifactId> getMyOrgArtsId(){
		return this.myOrgArtId;
	}
	
	int currentOrgBoardNumber = 0;
	int currentGroupBoardNumber = 0;
	int currentSchemeBoardNumber = 0;
	int currentNormativeBoardNumber = 0;
	
	/**
	 * Method called to create one instance of all types of OrgArts with a given OS 
	 * @param osXmlURI
	 */
	public void createAllOrgAtrsOfAnOS(String osXmlURI) {	
	
		//SensorId orgSensorId = linkDefaultSensor();
		//Perception orgPerception;
		
		String orgBoardNumber = String.valueOf(currentOrgBoardNumber);
		String groupBoardNumber = String.valueOf(currentGroupBoardNumber) ;
		String schemeBoardNumber = String.valueOf(currentSchemeBoardNumber);
		String normativeBoardNumber = String.valueOf(currentNormativeBoardNumber);
		
		try {			
			/**
			 * Load OS and create OrgBoard
			 */
			OS os = OS.loadOSFromURI(osXmlURI);
			createOrgBoard(osXmlURI, orgBoardNumber);
			currentOrgBoardNumber++;
			
			/**
			 * Create All GroupBoard(s)
			 **/		
			String orgBoardName = "OrgBoard_" + orgBoardNumber;
			ArtifactId orgBoardId = this.myOrgArtId.get(orgBoardName);
			
			
			String rootGroupName = os.getSS().getRootGrSpec().getFullId();
			Group rootGroupSpec = os.getSS().getRootGrSpec();
			CardinalitySet<Group> subGrp; 
			subGrp = rootGroupSpec.getSubGroups();
			
			String rootGroupBoardSuffix = "root"+groupBoardNumber;
			String rootGroupBoardName = "GroupBoard_" + rootGroupSpec + "_" +rootGroupBoardSuffix; //+rootGroupSpec.getFullId();
			
			createGroupBoard(rootGroupName, rootGroupBoardSuffix, orgBoardName, null);
			currentGroupBoardNumber++;
			ArtifactId rootGroupBoardId = myOrgArtId.get(rootGroupBoardName);
			
			Set<ArtifactId> groupBoardsId = new HashSet<ArtifactId>();
			groupBoardsId.add(rootGroupBoardId);
			
			Set<String> groupBoardsName = new HashSet<String>();
			groupBoardsName.add(rootGroupBoardName);
			
			//Creation of the sub-group if they exist with ArtifactId as parameter
			/*if (subGrp.size()>0){
				int subGrpNum = 0;
				String subGroupBoardSuffix; 
				Iterator<Group> it = subGrp.iterator();
				String subGroupName, grpName;
				ArtifactId grpId;
				
				while(it.hasNext()){
					subGroupBoardSuffix = "subGrp" + String.valueOf(subGrpNum) 
					+ "_" + rootGroupBoardSuffix;
					subGroupName = it.next().getFullId();
					grpName = "GroupBoard_" + subGroupName + "_" + subGroupBoardSuffix;
					createGroupBoard(subGroupName, subGroupBoardSuffix, orgBoardId, 
							rootGroupBoardId); 
					grpId = myOrgArtId.get(grpName);
					groupBoardsId.add(grpId);
					subGrpNum++;					
				}
			}*/
			
			//Creation of the sub-group if they exist with Artifact name as parameter
			if (subGrp.size()>0){
				int subGrpNum = 0;
				String subGroupBoardSuffix; 
				Iterator<Group> it = subGrp.iterator();
				String subGroupName, grpName;
				ArtifactId grpId;
				
				while(it.hasNext()){
					subGroupBoardSuffix = "subGrp" + String.valueOf(subGrpNum) 
					+ "_" + rootGroupBoardSuffix;
					subGroupName = it.next().getFullId();
					grpName = "GroupBoard_" + subGroupName + "_" + subGroupBoardSuffix;
					createGroupBoard(subGroupName, subGroupBoardSuffix, orgBoardName, 
							rootGroupBoardName); 
					grpId = myOrgArtId.get(grpName);
					groupBoardsId.add(grpId);
					groupBoardsName.add(grpName);
					subGrpNum++;					
				}
			}
			
			/**
			 * Create All SchemeBoard(s)
			 **/					 
			Collection<Scheme> schs = os.getFS().getSchemes();	
			String schemeSpecName = null;
			if (schs.size()>0){	
				for (Scheme sch:schs) { 
					schemeSpecName = sch.getFullId();
					createSchemeBoard(schemeSpecName, schemeBoardNumber, orgBoardName, rootGroupBoardName);
					currentSchemeBoardNumber++;
					schemeBoardNumber = String.valueOf(currentSchemeBoardNumber);
				}
			}
			
			String rootSchemeBoardName = "SchemeBoard_" + schemeSpecName + "_" + String.valueOf(currentSchemeBoardNumber -1);
			ArtifactId rootSchemeBoardId = myOrgArtId.get(rootSchemeBoardName);			
			createNormativeBoard(normativeBoardNumber, orgBoardName, rootSchemeBoardName, groupBoardsName);
			currentNormativeBoardNumber++;
				
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
