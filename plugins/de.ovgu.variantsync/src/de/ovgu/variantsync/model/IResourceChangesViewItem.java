package de.ovgu.variantsync.model;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author Lei Luo
 * 
 */
public interface IResourceChangesViewItem {

	public ArrayList<IResourceChangesViewItem> getChildren();

	public String getName();

	public IResourceChangesViewItem getParent();

	public Image getImage();

	public String getTime();

	public Action[] getActions();

	public boolean hasChildren();

	public String getPath();

	public void addChildren(IResourceChangesViewItem child);

	public void setParent(IResourceChangesViewItem parent);

	public void removeChildren(IResourceChangesViewItem child);

	public void linkFile(File file);
	
	public void setStatus(String status);
	
	public String getStatus();

}
