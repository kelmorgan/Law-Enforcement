
package com.newgen.LE;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
//import org.ini4j.Ini;
//import org.ini4j.InvalidFileFormatException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.newgen.iforms.EControl;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.user.CIDocumentCopyException;
import com.newgen.iforms.user.Constants;
import com.newgen.iforms.user.XmlParser;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;
//import com.newgen.omni.jts.cmgr.XMLParser;//anil.ojha@19-Feb-2019
//import com.newgen.omni.wf.util.app.NGEjbClient;//anil.ojha@19-Feb-2019
//import com.newgen.omni.wf.util.excp.NGException;//anil.ojha@19-Feb-2019
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("unused")
public class Commons implements Serializable, Constants {
	private static final long serialVersionUID = -681923235023537133L;
	public static Logger logger = LogGEN.getLoggerInstance(Commons.class);
	//protected NGEjbClient ngEjbClient = null;//anil.ojha@19-Feb-2019
	public String decisionTaken = "";
	public String currentWorkstep = "";
	public String sSessionID = "";
	public String currentWIName = "";
	public String prevWorkstep = "";

	
	
	public void checkDecision (IFormReference ifr, String decision, String leaNo, String reqAgency, String location, String table, String remarks){
		 if (decision.equalsIgnoreCase("Discard")){
			ifr.setStyle(leaNo, "disable", "true"); 
			ifr.setStyle(reqAgency, "disable", "true"); 
			ifr.setStyle(location, "disable", "true"); 
			ifr.setStyle(table, "disable", "true"); 
			ifr.setStyle(leaNo, "mandatory", "false"); 
			ifr.setStyle(remarks, "mandatory", "false"); 
			ifr.setStyle(reqAgency, "mandatory", "false"); 
		 }
		 else {
			 	ifr.setStyle(leaNo, "disable", "false"); 
				ifr.setStyle(reqAgency, "disable", "false"); 
				ifr.setStyle(location, "disable", "false"); 
				ifr.setStyle(table, "disable", "false"); 
				ifr.setStyle(leaNo, "mandatory", "true"); 
				ifr.setStyle(remarks, "mandatory", "true"); 
				ifr.setStyle(reqAgency, "mandatory", "true"); 
		 }
	}
	
	public String getUsersToNextWorkstep(IFormReference ifr, String groupName){
		
		String staffList ="";
		String endmail = "";
		String query = "select distinct (pu.username) "
				+ "from pdbuser pu, pdbgroup g, pdbgroupmember gm where gm.groupindex = (select g.groupindex from PDBGroup where GroupName='"+groupName+"')  "
				+ "and pu.userindex in (select userindex from pdbgroupmember "
				+ "where groupindex = (select groupindex from PDBGroup where GroupName='"+groupName+"')) and g.groupname = '"+groupName+"'";
		logger.info("query: "+query);
		
		List<List<String>> getUsers = ifr.getDataFromDB(query);
		logger.info("getUsers: "+getUsers);
		
		int count = getUsers.size();
		
		logger.info("count: "+count);
		
		for (int i = 0 ; i < count ; i++){
			 endmail = "@firstbanknigeria.com";
			 String staff = getUsers.get(i).get(0)+endmail;
			 logger.info("staff: "+staff);
			
			 staffList = staff+","+staffList;
		}
		
		logger.info("staffList: "+staffList);
		
		return staffList.trim();
	}
	
	public void setUsersToMail(IFormReference ifr, String staff1, String staff2, String staff3, String sendTo, String sendCc, String sendBcc){
		if (!sendTo.equalsIgnoreCase(EMPTY))
			ifr.setValue(sendTo, staff1);
		
		if (!sendCc.equalsIgnoreCase(EMPTY))
			ifr.setValue(sendCc, staff2);
		
		if (!sendBcc.equalsIgnoreCase(EMPTY))
			ifr.setValue(sendBcc, staff3);
	}
	

	public String checkLeRef(IFormReference iFormRef, String winame, String leaRef){
		String response = "";
		String query ="select count(lea_reference_number) from ext_law_enforcement  where lea_reference_number = '"+leaRef+"'";
		
		List<List<String>> checkLeaRef = iFormRef.getDataFromDB(query);
		int count = Integer.parseInt(checkLeaRef.get(0).get(0));
		
		if (count > 0){
			iFormRef.setValue("LEA_Reference_Number",EMPTY);
			response = "This LEA Reference Number Has Been Used";
		}
	
		return response;
	}
	
	public void setInitiationDate(IFormReference iFormRef, String initiationDate, String currentDate){
	    iFormRef.setValue(initiationDate,currentDate);  
	}
	
	public void getRequestType(IFormReference ifr,String sData, String table){
		int rowCount = Integer.parseInt(sData);
		for(int i = 0; i < rowCount; i++){
			String requestType = ifr.getTableCellValue(table, i, 5);
			if(!EMPTY.equalsIgnoreCase(requestType)){
				ifr.setValue("TYP_OF_REQUEST", requestType);
			
				break;
			}
		}
	}
	
	public void setSolId(IFormReference iFormRef, String sData, String table){
		String location = (String)iFormRef.getValue("Location_LEA");
		String leaRefNumber = (String)iFormRef.getValue("LEA_Reference_Number");
		String requestingAgency=(String)iFormRef.getValue("Requesting_Agency");
		
		int rowCount = Integer.parseInt(sData);
		for(int i = 0; i < rowCount; i++){
			String accountNumber = iFormRef.getTableCellValue(table, i, 0);
			String accountName = iFormRef.getTableCellValue(table, i, 1);
			String custmerBranch = iFormRef.getTableCellValue(table, i, 2);
			String solId = iFormRef.getTableCellValue(table, i, 3);
			String accountStatus = iFormRef.getTableCellValue(table, i, 4);
			String requestType = iFormRef.getTableCellValue(table, i, 5);
			if(!EMPTY.equalsIgnoreCase(solId)){
				iFormRef.setValue("AccountNumber1", accountNumber);
				iFormRef.setValue("AccountName", accountName);
				iFormRef.setValue("Branch", custmerBranch);
				iFormRef.setValue("AccountSOL_ID", solId);
				iFormRef.setValue("accountStatus", accountStatus);
				iFormRef.setValue("TYP_OF_REQUEST", requestType);
			
				break;
			}
		}	
		iFormRef.setValue("Reference_Number1", leaRefNumber);
		iFormRef.setValue("Agency", requestingAgency);
		iFormRef.setValue("Location", location);
	}
	
	public boolean checkDocuments(IFormReference iFormRef,String winame,String supportingDoc){
		String query = "SELECT COUNT(1) " +
				"FROM pdbdocument A, PDBDocumentContent B " +
				"WHERE A.DocumentIndex=B.DocumentIndex " +
				"AND B.ParentFolderIndex=(select FolderIndex from PDBFolder where name ='"+ winame + "') " +
				"AND A.Name like '"+ supportingDoc +"%'";
		
		logger.info("query of commons in acknowledge copy: "+query);

	
		List<List<String>> docList = iFormRef.getDataFromDB(query);
		logger.info("this is doclist: "+docList);
		Iterator itdoc = docList.iterator();
		String docCount ="";
		while (itdoc.hasNext()) {
			docCount = itdoc.next().toString();
			logger.info("this is docCount: "+docCount);
			docCount = docCount.substring(docCount.indexOf("[") + 1, docCount.indexOf("]"));
			logger.info("docCount: "+docCount);
			int docCounts = Integer.parseInt(docCount);
			logger.info("docCount: "+docCounts);
			if (docCounts == 0)
				return false;
		}
		return true;
	}
	
	public void apiCall(IFormReference iFormRef, String custAcctNo) {
		if(custAcctNo.charAt(0)=='3') {
			String temp  ="<SBAcctInqRequest><SBAcctInqRq><SBAcctId><AcctId>"+custAcctNo+"</AcctId><AcctType><SchmType>SBA</SchmType></AcctType></SBAcctId></SBAcctInqRq></SBAcctInqRequest>";
			String cust = "SBACC_INQ";

			iFormRef.setValue("table4_appCode",cust);
			iFormRef.setValue("table4_Fidata",temp);

			iFormRef.setStyle("fetchCurrentAccount", "visible", "false");
			iFormRef.setStyle("fetchSpecialAccount", "visible", "false");
			iFormRef.setStyle("fetchSavingsAccount", "visible", "true");

			iFormRef.setValue("table4_CustomerName", "");
			iFormRef.setValue("table4_CustomerBranch", "");
			iFormRef.setValue("table4_SOL_ID", "");
			iFormRef.setValue("table4_AccountStatus", "");
			iFormRef.setValue("table4_status", "");
			iFormRef.setValue("table4_errorCode", "");
			iFormRef.setValue("table4_errorDesc", "");
			iFormRef.setValue("table4_errorDesc", "");
		}
		else if(custAcctNo.charAt(0)=='2') {
			String temp  ="<ODAcctInqRequest><ODAcctInqRq><ODAcctId><AcctId>"+custAcctNo+"</AcctId><AcctType><SchmType>ODA</SchmType></AcctType></ODAcctId></ODAcctInqRq></ODAcctInqRequest>";
			String cust = "ODAACC_INQ";

			iFormRef.setValue("table4_appCode",cust);
			iFormRef.setValue("table4_Fidata",temp);

			iFormRef.setStyle("fetchCurrentAccount", "visible", "true");
			iFormRef.setStyle("fetchSpecialAccount", "visible", "false");
			iFormRef.setStyle("fetchSavingsAccount", "visible", "false");

			iFormRef.setValue("table4_CustomerName", "");
			iFormRef.setValue("table4_CustomerBranch", "");
			iFormRef.setValue("table4_SOL_ID", "");
			iFormRef.setValue("table4_AccountStatus", "");
			iFormRef.setValue("table4_status", "");
			iFormRef.setValue("table4_errorCode", "");
			iFormRef.setValue("table4_errorDesc", "");
			iFormRef.setValue("table4_mailGroup", "");
		}
		else if(custAcctNo.charAt(0)=='1'){
			String temp  ="<CAAcctInqRequest><CAAcctInqRq><CAAcctId><AcctId>"+custAcctNo+"</AcctId></CAAcctId></CAAcctInqRq></CAAcctInqRequest>";
			String cust = "CAAcctInq";

			iFormRef.setValue("table4_appCode",cust);
			iFormRef.setValue("table4_Fidata",temp);

			iFormRef.setStyle("fetchCurrentAccount", "visible", "false");
			iFormRef.setStyle("fetchSpecialAccount", "visible", "true");
			iFormRef.setStyle("fetchSavingsAccount", "visible", "false");

			iFormRef.setValue("table4_CustomerName", "");
			iFormRef.setValue("table4_CustomerBranch", "");
			iFormRef.setValue("table4_SOL_ID", "");
			iFormRef.setValue("table4_AccountStatus", "");
			iFormRef.setValue("table4_status", "");
			iFormRef.setValue("table4_errorCode", "");
			iFormRef.setValue("table4_errorDesc", "");
			iFormRef.setValue("table4_mailGroup", "");
		}
	}
	
	public boolean makeTableCompulsory(IFormReference iFormRef, String sData){
		int rowCount = Integer.parseInt(sData);
		if (rowCount < 1)
			return false;
	return true;
	}
		
	public boolean getValueOfRequest(IFormReference iFormRef, String sData, String table, int columnId){
		int rowCount = Integer.parseInt(sData);
		for(int i = 0; i < rowCount; i++){
			String typeOfRequest = iFormRef.getTableCellValue(table, i, columnId);
			if(EMPTY.equalsIgnoreCase(typeOfRequest))
				return false;
		}
		return true;
	}
	
	public void setIsDomiciledResult(IFormReference iFormRef,String accountTable,int rowId,int columnId ,String SolId, String isDomiciled){
			String branchId = iFormRef.getTableCellValue(accountTable, rowId, columnId);
			 logger.info("branchId: "+branchId);
			 
			 logger.info("SolId: "+SolId);
			 
			if (SolId.equalsIgnoreCase(branchId)){
				iFormRef.setValue(isDomiciled,"Yes");
			}
			else {
				iFormRef.setValue(isDomiciled,"No");	
			}
	}
		
	public void isDomiciledVisibleTrueOrFalse(IFormReference iFormRef, String sData, String isDomiciled){
		int rowCount = Integer.parseInt(sData);
		if(rowCount > 1)
			iFormRef.setStyle(isDomiciled,"visible","false");
		else
			iFormRef.setStyle(isDomiciled,"visible","true");	
	}
		
	public void BrnIni(IFormReference ifr) {
		WDGeneralData wdgeneralObj= ifr.getObjGeneralData(); 
	    String winame =wdgeneralObj.getM_strProcessDefId();
	    String activityName=wdgeneralObj.getM_strActivityName();	  
	    String userName=wdgeneralObj.getM_strUserName();
	
		
	    ifr.setValue("WIName",winame);
		
	    logger.info("LE : Branch_Initiator : winame"+winame);
	    logger.info("LE : Branch_Initiator : "+ifr.getUserName());
	    
		    ifr.setValue("StaffID",userName);
		    ifr.setValue("Account_Branch_Name","Iganmu");
		    ifr.setValue("Initiator_SOL_ID","0090");
		    ifr.setValue("Account_Branch_Name","Marina");
		    ifr.clearCombo("Decision");
		    ifr.addItemInCombo("Decision", "Submit", "Submit", "tooltip");
		    ifr.addItemInCombo("Decision", "Discard", "Discard", "tooltip");
	}
		
	public void dispalyAcctDomiciled (IFormReference ifr) {
			int gridCount= (int) ifr.getValue("gridCount");
		if (gridCount > 1)
			ifr.setStyle("Is_Request_Domiciled","visible","false");
	}
		
	public void Review_Ack(IFormReference ifr) {
			
		  logger.info("LE : Review_Ack ss");
		
			WDGeneralData wdgeneralObj= ifr.getObjGeneralData(); 
		    String winame =wdgeneralObj.getM_strProcessDefId();
		    String activityName=wdgeneralObj.getM_strActivityName();	  
		    String userName=wdgeneralObj.getM_strUserName();
	
			
		    ifr.setValue("WIName",winame);
			
		    logger.info("LE : Branch_Initiator : winame"+winame);
		    logger.info("LE : Branch_Initiator : "+ifr.getUserName());
			ifr.setValue("StaffID", userName);
			ifr.setStyle("Initiator_SOL_ID", "visible", "false");
			ifr.setStyle("Account_Branch_Name", "visible", "false");
			// ifr.clearValue('Decision','true');
			//clearValue('Remarks','true');
			//ifr.setColumnVisible("table4",5,true,true);
			ifr.setStyle("AccountNumber", "disable", "true");
		    ifr.setStyle("CustomerName", "disable", "true");
		    ifr.setStyle("CustomerBranch", "disable", "true");
		    ifr.setStyle("SOL_ID","disable","true");
		    ifr.setStyle("AccountStatus", "disable", "true");
		   	ifr.setStyle("Type_of_Request", "disable", "false");
			ifr.setStyle("LEA_Reference_Number", "disable", "false");
			ifr.setStyle("typ_of_req", "visible", "true");
	}
	
	public void Attach_Doc(IFormReference ifr) {
		WDGeneralData wdgeneralObj= ifr.getObjGeneralData(); 
	    String winame =wdgeneralObj.getM_strProcessDefId();
	    String activityName=wdgeneralObj.getM_strActivityName();	  
	    String userName=wdgeneralObj.getM_strUserName();
	
		
	    ifr.setValue("WIName",winame);
		
	    logger.info("LE : Branch_Initiator : winame"+winame);
	    logger.info("LE : Branch_Initiator : "+ifr.getUserName());
	    ifr.setValue("StaffID", userName);
	    ifr.setValue("Initiator_SOL_ID","0090");
	    ifr.setValue("Account_Branch_Name","Marina");
		ifr.setStyle("frame4", "disable", "true");
		
	}
	
	public void End(IFormReference ifr) {
	
		ifr.setStyle("headerframe1", "disable", "true");
		ifr.setStyle("frame4", "disable", "true");
		ifr.setStyle("frame1", "disable", "true");
	}
	public String generateHTML(EControl paramEControl) {
        return null;
}

public String introduceWorkItemInWorkFlow(IFormReference paramIFormReference, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) {
        return null;

}
}