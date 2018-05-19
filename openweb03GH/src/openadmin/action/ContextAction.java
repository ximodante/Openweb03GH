package openadmin.action;

import java.beans.IntrospectionException;

/**
*	Classe ContextAction
*   <desc> class that represents the context of application</desc>
*	@version  0.1
*	Creada  21-10-2009
*   Revisió 01-12-2009
*   @author Alfred Oliver 
*/

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import java.util.stream.Collectors;

import openadmin.dao.operation.DaoJpa;
import openadmin.dao.operation.DaoOperationFacade;
import openadmin.dao.operation.LogDao;
import openadmin.model.control.Access;
import openadmin.model.control.EntityAdm;
import openadmin.model.control.Role;
import openadmin.model.control.User;
import openadmin.util.configuration.FirstControlLoad;
import openadmin.util.configuration.TypeEnvironment;
import openadmin.util.configuration.TypeLanguages;
import openadmin.util.edu.CollectionUtils;
import openadmin.util.lang.LangType;
import openadmin.util.lang.WebMessages;
import openadmin.web.view.ViewFacade;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Named(value = "ctx")
@SessionScoped
public class ContextAction implements Serializable {

	private static final long serialVersionUID = 21100901L;

	/** Field that contain the connection*/
	@Getter @Setter
	private DaoOperationFacade connControl = null;
	
	/** Field that contain the connection with log*/
	private DaoOperationFacade connLog = null;
	
	/** Field that contain the connection with log*/
	//@Getter @Setter
	//private DaoOperationFacade connDefault = null;
	
	@Getter @Setter
	private Map<EntityAdm, List<Access>> mapEntityAccess = new HashMap<EntityAdm, List<Access>>();
	
	//private EntityAdm entityDefault = null;
	
	//private Role rolDefault = null;
	
	/** Field that contain the list of actions*/
	private Map<Integer, ViewFacade> lstView = new HashMap<Integer, ViewFacade>();
	
	/** Field that contain the user*/
	@Getter @Setter
	private User user;

	/** If is connect with database*/
	private boolean connected = false;
	
	/** Field that contain the Log */
	@Getter
	LogDao log = null;
	
	@Inject
	@Getter
	private LangType langType;
	
	@Getter @Setter
	private Role activeRol;
	
	private void connect() {
		//comproba si hi ha conexi� a la base de dades
		if (!connected){
			
			//hi ha que modificar l'idioma del log per el de la configuracio manual, la connexió també  
			langType.changeMessageLog(TypeLanguages.es);
			
			//Log connection
			connLog = new DaoJpa(user, "log_post", null, langType);									
			log = new LogDao(connLog, "clientweb", langType);
			
			//connection
			connControl = new DaoJpa(user, "control_post", log, langType);
			connControl.setEnvironment(TypeEnvironment.WEB);
			connected = true;
		
		}
		
	}
	
	/**<desc> Performs the application login</desc>
 	 * @return true if login user is correct
	 */
	public boolean login (User pUser) {
		
		//Usuari que es connecta
		user = pUser;
		user.setActive(true);
		user.setFirma(false);
		
		// Open JPA db connections if closed
		this.connect();
		
		user=CollectionUtils.get(connControl.findObjects(user),0);
				
		try {
			
			if (null != user) {
				loadPrograms();
				return true;
			}
			
			//if (connControl.isEmpty(user)) FirstControlLoadEdu.PersistConfiguration();
			else if (connControl.isEmpty(User.class)) new FirstControlLoad().dataLoad();
			
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IntrospectionException | RuntimeException e) {
			
			e.printStackTrace();
		
		}
		
		WebMessages.messageError("error_validation_login");
		return false;
	
	}
	
	
	
	/**
	public boolean loginFirma(String pIdentificador) {
		
		boolean exist = false;
		
		//Database connection 
		if (!connected){
				
			//hi ha que modificar l'idioma del log per el de la configuracio manual, la connexió també  
			MessagesTypes.changeMessageLog(TypeLanguages.es);
			MessagesTypes.changeMessage("es");
			
			//Log connection
			connLog = new DaoJpaHibernate(user, "log_post", null);									
			log = new LogDao(connLog, "clientweb");
			
			//connection
			connControl = new DaoJpaHibernate(user, "control_post", log);
			connControl.setEnvironment(TypeEnvironment.WEB);
			connected = true;
		
		}
		
		//if user is activate
		user.setActive(true);
								
		//Identificador
		user.setIdentifier(pIdentificador);
		
		//Find user
		List <Base> listUsers = connControl.findObjects(user);
		
		//If user exist
		if (connControl.isResultOperation()){
			
			user = (User)listUsers.get(0);
			
			if (user.getActive()){
				
				//Locale default user
				connLog.setUser(user);
				connControl.setUser(user);
				connEntityDefault(user.getEntityDefault());
				loadPrograms();
				exist = true;
				
				//load Messages languages 
				MessagesTypes.changeMessage(connControl.getUser().getLanguage());
				return exist;
			}	
		}
		
		return exist;
		
	}*/
	
	
	/**<desc> Performs the logout</desc>
 	 */
	public String logout () {
				
		user = new User();
		connControl.finalize();
		//connDefault.finalize();
		log.finalizeLog(); 
		connControl = null;
		//connDefault = null;
		connLog = null;
		connected = false;
		
		System.out.println("  Eixir 100 ");
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "index";
		
	}


	private void loadPrograms() {
				
		/*****************************************************************
		 * Find all the entities that has the user, in the table access
		 *****************************************************************/
		
		Access access = new Access();
		access.setUser(user);
				
		mapEntityAccess = 
				connControl.findObjects(access).stream()
				.collect(Collectors.groupingBy(Access::getEntityAdm));
		
	}
	
	//Work view
	public ViewFacade getView(Integer key) {
		
		System.out.println("VISTA: " + key);
		
		return lstView.get(key);
	
	}

	public void setView(Integer key, ViewFacade pVista) {
		
		lstView.put(key, pVista);
		
	}
	
	public void deleteView() {
		
		lstView.remove(lstView.size());
		
	}
	
	public void deleteAllView() {
		
		lstView.clear();
		
	}
	
	public Integer numberView() {
		
		return lstView.size();
		
	}
	
}
