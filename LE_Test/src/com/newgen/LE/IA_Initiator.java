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
import com.newgen.mvcbeans.model.WorkdeskModel;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;

public class IA_Initiator extends Commons implements IFormServerEventHandler {
	
	public static Logger logger = LogGEN.getLoggerInstance(IA_Initiator.class);
	

	@Override
	public void beforeFormLoad(FormDef arg0, IFormReference ifr) {
		// TODO Auto-generated method stub

		WDGeneralData wdgeneralObj= ifr.getObjGeneralData(); 
	    String activityName=ifr.getActivityName();
	    String winame=  (String) ifr.getControlValue("WIName");
	    
	    
	    ifr.setValue("StaffID",ifr.getUserName());
	    ifr.setStyle("frame5","visible","false");
	    ifr.setStyle("Initiator_SOL_ID","visible","false");
		ifr.setStyle("Account_Branch_Name","visible","false");
	    ifr.setStyle("typ_of_Req","visible","true");
	    ifr.setStyle("typ_of_Req","mandatory","true");
	    ifr.setStyle("Is_Request_Domiciled","visible","false");
	    ifr.setValue("Decision","");
	    logger.info("Adding Option to Decision dropdown");
	    ifr.addItemInCombo("Decision", "Submit", "Submit", "tooltip");
	    ifr.addItemInCombo("Decision", "Discard", "Discard", "tooltip");
	    logger.info(" Decision options Added successfully ");
	    
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
		logger.info("LE : IA_Initiator : executeServerEvent :  controlName : " + controlName);
		logger.info("LE : IA_Initiator : executeServerEvent :  value : " + value);
		logger.info("--executeServerEvent--");
		logger.info("sEventName-->" + eventName);
		logger.info("\nTestinng ONDONE---");
		logger.info("Passed Event is " + eventName); 
		WDGeneralData wdgeneralObj = ifr.getObjGeneralData();
		
		 String winame=  (String) ifr.getControlValue("WorkItemName");
		 logger.info("winame: "+winame );
		 String accountNumber = (String) ifr.getValue("table4_AccountNumber");
		 String leaRef = (String) ifr.getValue("LEA_Reference_Number");
		 String decision = (String)ifr.getValue("Decision");
			logger.info("decision: "+decision);
		try {
			switch (eventName) {

			case CLICK:
				break;
			case FORMLOAD: {
				ifr.setValue("Prev_WS", "NA");
				ifr.setValue("Curr_WS", ifr.getActivityName());
				ifr.setValue("StaffID",ifr.getUserName());
				ifr.setStyle("frame5","visible","false");
				ifr.setStyle("Initiator_SOL_ID","visible","false");
				ifr.setStyle("Account_Branch_Name","visible","false");
				ifr.setStyle("typ_of_Req","visible","true");
				ifr.setStyle("Is_Request_Domiciled","visible","false");
				ifr.setValue("Decision","");
				ifr.clearCombo("Decision");
				ifr.addItemInCombo("Decision", "Submit", "Submit", "tooltip");
				ifr.addItemInCombo("Decision", "Discard", "Discard", "tooltip");
			}
				break;

			case ONCHANGE: {
				//
				}
				break;
			case ONDONE:{
				logger.info("LE : IA_Initiator : executeServerEvent :  winame : "+winame );
				String StaffID = (String)ifr.getValue("StaffID");
				String Reference_Number1 = (String)ifr.getValue("LEA_Reference_Number");
				String Agency = (String)ifr.getValue("Requesting_Agency");
				String Location = (String)ifr.getValue("Location_LEA");
				String TYP_OF_REQUEST = (String)ifr.getValue("typ_of_Req");
				String otp = "";
				
				if(value.equals("Insert")) {
				
				logger.info("LE : IA_Initiator : executeServerEvent : ONDONE: wi_name "+winame +" StaffID : "+StaffID+" TYP_OF_REQUEST : "+TYP_OF_REQUEST); 
				String  Resplistval= " SELECT WINAME,ACCOUNTNUMBER,CUSTOMERNAME,CUSTOMERBRANCH,SOL_ID,ACCOUNTSTATUS,'"+StaffID+"',typ_of_req,'"+Reference_Number1+"','"+Agency+"','"+Location+"' FROM cmplx_lea " + 
						" WHERE winame ='"+winame+"'";
				List<List<String>> listval = ifr.getDataFromDB(Resplistval);
				logger.info("LE : IA_Initiator : executeServerEvent : ONDONE: MANOJ  1" +Resplistval); 
				
				logger.info("LE : IA_Initiator : executeServerEvent : ONDONE: MANOJ 2 " +listval); 
				
				String qucery ="INSERT INTO ia_import (WINAME,ACCOUNTNUMBER1,ACCOUNTNAME,BRANCH,ACCOUNTSOL_ID,ACCOUNTSTATUS,StaffID,TYP_OF_REQUEST,Reference_Number1,Agency,Location, SEND_TO ) " + 
						" SELECT WINAME,ACCOUNTNUMBER,CUSTOMERNAME,CUSTOMERBRANCH,SOL_ID,ACCOUNTSTATUS,'"+StaffID+"',typ_of_req,'"+Reference_Number1+"','"+Agency+"','"+Location+"', mailGroup FROM cmplx_lea " + 
						" WHERE winame ='"+winame+"'";
				logger.info("LE : IA_Initiator : executeServerEvent : ONDONE: QUERY "+qucery);
				int resp =  ifr.saveDataInDB(qucery); 

				if (resp >= 0){
					logger.info("Inserted data to Import: "+qucery);
				}
				logger.info("LE : IA_Initiator : executeServerEvent : ONDONE: Inserted data to Import");
			
				}
				logger.info("LE : IA_Initiator : executeServerEvent :  TYP_OF_REQUEST : " + TYP_OF_REQUEST);
				logger.info("LE : IA_Initiator : executeServerEvent : ONDONE: wi_name " + winame);
				String sDocPresentQuery = "SELECT COUNT(1) FROM pdbdocument A, PDBDocumentContent B WHERE A.DocumentIndex=B.DocumentIndex AND B.ParentFolderIndex=(select FolderIndex from PDBFolder where name ='"+ winame + "') and A.Name like 'LEA Letter%'"; //Owner = 83
				List docList = new ArrayList();
				docList = ifr.getDataFromDB(sDocPresentQuery);
				logger.info("sDocPresentQuery" + sDocPresentQuery);
				Iterator itdoc = docList.iterator();
				String sDocCount = "";
				while (itdoc.hasNext()) {
					sDocCount = itdoc.next().toString();
					sDocCount = sDocCount.substring(sDocCount.indexOf("[") + 1, sDocCount.indexOf("]"));
					logger.info("LE : IA_Initiator : executeServerEvent :  sDocCount : " + sDocCount);
					if (sDocCount.equalsIgnoreCase("0")) {
						otp = "false";
						return otp;
					}
				}
				
			}
				break;
			case ONLOAD: {
				switch (controlName) {
				
				}
				break;
			}
			case CUSTOM:{
				switch (controlName){
					case "checkAccountTable":{
					boolean resp = makeTableCompulsory(ifr,value);
					if (resp == false)
						return "Kindly input Account Number Details";
					}
					
					case "setSolId":{
						ifr.setValue("CR_COUNT", "YES");
						setSolId(ifr,value,"table4");
						break;	
					}
					case "checkRequest":{
						boolean checkRequest = getValueOfRequest(ifr, value, "table4", 5);
						if (checkRequest == false)
							return "Kindly Select Request Type";
					}
					case "checkLeRef":{
						String response = checkLeRef(ifr,winame,leaRef);
						return response;
						}
				
				}
			}
			break;
			case SENDMAIL:{
				
				
				String prevWS = (String)ifr.getValue("Prev_WS");
				logger.info("prevWS: "+prevWS);
				
				String sol = (String)ifr.getValue("AccountSOL_ID");
				logger.info("sol: "+sol);
				
				String crCount = (String)ifr.getValue("CR_COUNT");
				logger.info("crCount: "+crCount);
				
				String initSol = (String)ifr.getValue("Initiator_SOL_ID");
				logger.info("initSol: "+initSol);
				
				String staff ="";
				String group = "";
				
				 if(crCount.equalsIgnoreCase("YES") && decision.equalsIgnoreCase("Submit")) {
					group = "HNFT_"+sol;
					logger.info("group: "+group);
					
					staff = getUsersToNextWorkstep(ifr,group);
					
					logger.info("staff: "+staff);
				
				if (!staff.equals(EMPTY))
					setUsersToMail(ifr,staff,"","","SEND_TO","","");	
				}	
			}
			break;
			case WEBSERVICE:{
				apiCall(ifr, accountNumber);
			}
			break;
			case GETUSERS:{
				String sol = (String) ifr.getValue("table4_SOL_ID");
				logger.info("sol: "+sol);
				String group = "HNFT_"+sol;
				logger.info("group: "+group);
				
				String staff = getUsersToNextWorkstep(ifr,group);
				
				if (!staff.equalsIgnoreCase(EMPTY))
				ifr.setValue("table4_mailGroup", staff);
				
			}
			break;
			case CHECKDECISION:{
				checkDecision(ifr,decision,"LEA_Reference_Number","Requesting_Agency","Location_LEA","table4","LE_REMARKS");
				break;
			}

			}
		} catch (Exception ex) {
			logger.info("Exception-->" + ex.getStackTrace().toString());
		}
		return null;
	}

	@Override
	public String getCustomFilterXML(FormDef arg0, IFormReference arg1, String arg2) {
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

	@Override
	public String generateHTML(EControl arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String introduceWorkItemInWorkFlow(IFormReference arg0, HttpServletRequest arg1, HttpServletResponse arg2,
			WorkdeskModel arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
