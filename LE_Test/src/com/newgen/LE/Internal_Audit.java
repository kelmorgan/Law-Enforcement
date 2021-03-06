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

public class Internal_Audit extends Commons implements IFormServerEventHandler {
	public static Logger logger = LogGEN.getLoggerInstance(Internal_Audit.class);

	@Override
	public void beforeFormLoad(FormDef arg0, IFormReference ifr) {
		// TODO Auto-generated method stub
		ifr.setValue("SEND_TO", "");
		ifr.setValue("SEND_CC", "");
		ifr.setValue("SEND_BCC", "");
		
		String check = (String)ifr.getValue("CR_COUNT");
		ifr.setStyle("Initiator_SOL_ID","visible","false");
		ifr.setStyle("Account_Branch_Name","visible","false");
		
	    if(check.equalsIgnoreCase("YES")){
	    	ifr.setStyle("frame4","disable","true");
			ifr.setStyle("frame4","visible","true");
			ifr.setStyle("frame5","visible","false");
			ifr.setStyle("typ_of_Req","visible","true");
		
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
		 String winame=  (String) ifr.getControlValue("WorkItemName");
			String prevWs= (String)ifr.getValue("Prev_WS");
		try {
			switch (eventName) {

			case CLICK:
				break;
			case FORMLOAD: {
				ifr.setValue("StaffID", ifr.getUserName());
				ifr.setStyle("Is_Request_Domiciled","visible","false");
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
					boolean checkDocs= checkDocuments(ifr,winame,"Acknowledgement Copy");
					logger.info("checkdocs: "+checkDocs);
					if(checkDocs == false)
						return "Please Attach Documents";
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
				
				if (decision.equalsIgnoreCase("Approve")){
					staff = getUsersToNextWorkstep(ifr,"LE_IA");
				
					logger.info("staff: "+staff);
				
				if (!staff.equalsIgnoreCase(EMPTY))
					setUsersToMail(ifr,staff,"","","SEND_TO","","");
				}
				
				else if(decision.equalsIgnoreCase("Return")) {
					group = "HNFT_"+sol;
					
					logger.info("group: "+group);
					
					staff = getUsersToNextWorkstep(ifr,group);
					
					logger.info("staff: "+staff);
				
				if (!staff.equals(EMPTY))
					setUsersToMail(ifr,staff,"","","SEND_TO","","");	
				}
			}
			break;

			}
		} catch (Exception ex) {
			logger.info("Exception-->" + ex.getStackTrace().toString());
		}
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
