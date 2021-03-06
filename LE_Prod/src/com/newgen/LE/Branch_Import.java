package com.newgen.LE;

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

public class Branch_Import extends Commons implements IFormServerEventHandler, Constants {
	public static Logger logger = LogGEN.getLoggerInstance(Branch_Import.class);

	@Override
	public void beforeFormLoad(FormDef arg0, IFormReference ifr) {
		// TODO Auto-generated method stub
		ifr.setValue("SEND_TO", "");
		ifr.setValue("SEND_CC", "");
		ifr.setValue("SEND_BCC", "");
		
		String decision = (String)ifr.getValue("Decision");
		logger.info("decision: "+decision);
		
		String prevWS = (String)ifr.getValue("Prev_WS");
		logger.info("prevWS: "+prevWS);
		
		String sol = (String)ifr.getValue("AccountSOL_ID");
		logger.info("sol: "+sol);
		
		String staff ="";
		String group = "";
		
		//if (prevWS.equalsIgnoreCase("") || prevWS == null){
			group = "HNFT_"+sol;
			logger.info("group: "+group);
			
			//staff = getUsersToNextWorkstep(ifr,group);
			staff = "sn029216@firstbanknigeria.com";
		
			logger.info("staff: "+staff);
		
		//if (!staff.equals(EMPTY))
			setUsersToMail(ifr,staff,"","","sendTo","","");
		//}

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
	public String executeServerEvent(IFormReference arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
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
		return null;
	}

	@Override
	public JSONArray validateSubmittedForm(FormDef arg0, IFormReference arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
