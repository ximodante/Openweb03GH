package openadmin.web;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import lombok.Getter;
import lombok.Setter;

import openadmin.action.ContextAction;
import openadmin.model.Base;
import openadmin.model.control.Access;
import openadmin.model.control.Action;
import openadmin.model.control.ActionViewRole;
import openadmin.model.control.EntityAdm;
import openadmin.model.control.MenuItem;
import openadmin.model.control.Program;
import openadmin.model.control.Role;
import openadmin.util.edu.ReflectionUtils;
import openadmin.util.lang.LangType;
import openadmin.util.reflection.ReflectionField;
import openadmin.web.components.PFMenuBar;
import openadmin.web.components.PFPanelMenu;
import openadmin.web.view.DefaultView;
import openadmin.web.view.ViewFacade;

@Named (value = "main")
@SessionScoped
public class MainScreen implements Serializable {
	
	// Atributs
	private static final long serialVersionUID = 6081501L;
	
	private EntityAdm activeEntity;
	
	//private Role activeRol;
	
	List<Access> lstAccess;

	/** Field that contain the connection*/
	@Inject
	private ContextAction ctx;
	
	@Inject
	private LangType lang;

	@Setter
	private MenuModel menuBar;
	
	@Getter @Setter
	private MenuModel  menuLateral;
	
	//Fi atributs

	///////////////////////////////////////////////////////////////////
	//                    Per generar el menu horizontal
	///////////////////////////////////////////////////////////////////
	/**<desc> Genera el menu amb les entitats, aplicacions i altres accions</desc>
 	 * @return Menubar
 	 */		
	public MenuModel  getMenuBar() {
				
		menuBar = new DefaultMenuModel();
		
		PFMenuBar pfMenuBar = new PFMenuBar(lang);

		// ***************   Genera el menu de les entitats y programes  ********************

		Set<EntityAdm> entities = ctx.getMapEntityAccess().keySet();		
		
		//if there are two o more entities
		 if (entities.size() > 1) {
			 
			 menuBar.addElement(pfMenuBar.menuEntities("entities", entities));
			 
		 }
		 
		if (null == activeEntity)  activeEntity = entities.stream().findFirst().get();
					
		lstAccess = ctx.getMapEntityAccess().get(activeEntity);
			
		//If there is an program
		if (lstAccess.size() == 1) {
				
			Access vaccess = lstAccess.stream().findFirst().get();
				
			loadMenuItems(vaccess.getRole().getId(), vaccess.getProgram().getId());
			
		} else menuBar.addElement(pfMenuBar.menuPrograms("programs", lstAccess));
			 
		 
		menuBar.generateUniqueIds();
		
		return menuBar;
	
	}
	
	public void selectActiveEntity(Long pEntity){
		
		EntityAdm pEntityAdm = new EntityAdm();
		pEntityAdm.setId(pEntity);
		
		activeEntity = ctx.getConnControl().findObjectPK(pEntityAdm);
		getMenuBar();
		
		//Actualitzar el context
		ctx.connEntityDefault(activeEntity.getConn());
	}
	
	public void loadMenuItems(Long pRol, Long pProgram) {
		
		ctx.getConnControl().setUser(ctx.getUser());
		
		menuLateral = new DefaultMenuModel();
		
		PFPanelMenu pPFPanelMenu = new PFPanelMenu(lang);
		
		Role rol = 	new Role();
		rol.setId(pRol);
		
		//activeRol = ctx.getConnControl().findObjectPK(rol);
		
		//Current Rol
		ctx.setActiveRol(ctx.getConnControl().findObjectPK(rol));
		
		Program program = new Program();
		program.setId(pProgram);
		
		program = ctx.getConnControl().findObjectPK(program);
		 
		ActionViewRole actionViewRole = new ActionViewRole();		 
		actionViewRole.setRole(ctx.getActiveRol());
		
		Set<MenuItem> lstMenuItems = 
				ctx.getConnControl().findObjects(actionViewRole).stream()
				.map(ActionViewRole::getMenuItem).collect(Collectors.toCollection(TreeSet::new));
		
		//Registra en el log el nom del programa seleccionat
	    if (lstMenuItems.size() > 0){
				
	    	ctx.getLog().changeProgram(program.getDescription());
	    	
	    }
				
		//Seleccioa si es pare  o fill
	    for (MenuItem vr: lstMenuItems){
					
				
	    	if (vr.getTypeNode().equals("c") && null == vr.getParent() ){
	    		
	    		//Calls the method loadChild
	    		menuLateral.addElement(pPFPanelMenu.itemFill(vr));
	    		 
	    	}
						
	    	else if (vr.getTypeNode().equals("p") && null == vr.getParent()) {
			
			  //Calls the method loadParent
	    		menuLateral.addElement(pPFPanelMenu.itemPare(vr, lstMenuItems));
	    		
	    	}
	    }
	    
	    menuLateral.generateUniqueIds();
		
	}
	
	/*****************************************************************************************************************************
	*
	*              Load screen
	********************************************************************************************************************************/
	public void loadScreen(Long pMenuItem) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		
		if (ctx.numberView() > 0) ctx.deleteAllView();
		
		MenuItem menuItem = new MenuItem();
		menuItem.setId(pMenuItem);
		menuItem = ctx.getConnControl().findObjectPK(menuItem);
		
		screen(menuItem, null);
	
	}
	
	public void loadScreenRecursive(String pMenuItem) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		

		MenuItem menuItem = new MenuItem();
		menuItem.setDescription(pMenuItem);
		menuItem = ctx.getConnControl().findObjectDescription(menuItem);
		
		//Objecte actual
		Base _obj = ctx.getView(ctx.numberView()).getBase();
		
		//Objecte a crear
		Base obj = (Base)ReflectionUtils.createObject(menuItem.getClassName().getDescription());
		
		//Find object if is instance
		if (null != _obj){
						
			obj = ReflectionField.copyObject2(_obj, (Base)obj);
			
		}
		
		
		screen(menuItem, obj);
	
	}
	
	public void exitScreenRecursive() 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
				
	
		Base _obj = ctx.getView(ctx.numberView()).getBase();
				
		ReflectionField refl = new ReflectionField();
		
		Base pObejectCopy = refl.copyObject(_obj, ctx.getView(ctx.numberView() - 1).getBase(), ctx.getView(ctx.numberView()).getMetodo());
		
		ctx.deleteView();
		
		MenuItem menuItem = ctx.getView(ctx.numberView()).getMenuItem();
		
		ctx.deleteView();
		
		screen(menuItem, pObejectCopy);
		
	}
	
	public void screen(MenuItem pMenuItem, Object obj) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException{
		
		//Delete screen
		FacesContext _context = FacesContext.getCurrentInstance();	
		OutputPanel outView = (OutputPanel)_context.getViewRoot().findComponent("form1:idContingut");
		
		if (outView.getChildCount() > 0) {
			
			outView.getChildren().clear();
		}
		
		//Action view
		ActionViewRole actionViewRole = new ActionViewRole();
		actionViewRole.setMenuItem(pMenuItem);
		actionViewRole.setRole(ctx.getActiveRol());
		
		List<Action> lstActionView = 
		ctx.getConnControl().findObjects(actionViewRole).stream()
		.map(ActionViewRole::getAction)
		.collect(Collectors.toList());
		
		//Create object
		if (null == obj) {
			
			obj = ReflectionUtils.createObject(pMenuItem.getClassName().getDescription());
			
			
		}
		
		//Default View
		if (pMenuItem.getType() == 0) {
					
			Integer numberView = ctx.numberView()+1;
			ViewFacade view = new DefaultView();
			view.setMenuItem(pMenuItem);
			view.setCtx(ctx);
			view.setBase((Base) obj);
			view.execute(lang, numberView, lstActionView);
			outView.getChildren().add(view.getOutPanel());
			
			ctx.setView(numberView, view);
					
		}	
	}
}
