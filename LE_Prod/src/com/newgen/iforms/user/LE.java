package com.newgen.iforms.user;

import java.util.List;
import org.apache.log4j.Logger;
import com.newgen.LE.Attach_Documents;
import com.newgen.LE.Attach_acknowledgement_copy;
import com.newgen.LE.Branch_Import;
import com.newgen.LE.Branch_Initiator;
import com.newgen.LE.Commons;
import com.newgen.LE.End;
import com.newgen.LE.IA_Import_Initiation;
//import com.newgen.omni.wf.util.excp.NGException;
import com.newgen.LE.IA_Initiator;
import com.newgen.LE.Internal_Audit;
import com.newgen.LE.LogGEN;
import com.newgen.LE.Originating_Branch;
import com.newgen.LE.Review_and_advise_action;
import com.newgen.iforms.custom.IFormListenerFactory;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.custom.IFormServerEventHandler;

public class LE extends Commons implements IFormListenerFactory,Constants  {
	
	
	public static Logger logger = LogGEN.getLoggerInstance(LE.class);
	//@SuppressWarnings("unchecked")
	@Override
	public IFormServerEventHandler getClassInstance(IFormReference ifr) {
		// TODO Auto-generated method stub

		IFormServerEventHandler objActivity  =null;

		String sProcessName = ifr.getProcessName();
		String sActivtyName =ifr.getActivityName();
		
		
		//logger.info("LE : --beforeFormLoad-- "+sProcessName +" " +sActivtiyName);
		
		try{
			if(sProcessName.equalsIgnoreCase("LE")){
				logger.info("LE : --sProcessName-- new jar "+sProcessName );
				if (sActivtyName!=null && sActivtyName.trim().equalsIgnoreCase(Branch_Initiator)) {
					logger.info("LE : --sActivtyName-- " +sActivtyName);
					objActivity = new Branch_Initiator();
		}
					else if(sActivtyName!=null && sActivtyName.trim().equalsIgnoreCase(IA_Initiator)) {
						objActivity = new IA_Initiator();
		}
					else if(sActivtyName!=null && sActivtyName.trim().equalsIgnoreCase(Review_and_advise_action)) {
						objActivity = new Review_and_advise_action();
		}
					else if(sActivtyName!=null && sActivtyName.trim().equalsIgnoreCase(Attach_acknowledgement_copy)) {
						objActivity = new Attach_acknowledgement_copy();
		}
					else if(sActivtyName!=null && sActivtyName.trim().equalsIgnoreCase(Attach_Documents)) {
						objActivity = new Attach_Documents();
		}
					else if(sActivtyName!=null && sActivtyName.trim().equalsIgnoreCase(Internal_Audit)) {
						objActivity = new Internal_Audit();
		}
					else if(sActivtyName!=null && sActivtyName.trim().equalsIgnoreCase(Originating_Branch)) {
						objActivity = new Originating_Branch();
		}
					else if(sActivtyName!=null && sActivtyName.trim().equalsIgnoreCase(IA_Import_Initiation)) {
						objActivity = new IA_Import_Initiation();
		}
					else if(sActivtyName!=null && sActivtyName.trim().equalsIgnoreCase(Branch_Import)) {
						objActivity = new Branch_Import();
		}
					else if (sActivtyName!=null && (sActivtyName.trim().equalsIgnoreCase("Discard") || sActivtyName.trim().equalsIgnoreCase("Query")
							|| sActivtyName.trim().equalsIgnoreCase("End"))) {
						objActivity = new End();
		}
			
		}
		}
					catch(Exception e){
					e.printStackTrace();
				}
		return objActivity;
	
		}
	
}
	
