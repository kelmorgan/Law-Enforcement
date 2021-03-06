package com.newgen.LE;

import org.json.simple.JSONArray;

import com.newgen.iforms.EControl;
import com.newgen.iforms.FormDef;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.custom.IFormServerEventHandler;
import com.newgen.iforms.user.Constants;
import com.newgen.mvcbeans.model.WorkdeskModel;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class Branch_Initiator extends Commons implements IFormServerEventHandler, Constants {

	public static Logger logger = LogGEN.getLoggerInstance(Branch_Initiator.class);
	String branchName;
	String branchId;

	@Override
	public void beforeFormLoad(FormDef arg0, IFormReference ifr) {
		
		
		
		String query = "select sole_id , branch_name  from  usr_0_fbn_usr_branch_mapping where upper(user_id) = upper('"+ ifr.getUserName() +"')";
		
		logger.info(query);

	List<List<String>> getGeneralDetails = ifr.getDataFromDB(query);

		ifr.setValue("StaffID", ifr.getUserName());
		
		branchName = getGeneralDetails.get(0).get(1);
		logger.info("branch name: " + branchName);
		
		ifr.setValue("Account_Branch_Name", branchName);
		
		branchId = getGeneralDetails.get(0).get(0);
		logger.info("this is branch id: "+branchId );
		
		ifr.setValue("Initiator_SOL_ID", branchId);
	
		
		ifr.setValue("INIT_BRANCH",branchName);
		
		ifr.setStyle("frame5","visible","false");
		ifr.setStyle("Is_Request_Domiciled","disable","true");
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
		// executeServerEvent("Is_domiciled","Done",ifr.getValue("Is_domiciled"),true);
		logger.info("LE : Branch_Initiator : executeServerEvent :  controlName : " + controlName);
		logger.info("LE : Branch_Initiator : executeServerEvent :  value : " + value);
		logger.info("--executeServerEvent--");
		logger.info("sEventName-->" + eventName);
		
		WDGeneralData wdgeneralObj = ifr.getObjGeneralData();
		
		 String winame=  (String) ifr.getControlValue("WorkItemName");
		 String initID = (String) ifr.getValue("Initiator_SOL_ID");
		 String accountNumber = (String) ifr.getValue("table4_AccountNumber");
		 String currentDate = (String)ifr.getControlValue("CurrentDateTime");
		 String initiationDate = "";
		 String leaRef = (String) ifr.getValue("LEA_Reference_Number");
		 String decision = (String)ifr.getValue("Decision");
	
		try {
			switch (eventName) {
			case CLICK:{
			}
				break;
			case FORMLOAD: {
				logger.info("we are in formload");
				ifr.addItemInCombo("Decision","Submit","Submit");
				ifr.addItemInCombo("Decision","Discard","Discard");	
				ifr.setValue("Prev_WS", "NA");
				ifr.setValue("Curr_WS", ifr.getActivityName());
				//ifr.setStyle("table1", "disable", "true");
			}
				break;

			case ONCHANGE: {	
				}																	
				break;
			case ONDONE: {
	
				logger.info("LE : Branch_Initiator : executeServerEvent : ONDONE: wi_name " + winame);
				String sDocPresentQuery = "SELECT COUNT(1) FROM pdbdocument A, PDBDocumentContent B WHERE A.DocumentIndex=B.DocumentIndex AND B.ParentFolderIndex=(select FolderIndex from PDBFolder where name ='"+ winame + "') and A.Name like 'LEA Letter%'"; //Owner = 83
				List docList = new ArrayList();
				docList = ifr.getDataFromDB(sDocPresentQuery);
				logger.info("sDocPresentQuery" + sDocPresentQuery);
				Iterator itdoc = docList.iterator();
				String sDocCount = "";
				while (itdoc.hasNext()) {
					sDocCount = itdoc.next().toString();
					sDocCount = sDocCount.substring(sDocCount.indexOf("[") + 1, sDocCount.indexOf("]"));
					logger.info("LE : Branch_Initiator : executeServerEvent :  sDocCount : " + sDocCount);
					if (sDocCount.equalsIgnoreCase("0")) {
						String otp = "";
						logger.info("sDocPresentQuery " + sDocCount);
						otp = "false";
						return otp;
					}
					
				}
			}
		
				break;
			case ONLOAD: {
				switch (controlName) {
				case "":{
					
				}
			}
			break;
			}
			case CUSTOM: {
				switch (controlName) {
				case "checkAccountTable":{
					boolean resp = makeTableCompulsory(ifr,value);
					if (resp == false)
						return "Kindly input Account Number Details";
					}
					
					case "setInvisible":{
						isDomiciledVisibleTrueOrFalse(ifr, value, "Is_Request_Domiciled");
						break;
					}
					case "setIsDomiciled":{
						logger.info("setIsDomiciled: ");
						logger.info("initID: "+initID);
						setIsDomiciledResult(ifr, "table4", 0, 3, initID, "Is_Request_Domiciled");
						break;
					}
						
					case "setSolId":{
						ifr.setValue("CR_COUNT", "YES");
						setSolId(ifr,value,"table4");
						break;	
					}
						
					case "checkLeRef":{
					String response = checkLeRef(ifr,winame,leaRef);
					return response;
					}
				
				}
			}
			break;
			case SENDMAIL:{
			
				logger.info("decision: "+decision);
				String staff ="";
				
				if (decision.equalsIgnoreCase("Submit")){
					staff = getUsersToNextWorkstep(ifr,"LE_IA");
				
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
