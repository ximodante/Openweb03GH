package openadmin.web.components;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import openadmin.model.Base;

public class BaseDataModel  extends ListDataModel<Base> implements SelectableDataModel<Base>, Serializable {


		private static final long serialVersionUID = 14051803L;
		
		private List<Base> lstBase;
		
		public BaseDataModel() {
			
		}
		
	    public BaseDataModel(Object pData) {
	    	
	    	setWrappedData(pData);
	    	lstBase = (List<Base>) pData;
	    }
	
		@Override
		public Base getRowData(String pDescription) {
			
			System.out.println("Base model: " + pDescription);
			
			//List<Base> lstBase = (List<Base>) getWrappedData();  
	        for(Base pBase : lstBase) { 
	        	
	        	System.out.println("LstBase: " + pBase.getDescription());
	        	
	            if(pBase.getDescription().equals(pDescription)) 
	            	
	            	System.out.println("Base model select: " + pBase.getDescription());
	            	
	                return pBase; 
	        } 
			
			return null;
		}

		@Override
		public Object getRowKey(Base pBase) {
					
			return pBase.getDescription();
		}
	
}
