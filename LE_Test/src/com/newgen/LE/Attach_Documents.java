package com.newgen.LE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import com.newgen.EmailSetup.MailSetup;
import com.newgen.LE.Commons;
import com.newgen.LE.LogGEN;
import com.newgen.iforms.EControl;
import com.newgen.iforms.FormDef;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.custom.IFormServerEventHandler;
import com.newgen.iforms.user.Constants;
import com.newgen.mvcbeans.model.WorkdeskModel;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;

public class Attach_Documents extends Commons implements IFormServerEventHandler, Constants{
	
	public static Logger logger = LogGEN.getLoggerInstance(Attach_Documents.class);
	String branchName;
	String branchId;
	MailSetup mailSetup = new MailSetup ();
		@Override
		public void beforeFormLoad(FormDef arg0, IFormReference ifr) {
			ifr.setValue("SEND_TO", "");
			ifr.setValue("SEND_CC", "");
			ifr.setValue("SEND_BCC", "");
			
			 String winame=  (String) ifr.getControlValue("WorkItemName");
			 logger.info("winame: "+winame);
			
			String decision = (String)ifr.getValue("Decision");
			logger.info("decision: "+decision);
			
			String prevWS = (String)ifr.getValue("Prev_WS");
			logger.info("prevWS: "+prevWS);
			
			String sol = (String)ifr.getValue("AccountSOL_ID");
			logger.info("sol: "+sol);
			
			String staff ="";
			String group = "";
			
			String check = (String)ifr.getValue("CR_COUNT");
			logger.info("this is check: "+check);
			ifr.setValue("StaffID", ifr.getUserName());
			
		//	String query = "SELECT UBM.USERID USERID, UBM.BRANCHID BRANCHID, UBM.BRANCHNAME BRANCHNAME FROM LE_USR_BRANCH_MAPPING_MTR UBM, LE_BRANCH_MTR BMTR WHERE BMTR.BRANCHID=UBM.BRANCHID AND UPPER(USERID)=UPPER('" + ifr.getUserName() + "')";
			String query = "select sole_id , branch_name  from  usr_0_fbn_usr_branch_mapping where upper(user_id) = upper('"+ ifr.getUserName() +"')";
			
			logger.info("this is the query: "+ query);

			List<List<String>> getGeneralDetails = ifr.getDataFromDB(query);
			logger.info("this query from db: "+ getGeneralDetails);
			branchName = getGeneralDetails.get(0).get(1);
			ifr.setValue("Account_Branch_Name", branchName);
			branchId = getGeneralDetails.get(0).get(0);
			ifr.setValue("ATTAC_INIT_SOL", branchId);
			ifr.setStyle("ATTAC_INIT_SOL", "visible", "true");
					
			if(check.equalsIgnoreCase("YES")){
				ifr.setStyle("frame4","disable","true");
				ifr.setStyle("frame4","visible","true");
				ifr.setStyle("frame5","visible","false");
				ifr.setStyle("typ_of_Req","visible","true");
				ifr.setStyle("Is_Request_Domiciled","visible","false");
			}
			else {
				ifr.setStyle("frame4","visible","false");
				ifr.setStyle("frame5","disable","true");
				ifr.setStyle("Account_Branch_Name","visible","false");
				ifr.setStyle("headerframe1","disable","true");
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
			logger.info("LE : Attach_Documents : executeServerEvent :  controlName : "+controlName );
			logger.info("LE : Attach_Documents : executeServerEvent :  value : "+value );
			logger.info("--executeServerEvent--");
			logger.info("sEventName-->" + eventName);
			String prevWs= (String)ifr.getValue("Prev_WS");
			String attachedSolId = (String)ifr.getValue("ATTAC_INIT_SOL");
			String orginatingSolId = (String)ifr.getValue("Initiator_SOL_ID");
			String check = (String)ifr.getValue("CR_COUNT");
			
		

			try {
				switch (eventName) {

				case CLICK:
					break;
				case FORMLOAD: {
					if (prevWs.equalsIgnoreCase(null)||prevWs.equalsIgnoreCase(""))
						ifr.setValue("Prev_WS", Review_and_advise_action);
					
					if(attachedSolId.equalsIgnoreCase(orginatingSolId))
						ifr.setStyle("Initiator_SOL_ID","visible","false");
					
					
					
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
						if (orginatingSolId.equalsIgnoreCase(attachedSolId) && decision.equalsIgnoreCase("Approve")){
							boolean checkDocs= checkDocuments(ifr,winame,"Acknowledgement Copy");
							logger.info("Acknowledgement Copy:"+checkDocs);
							boolean checkDocsSupport = checkDocuments(ifr,winame,"Supporting Documents");
							logger.info("Supporting Documents:"+checkDocsSupport);
							if(checkDocs == false || checkDocsSupport == false)
								return "Please Attach Documents";
						}
						
						else if (decision.equalsIgnoreCase("Approve")){
						String sDocPresentQuery = "SELECT COUNT(1) FROM pdbdocument A, PDBDocumentContent B WHERE A.DocumentIndex=B.DocumentIndex AND B.ParentFolderIndex=(select FolderIndex from PDBFolder where name ='"+ winame + "') and A.Name like 'Supporting Documents%'"; //Owner = 83
						List docList = new ArrayList();
						docList = ifr.getDataFromDB(sDocPresentQuery);
						Iterator itdoc = docList.iterator();
						String sDocCount = "";
						while (itdoc.hasNext()) {
							sDocCount = itdoc.next().toString();
							sDocCount = sDocCount.substring(sDocCount.indexOf("[") + 1, sDocCount.indexOf("]"));
							if (sDocCount.equalsIgnoreCase("0")) {
								String otp = "";
								otp = "false";
								return otp;
							}
						}
						}
						if (controlName.equalsIgnoreCase("Is_Request_Domiciled") && eventName.equalsIgnoreCase("Done")) {
							ifr.setValue("Is_Request_Domiciled", value);
							logger.info("LE : Attach_Documents Value setted");
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
					String group = "";
					
					if (sol.equalsIgnoreCase(initSol) && decision.equalsIgnoreCase("Approve")){
						staff = getUsersToNextWorkstep(ifr,"LE_IA");
					
						logger.info("staff: "+staff);
					
					if (!staff.equals(EMPTY))
						setUsersToMail(ifr,staff,"","","SEND_TO","","");
					}
					
					else if(!sol.equalsIgnoreCase(initSol) && decision.equalsIgnoreCase("Approve")) {
						
						group = "HNFT_"+initSol;
						logger.info("group: "+group);
						
						staff = getUsersToNextWorkstep(ifr,group);
						logger.info("staff: "+staff);
						
						staff2 = getUsersToNextWorkstep(ifr,"LE_IA");
						logger.info("staff2: "+staff2);
					
					if (!staff.equalsIgnoreCase(EMPTY) || !staff2.equalsIgnoreCase(EMPTY))
						setUsersToMail(ifr,staff,staff2,"","SEND_TO","SEND_CC","");	
					}
					
					else if (decision.equalsIgnoreCase("Return")){
						staff = getUsersToNextWorkstep(ifr,"LE_IA");
						logger.info("staff: "+staff);
					
					if (!staff.equalsIgnoreCase(EMPTY))
						setUsersToMail(ifr,staff,"","","SEND_TO","","");	
					}
				}
					break;
			case ONLOAD: {
				switch (controlName) {
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


