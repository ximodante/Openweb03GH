package openadmin.action;

import openadmin.model.Base;
import openadmin.model.control.MenuItem;

public interface ObjectActionFacade {
	
	public Base getBase();
	public void setBase(Base pBase);
	public ContextAction getCtx();
	public void setCtx(ContextAction ctx);
	public MenuItem getMenuItem();
	public void setMenuItem(MenuItem pMenuItem);
	public String getMetodo();
	
}
