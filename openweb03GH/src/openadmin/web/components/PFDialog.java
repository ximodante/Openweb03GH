package openadmin.web.components;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.context.RequestContext;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.dialog.Dialog;

import openadmin.model.Base;
import openadmin.util.faces.UtilFaces;

public class PFDialog {

	private OutputPanel panelDialogo;	
	
	
	public String  panel01(List<Base> lstbase) {
		
		//PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(severity, summary, detail));
		Map<String,Object> properties  = new HashMap<String,Object>();
		properties.put("modal", true);
		properties.put("resizable", true);
		properties.put("draggable", true);
		properties.put("width", 600);
		properties.put("height", 600);
		
		PrimeFaces.current().dialog().openDynamic("/pages/dialogs/dialogo01.xhtml", properties, null);
		//DefaultRequestContext.getCurrentInstance().openDialog("/pages/dialogs/dialogo02.xhtml", properties, null);
		
		//UtilFaces.findComponentOfId(":formDialog01", "idContingutDialog");
		
		return "";
		
	}
	
	public void panel02(List<Base> lstbase) {
		
		FacesContext _context = FacesContext.getCurrentInstance();	
		Dialog dialog = (Dialog) _context.getViewRoot().findComponent("form1:dialogo01");
		
		UIComponent form = _context.getViewRoot().findComponent("form1");
		
		if (null != dialog) {
			 
			dialog.getChildren().clear();
			UtilFaces.removeComponentOfId(form, "dialogo01");
			
		} else {
			
			dialog =  (Dialog) _context.getApplication().createComponent(Dialog.COMPONENT_TYPE);
			dialog.setId("dialogo01");
		}
		
		System.out.println("Dialogo:  " + dialog);
		
		dialog.setWidgetVar("widget");
		dialog.setHeader("Carles");
		dialog.setVisible(true);
		dialog.setMinimizable(true);
		dialog.setDynamic(true);
		dialog.setFooter("Alex");
		dialog.setDraggable(true);
		dialog.setMinWidth(600);
		dialog.setClosable(true);
		dialog.setModal(true);
		
		
		dialog.getChildren().add(createDataTable(lstbase));
		
		form.getChildren().add(dialog);
		
		RequestContext request = RequestContext.getCurrentInstance();
		request.execute("PF('widget').show()");
		request.update("form1");
		
	}
	
	private DataTable createDataTable(List<Base> lstbase) {
		
		FacesContext _context = FacesContext.getCurrentInstance();
		
		DataTable table = (DataTable) _context.getApplication().createComponent(DataTable.COMPONENT_TYPE);
		
		table.setId("idlist");
		table.setRows(10);
		table.setValue(lstbase);
		table.setVar("pbase");
		
		Base base = lstbase.get(0);
		Column column;
		
		for (Field f: base.getClass().getDeclaredFields()){				
			//Exclusions
			
			System.out.println("Atributs:  " + f.getName());
			
			
			if (	f.getName().equals("debuglog") ||
					f.getName().equals("historiclog") ||
					f.getName().equals ("serialVersionUID"))
					continue;
			
			//View of fields 
			f.setAccessible(true);
			
				column  = (Column) _context.getApplication().createComponent(Column.COMPONENT_TYPE);
				
				//New column name
				HtmlOutputText head = new HtmlOutputText();
				
				//Value column head
				head.setValue(f.getName());
				column.setHeader(head);
												
				//add component to column
				//typeComponent(f);
				//If is String	
				if (f.getType().getSimpleName().endsWith("String") || 
					f.getType().getSimpleName().endsWith("Integer") ||			
					f.getType().getSimpleName().endsWith("Date")) {	
				
				 HtmlOutputText outText = new HtmlOutputText();
				 
				 outText.setValueExpression("value", _context.getApplication().getExpressionFactory().createValueExpression(
						 _context.getELContext(), 
						 "#{pbase." +  f.getName() + "}",
						  f.getType()));
			
				 column.getChildren().add(outText);
				
				}
				
				
				//add column to data table
				table.getChildren().add(column);	
		}
		
		return table;
	}

}
