package com.newgen.LE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import com.newgen.iforms.user.Constants;
import com.newgen.iforms.EControl;
import com.newgen.iforms.FormDef;
	import com.newgen.iforms.custom.IFormReference;
	import com.newgen.iforms.custom.IFormServerEventHandler;
import com.newgen.mvcbeans.model.WorkdeskModel;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;

	public class Review_and_advise_action extends Commons implements IFormServerEventHandler, Constants {
		public static Logger logger = LogGEN.getLoggerInstance(Review_and_advise_action.class);
	
		@Override
		public void beforeFormLoad(FormDef arg0, IFormReference ifr) {
			// TODO Auto-generated method stub
			ifr.setValue("SEND_TO", "");
			ifr.setValue("SEND_CC", "");
			ifr.setValue("SEND_BCC", "");
			
			
			String check = (String)ifr.getValue("CR_COUNT");
			logger.info("this is check: "+ check);
			String prevWs= (String)ifr.getValue("Prev_WS");
			logger.info("this is prevWs: "+ prevWs);
			
			if(check.equalsIgnoreCase("YES") && prevWs.equalsIgnoreCase(Attach_Documents)){
				ifr.setStyle("frame4","disable","true");
				ifr.setStyle("frame4","visible","true");
				ifr.setStyle("frame5","visible","false");
				ifr.setStyle("typ_of_Req","visible","true");
				ifr.setStyle("Is_Request_Domiciled","visible","false");
				ifr.setStyle("Requesting_Agency","disable","false");
				ifr.setStyle("Location_LEA","disable","false");
			}
			else {
			ifr.setStyle("frame4","visible","true");
			ifr.setStyle("frame5","disable","true");
			ifr.setStyle("headerframe1","disable","true");
			ifr.setStyle("frame5","visible","false");
			ifr.setStyle("typ_of_Req","visible","true");
			ifr.setStyle("typ_of_Req","mandatory","true");
			ifr.setStyle("Initiator_SOL_ID","visible","false");
			ifr.setStyle("Account_Branch_Name","visible","false");
			ifr.setStyle("Is_Request_Domiciled","visible","false");
			ifr.setStyle("Requesting_Agency","disable","false");
			ifr.setStyle("Location_LEA","disable","false");
			ifr.setStyle("table4_CustomerName","disable","true");
			ifr.setStyle("table4_AccountNumber","disable","true");
			ifr.setStyle("table4_CustomerBranch","disable","true");
			ifr.setStyle("table4_SOL_ID","disable","true");
			ifr.setStyle("table4_AccountStatus","disable","true");
			ifr.setStyle("button9","disable","true");
			ifr.setStyle("copyrow_table4","disable","true");   
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
			logger.info("LE : Review_and_advise_action : executeServerEvent :  controlName : " + controlName);
			logger.info("LE : Review_and_advise_action : executeServerEvent :  value : " + value);
			logger.info("--executeServerEvent--");
			logger.info("sEventName-->" + eventName);
			logger.info("\nTestinng ONDONE---");
			logger.info("Passed Event is " + eventName); 
			 String leaRef = (String) ifr.getValue("LEA_Reference_Number");
			
			
			
			WDGeneralData wdgeneralObj = ifr.getObjGeneralData();
			
			 String winame=  (String) ifr.getControlValue("WorkItemName");
			 String prev_WS=(String) ifr.getValue("Prev_WS");
			 
			 logger.info("LE : Review_and_advise_action : executeServerEvent :  winame : " + winame);
			 
			
			try {
				switch (eventName) {

				case CLICK:{
					//	
				}
					break;
				case FORMLOAD: {
					ifr.setValue("StaffID", ifr.getUserName());
					String check = (String)ifr.getValue("CR_COUNT");
					logger.info("we are in formload scenario "+ winame);	  
					  ifr.setStyle("delete_table4","visible","false");
					  
					  if (prev_WS.equalsIgnoreCase(Attach_Documents) && check.equalsIgnoreCase("")) {
						  ifr.setStyle("frame5","visible","true");
						  ifr.setStyle("frame5","disable","true"); 
						  ifr.setStyle("frame4","visible","false"); 
						  ifr.setStyle("Requesting_Agency","disable","false");
						  ifr.setStyle("Location_LEA","disable","false");
					  }
					  
					  ifr.setValue("Decision","");
					  ifr.setValue("LE_REMARKS","");
					  ifr.clearCombo("Decision");
					  ifr.addItemInCombo("Decision","Approve","Approve","tooltip");
					  ifr.addItemInCombo("Decision","Reject","Reject","tooltip");
					  ifr.setStyle("table1", "disable", "true");
				}
					break;

				case ONCHANGE: {
					//
				}
					break;
					
				case ONDONE:{
					logger.info("we are in ondone scenario "+ winame);
					
					logger.info("LE : Review_and_advise_action : executeServerEvent :  winame : "+winame );
					
					String prevWS = (String)ifr.getValue("Prev_WS");
					
					if (prevWS.equalsIgnoreCase("Branch_Initiator")) {
						if( value.equalsIgnoreCase("Insert")) {
					String StaffID = (String)ifr.getValue("StaffID");
					String initiatorSolId = (String)ifr.getValue("Initiator_SOL_ID");
					String Reference_Number1 = (String)ifr.getValue("LEA_Reference_Number");
					String Agency = (String)ifr.getValue("Requesting_Agency");
					String Location = (String)ifr.getValue("Location_LEA");
					String TYP_OF_REQUEST = (String)ifr.getValue("typ_of_Req");
					
					String  Resplistval= "SELECT WINAME,ACCOUNTNUMBER,CUSTOMERNAME,CUSTOMERBRANCH,SOL_ID,ACCOUNTSTATUS,'"+StaffID+"',typ_of_req,'"+Reference_Number1+"','"+Agency+"','"+Location+"' FROM cmplx_lea " + 
							" WHERE winame ='"+winame+"'";
					List<List<String>> listval = ifr.getDataFromDB(Resplistval);
					
					logger.info("LE : Review_and_advise_action : executeServerEvent : ONDONE: wi_name "+winame +" StaffID : "+StaffID+" TYP_OF_REQUEST : "+TYP_OF_REQUEST); 
					String qucery ="INSERT INTO le_import "
							+ "(WINAME,ACCOUNTNUMBER1,ACCOUNTNAME,BRANCH,ACCOUNTSOL_ID,ACCOUNTSTATUS,TYP_OF_REQUEST,StaffID,Reference_Number1,Agency,Location, Initiator_SOL_ID, SEND_TO) " + 
							" SELECT WINAME,ACCOUNTNUMBER,CUSTOMERNAME,CUSTOMERBRANCH,SOL_ID,ACCOUNTSTATUS,typ_of_req,'"+StaffID+"','"+Reference_Number1+"','"+Agency+"','"+Location+"', '"+initiatorSolId+"', mailGroup"
							+ " FROM cmplx_lea WHERE winame ='"+winame+"'";
					logger.info("qucery: "+qucery);
					 int resp = ifr.saveDataInDB(qucery); 
					 if (resp >= 0)
						 logger.info("Inserted data to Import: "+qucery);
					
				}
				}
				}
					
					break;
				case ONLOAD: {
					switch (controlName) {
					
					}
					break;
				}
				case CUSTOM: {
					switch (controlName){
					case "checkRequest":{
					boolean checkRequest = getValueOfRequest(ifr, value, "table4", 5);
					if (checkRequest == false)
						return "Kindly Select Request Type";
					
					break;
					}
					case "checkLeRef":{
						String response = checkLeRef(ifr,winame,leaRef);
						return response;
						}
					case "getRequestType":{
						getRequestType(ifr,value,"table4");
						
						break;
					}
					}
				}
				case SENDMAIL:{
					String decision = (String)ifr.getValue("Decision");
					logger.info("decision: "+decision);
					
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
					
					if (prevWS.equalsIgnoreCase(Attach_Documents) && decision.equalsIgnoreCase("Approve")){
						
						group = "HNFT_"+sol;
						
						logger.info("group: "+group);
						
						staff = getUsersToNextWorkstep(ifr,group);
					
						logger.info("staff: "+staff);
					
					if (!staff.equals(EMPTY))
						setUsersToMail(ifr,staff,"","","SEND_TO","","");
					
					}
					else if(crCount.equalsIgnoreCase("YES") && decision.equalsIgnoreCase("Approve")) {
						group = "HNFT_"+sol;
						logger.info("group: "+group);
						
						staff = getUsersToNextWorkstep(ifr,group);
						
						logger.info("staff: "+staff);
					
					if (!staff.equals(EMPTY))
						setUsersToMail(ifr,staff,"","","SEND_TO","","");	
					
					}
					else if (decision.equalsIgnoreCase("Reject")){
						group = "HNFT_"+initSol;
						
						logger.info("group: "+group);
						
						staff = getUsersToNextWorkstep(ifr,group);
						logger.info("staff: "+staff);
					
					if (!staff.equals(EMPTY))
						setUsersToMail(ifr,staff,"","","SEND_TO","","");	
					}
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
		public String introduceWorkItemInWorkFlow(IFormReference arg0, HttpServletRequest arg1,
				HttpServletResponse arg2, WorkdeskModel arg3) {
			// TODO Auto-generated method stub
			return null;
		}

	}


