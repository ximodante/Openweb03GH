package openadmin.web.components;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.primefaces.component.menubar.Menubar;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import openadmin.model.control.Access;
import openadmin.model.control.Action;
import openadmin.model.control.EntityAdm;
import openadmin.util.lang.LangType;


public class PFMenuBar implements Serializable{
	
	private static final long serialVersionUID = 9081501L;
	
	private LangType langType;
	
	
	public PFMenuBar (LangType pLangType) {
		
		langType = pLangType;
	}
	
	public DefaultSubMenu menuEntities(String head, Set<EntityAdm> listEntities){
		
		DefaultSubMenu subMenu = new DefaultSubMenu(langType.msgGenerals(head));
		
		for (EntityAdm enti: listEntities) {
			
			String pValorItem = langType.msgGenerals(enti.getDescription());
			DefaultMenuItem item = new DefaultMenuItem(pValorItem); 
			item.setIcon(enti.getIcon());
			item.setId("" + enti.getId());
			item.setCommand("#{main.selectActiveEntity(" + enti.getId() + ")}");
			item.setUpdate("form1");
		}
		
		
		return subMenu;
	}
	
	/**
	 * <desc>class that generates the applications submenu</desc>
	 * @author 			Alfred Oliver
	 * @param head    	submenu name 
	 * @param listEp  	programs that the user is allowed for each entity
	 * @return        	HtmlDropDownMenu*/
	 
	public DefaultSubMenu menuPrograms(String head, List<Access> listProgram){
		
		DefaultSubMenu subMenu = new DefaultSubMenu(langType.msgGenerals(head));
		
		for (Access ac: listProgram){
				 
				//System.out.println("programa: " + ac.getProgram().getDescription());
			
				//Create menu item
				DefaultMenuItem item = new DefaultMenuItem(langType.msgGenerals(ac.getProgram().getDescription())); 
				item.setIcon(ac.getProgram().getIcon());
				
				//Id menu item (id entity and id program)
				//item.setId("idi" +ac.getEntityAdm().getId() + "_" + ac.getRole().getId());
				item.setId("" + ac.getRole().getId());
				item.setCommand("#{main.loadMenuItems(" + ac.getRole().getId() + ","  + ac.getProgram().getId() + ")}");
				item.setUpdate("form1:idMenuLateral");
				//item.setUpdate("frmplan1:idMenuLateral frmplan1:rutaPrograma");
				
				subMenu.addElement(item);

		}
		
		return subMenu;
		
	}
	
	public Menubar menuAccions(Integer view, List<Action> pLstAction){
		
		Menubar menuBar = new Menubar();
		menuBar.setStyleClass("menubarTp1");
		
		MenuModel menuModel = new DefaultMenuModel();
		
		DefaultMenuItem item = null;
		
		String label;
		
		for (Action ac: pLstAction){
			
			if (ac.getDescription().indexOf(":") > 0){
				
				label = ac.getDescription().substring(ac.getDescription().indexOf(":")+1).trim();
				item = new DefaultMenuItem(langType.msgActions(label));
				item.setStyleClass("itemTp1");
				item.setId("" + ac.getId());
				item.setIcon("fa fa-fw " + ac.getIcon());
				item.setCommand("#{ctx.getView("+ view + ").otherAction(\"" + label + "\")}");
				item.setUpdate("form1:idContingut");
				menuModel.addElement(item);
				continue;
			}
			
			
			label = ac.getDescription().substring(ac.getDescription().indexOf("_")+1);
			
			item = new DefaultMenuItem(langType.msgActions(label)); 
			item.setStyleClass("itemTp1");
			item.setId("" + ac.getId());
			item.setIcon("fa fa-fw " + ac.getIcon());
			item.setCommand("#{ctx.getView(" + view + ")." + ac.getDescription().substring(ac.getDescription().indexOf("_")).trim() + "()}");
			item.setUpdate("form1:idContingut");
			menuModel.addElement(item);
			
		}
		
		if (view > 1){
			
			item = new DefaultMenuItem(langType.msgActions("clean"));
			item.setStyleClass("itemTp1");
			item.setId("iditemclean");
			item.setIcon("fa fa-fw fa-eraser");
			item.setCommand("#{ctx.getView(" + view + ").clean}");
			item.setUpdate("form1:idContingut");
			menuModel.addElement(item);
			
			item = new DefaultMenuItem(langType.msgActions("exit"));
			item.setStyleClass("itemTp1");
			item.setId("iditemexit");
			item.setIcon("fa fa-fw fa-external-link");
			item.setCommand("#{main.exitScreenRecursive()}");
			item.setUpdate("form1:idContingut");
			menuModel.addElement(item);
			
		}
		
		menuBar.setModel(menuModel);
			
		return menuBar;
		
	}
}
