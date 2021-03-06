package com.newgen.LE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import com.newgen.iforms.EControl;
import com.newgen.iforms.FormDef;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.custom.IFormServerEventHandler;
import com.newgen.iforms.user.Constants;
import com.newgen.mvcbeans.model.WorkdeskModel;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;

public class Originating_Branch extends Commons  implements IFormServerEventHandler, Constants {
	public static Logger logger = LogGEN.getLoggerInstance(Originating_Branch.class);
	String branchName;
	String branchId;

	@Override
	public void beforeFormLoad(FormDef arg0, IFormReference ifr) {
		// TODO Auto-generated method stub	
		ifr.setValue("SEND_TO", "");
		ifr.setValue("SEND_CC", "");
		ifr.setValue("SEND_BCC", "");
		String check = (String)ifr.getValue("CR_COUNT");
	
		//String query = "SELECT UBM.USERID USERID, UBM.BRANCHID BRANCHID, UBM.BRANCHNAME BRANCHNAME FROM LE_USR_BRANCH_MAPPING_MTR UBM, LE_BRANCH_MTR BMTR WHERE BMTR.BRANCHID=UBM.BRANCHID AND UPPER(USERID)=UPPER('" + ifr.getUserName() + "')";

		String query = "select sole_id , branch_name  from  usr_0_fbn_usr_branch_mapping where upper(user_id) = upper('"+ ifr.getUserName() +"')";
		
		List<List<String>> getGeneralDetails = ifr.getDataFromDB(query);
		logger.info("this query from db: "+ getGeneralDetails);
		ifr.setValue("StaffID", ifr.getUserName());
		branchName = getGeneralDetails.get(0).get(1);
		ifr.setValue("Account_Branch_Name", branchName);
		ifr.setValue("INIT_BRANCH", branchName);
		branchId = getGeneralDetails.get(0).get(0);
		//ifr.setStyle("INIT_BRANCH", "visible", "true");
		//ifr.setStyle("Account_Branch_Name", "visible", "false");
		//ifr.setValue("Initiator_SOL_ID", branchId);
		
		if(check.equalsIgnoreCase("YES")){
			ifr.setStyle("frame4","disable","true");
			ifr.setStyle("frame4","visible","true");
			ifr.setStyle("frame5","visible","false");
			ifr.setStyle("typ_of_Req","visible","true");
			ifr.setStyle("Is_Request_Domiciled","visible","false");
		}
		else {
		ifr.setStyle("headerframe1","disable","true");
		ifr.setStyle("frame4","visible","false");
		ifr.setStyle("frame5","disable","true");
	    ifr.setStyle("typ_of_Req","visible","true");
		}   
	}

	@Override
	public String executeCustomService(FormDef arg0, IFormReference arg1, String arg2, String arg3, String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONArray executeEvent(FormDef arg0, IFormReference arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String executeServerEvent(IFormReference ifr, String controlName, String eventName, String value) {
		// TODO Auto-generated method stub
		WDGeneralData wdgeneralObj= ifr.getObjGeneralData(); 
		 String winame=  (String) ifr.getControlValue("WorkItemName");
		logger.info("LE : Attach_acknowledgement_copy : executeServerEvent :  controlName : "+controlName );
		logger.info("LE : Attach_acknowledgement_copy : executeServerEvent :  value : "+value );
		logger.info("--executeServerEvent--");
		logger.info("sEventName-->" + eventName);

		String prevWs= (String)ifr.getValue("Prev_WS");
		try {
			switch (eventName) {

			case CLICK:
				break;
			case FORMLOAD: {
				ifr.setValue("Decision","");
				ifr.setValue("LE_REMARKS","");
				ifr.clearCombo("Decision");
				ifr.addItemInCombo("Decision","Approve","Approve","tooltip");
				ifr.addItemInCombo("Decision","Return","Return","tooltip");
				ifr.setStyle("table1", "disable", "true");	
			}
				break;

			case ONCHANGE: {
			}
				break;
			case ONDONE: {
				String decision = (String)ifr.getValue("Decision");
				if (decision.equalsIgnoreCase("Approve")){
				String sDocPresentQuery = "SELECT COUNT(1) FROM pdbdocument A, PDBDocumentContent B WHERE A.DocumentIndex=B.DocumentIndex AND B.ParentFolderIndex=(select FolderIndex from PDBFolder where name ='"+ winame + "') and A.Name like 'Acknowledgement Copy%'"; //Owner = 83
				List docList = new ArrayList();
				docList = ifr.getDataFromDB(sDocPresentQuery);
				logger.info("sDocPresentQuery" + sDocPresentQuery);
				Iterator itdoc = docList.iterator();
				String sDocCount = "";
				while (itdoc.hasNext()) {
					sDocCount = itdoc.next().toString();
					sDocCount = sDocCount.substring(sDocCount.indexOf("[") + 1, sDocCount.indexOf("]"));
					logger.info("LE : Attach_acknowledgement_copy : executeServerEvent :  sDocCount : " + sDocCount);
					if (sDocCount.equalsIgnoreCase("0")) {
						//boolean noDocs = Boolean.parseBoolean(sDocCount);
						String otp = "";
						logger.info("sDocPresentQuery " + sDocCount);
						otp = "false";
						logger.info("docCount--- " + otp);
						//SHOW MSG
						return otp;
						}
				}
				}
					//executeServerEvent("gridCount","Done",ifr.getGridRowCount("table4"),true);
					if(controlName.equalsIgnoreCase("gridCount")&& eventName.equalsIgnoreCase("Done")) {
				        ifr.setValue("gridCount",value);
				        logger.info("LE : Attach_acknowledgement_copy Value setted" );
					}
			}
			
				break;
			case ONLOAD: {
				switch (controlName) {
				
				}
				
			}
			break;
			case SENDMAIL:{
				String decision = (String)ifr.getValue("Decision");
				logger.info("decision: "+decision);
				
				String sol = (String)ifr.getValue("ATTAC_INIT_SOL");
				logger.info("sol: "+sol);
				
				String initSol = (String)ifr.getValue("Initiator_SOL_ID");
				logger.info("initSol: "+initSol);
				
				String staff ="";
				String staff2 = "";
				String group ="";
				
				if (decision.equalsIgnoreCase("Approve")){
					staff = getUsersToNextWorkstep(ifr,"LE_IA");
				
					logger.info("staff: "+staff);
				
				if (!staff.equalsIgnoreCase(EMPTY))
					setUsersToMail(ifr,staff,"","","SEND_TO","","");
				}
				
				else if( decision.equalsIgnoreCase("Return")) {
					group = "HNFT_"+sol;
					logger.info("group: "+group);
					
					staff = getUsersToNextWorkstep(ifr,group);
					logger.info("staff: "+staff);
					
					staff2 = getUsersToNextWorkstep(ifr,"LE_IA");
					logger.info("staff2: "+staff2);
				
				if (!staff.equalsIgnoreCase(EMPTY) || staff2.equalsIgnoreCase(EMPTY))
					setUsersToMail(ifr,staff,staff2,"","SEND_TO","SEND_CC","");	
				
				}	
				
			}
			break;
			}
		} catch (Exception ex) {
			logger.info("Exception-->" + ex.getStackTrace().toString());
		}
	
		
/*
 * String sDocPresentQuery =
 * "SELECT COUNT(1) FROM pdbdocument A, PDBDocumentContent B WHERE A.DocumentIndex=B.DocumentIndex AND B.ParentFolderIndex=(select FolderIndex from PDBFolder where Name ='"
 * +winame+"') and A.Name like 'LEA Letter%'";//Owner = 83 List docList = new
 * ArrayList();{ docList = ifr.getDataFromDB(sDocPresentQuery); Iterator itdoc =
 * docList.iterator(); String sDocCount = ""; while (itdoc.hasNext()) {
 * sDocCount = itdoc.next().toString(); sDocCount =
 * sDocCount.substring(sDocCount.indexOf("[") + 1, sDocCount.indexOf("]"));
 * logger.info("LE : Branch_Initiator : executeServerEvent :  sDocCount : "
 * +sDocCount ); if (sDocCount.equalsIgnoreCase("0")) { return
 * "Please insert Document."; }
 */

		
		return null;
	}


	@Override
	public String generateHTML(EControl arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCustomFilterXML(FormDef arg0, IFormReference arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String introduceWorkItemInWorkFlow(IFormReference arg0, HttpServletRequest arg1, HttpServletResponse arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String introduceWorkItemInWorkFlow(IFormReference arg0, HttpServletRequest arg1, HttpServletResponse arg2,
			WorkdeskModel arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setMaskedValue(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return arg1;
	}

	@Override
	public JSONArray validateSubmittedForm(FormDef arg0, IFormReference arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
