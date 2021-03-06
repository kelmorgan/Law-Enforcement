package com.newgen.EmailSetup;

import java.util.List;

import org.apache.log4j.Logger;

import com.newgen.LE.LogGEN;
import com.newgen.iforms.custom.IFormReference;

public class MailSetup {
	public static Logger logger = LogGEN.getLoggerInstance(MailSetup.class);
	
  
    String response="";
    String winame="";
    String toGroupMail = "";
    String ccGroupMail = "";
    String mailSubject= "";
    String mailMessage = "";
 
    
	public void mailMessageSetup (IFormReference ifr, String wiName, String toMail, String ccMail, String mSubject, String mMessage){
		toGroupMail = toMail;
		ccGroupMail = ccMail;
		mailSubject = mSubject;
		winame = wiName;
		
		/**mailMessage = "<html>"
				+ "<head>"
				+ "<style> a:link, a:visited {background-color: #e8e6e6;color: white; padding: 15px 25px;text-align: center;text-decoration: none;display: inline-block;}a:hover, a:active {background-color: red;}</style>"
				+ "</head>"
				+ "<body>"
				+ "Dear User, <br>"
				+ "<br>"+mMessage+" <br>"
				+ "<br> <a href=\"https://172.16.249.62:9443/omniapp/\" title= \"iBPS Homepage\">Kindly Click Here!</a> <br>"
				+ "<br> Please do not reply, this is a system generated mail. <br>"
				+ "</body>"
				+ "</html>";**/
		
	/**	mailMessage = "<html>"
				+ "<body>"
				+ "Dear User, <br>"
				+ "<br>"+mMessage+" <br>"
				+ "<br> <a href=\"https://172.16.249.62:9443/omniapp/\" title= \"iBPS Homepage\">Kindly Click Here</a> <br>"
				+ "<br> Please do not reply, this is a system generated mail. <br>"
				+ "</body>"
				+ "</html>";**/
		mailMessage = "<html>"
				+ "<body>"
				+ "Dear User, <br>"
				+ "<br>"+mMessage+" <br>"
				+ "<br> Please do not reply, this is a system generated mail. <br>"
				+ "</body>"
				+ "</html>";
		
		logger.info("mailMessage: " + mailMessage);
		
		setupMail(ifr);
	}
	
	private void setupMail (IFormReference ifr){
		logger.info("toGroupMail: " + toGroupMail);
		logger.info("ccGroupMail: " + ccGroupMail);
		logger.info("mailSubject: " + mailSubject);
		logger.info("mailMessage: " + mailMessage);
		logger.info("winame: " + winame);
		
		  String mailQuery = "INSERT INTO wfmailqueuetable ("
					+ "MAILFROM, "
					+ "MAILTO, "
					+ "MAILCC, "
					+ " MAILSUBJECT, "
					+ "MAILMESSAGE, "
					+ "MAILCONTENTTYPE, "
					+ "mailpriority, "
					+ "insertedby, "
					+ "MAILACTIONTYPE,  "
					+ "insertedtime, "
					+ "processdefid, "
					+ " processinstanceid, "
					+ "workitemid, "
					+ "activityid, "
					+ "MAILSTATUS) VALUES ("
					+ "'ibpsnotifications@firstbanknigeria.com', "
					+ "'"+toGroupMail+"', "
					+ "'"+ccGroupMail+"', "
					+ "'"+mailSubject+"', "
					+ "'"+mailMessage+"', "
					+ "'text/html;charset=UTF-8', "
					+ "1, "
					+ "'System', "
					+ "'TRIGGER', "
					+ "SYSDATE, "
					+ "20, "
					+ "'"+winame+"', "
					+ "1, "
					+ "10, "
					+ "'N')";
		  
		  logger.info("mailQuery: " + mailQuery);
		  
		  int resp = ifr.saveDataInDB(mailQuery);
		  logger.info("resp: " + resp);
		  
			if (resp >=0)
				logger.info("this query saved");     
		  
		  
	}
		
}

