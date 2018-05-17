package openadmin.web.components;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.dialog.Dialog;

import org.primefaces.model.SelectableDataModel; 
import javax.faces.model.ListDataModel;

import openadmin.model.Base;
import openadmin.util.faces.UtilFaces;
import openadmin.util.lang.LangType;

public class PFDialog implements Serializable {
	
	private static final long serialVersionUID = 14051801L;

	private OutputPanel panelDialogo;	
	
	private LangType langType;
	
	public PFDialog(LangType pLangType) {
		
		langType = pLangType;
		
	}	
	
	public void tabla01() {
		
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
	   
	    System.out.println("Nom ALFRED:  " + (String)sessionMap.get("user"));
	    
	    sessionMap.remove("user");
	}
	
	public void dialog01(List<Base> lstbase) {
		
		PFTable pfTable = new PFTable(langType);
		
		FacesContext _context = FacesContext.getCurrentInstance();	
		Dialog dialog = (Dialog) _context.getViewRoot().findComponent("form1:dialogo01");
		
		UIComponent form = _context.getViewRoot().findComponent("form1");
		
		if (null != dialog) {
			 
			dialog.getChildren().clear();
			
		} else {
			
			dialog =  (Dialog) _context.getApplication().createComponent(Dialog.COMPONENT_TYPE);
			dialog.setId("dialogo01");
		}
				
		dialog.setWidgetVar("widget");
		dialog.setHeader("Carles");
		dialog.setVisible(true);
		dialog.setMinimizable(true);
		dialog.setDynamic(true);
		dialog.setFooter("Alex");
		dialog.setDraggable(true);
		dialog.setMinWidth(300);
		dialog.setClosable(true);
		dialog.setModal(true);
			
		dialog.getChildren().add(pfTable.dataTable01(lstbase));
		
		MethodExpression me = _context.getApplication().getExpressionFactory().createMethodExpression(_context.getELContext(), 
				"#{ctx.getView( ctx.numberView()).closedDialog(\"" + dialog.getId() + "\")}", void.class, new Class<?>[]{SelectEvent.class});
		
		
		AjaxBehavior ajaxBehavior = new AjaxBehavior();
		ajaxBehavior.setUpdate("form1:idContingut");
		ajaxBehavior.setProcess("@this");
		ajaxBehavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(me,null));
		dialog.addClientBehavior("close", ajaxBehavior);
		
		
		form.getChildren().add(dialog);
		
		//RequestContext.getCurrentInstance().execute("PF('widget').show()");
		PrimeFaces.current().executeScript("PF('widget').show()");
		//RequestContext.getCurrentInstance().update("form1");
		PrimeFaces.current().ajax().update("form1");
	}
	
	public String  dialog02(List<Base> lstbase) {
		
		FacesContext _context = FacesContext.getCurrentInstance();
		//PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(severity, summary, detail));
		Map<String,Object> properties  = new HashMap<String,Object>();
		properties.put("modal", true);
		properties.put("resizable", true);
		properties.put("draggable", true);
		properties.put("width", 600);
		properties.put("height", 600);
		
		 Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		 sessionMap.put("user", "Oliver");
		
		PrimeFaces.current().dialog().openDynamic("/pages/dialogs/dialogo01.xhtml", properties, null);
		//DefaultRequestContext.getCurrentInstance().openDialog("/pages/dialogs/dialogo02.xhtml", properties, null);
		
		//UIComponent component = _context.getViewRoot().findComponent(":formDialog01");
		
		//System.out.println("Nom component:  " + component);
		
		//UtilFaces.findComponentOfId(component, "idContingutDialog");
		
		return "";
		
	}

}
