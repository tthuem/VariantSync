package de.ovgu.variantsync.applicationlayer.datamodel.resources;

import java.io.File;
import java.util.List;

import org.eclipse.swt.graphics.Image;

/**
 * Describes resources which can be synchronized. These resources are files or
 * folders. They can be deleted, added or - in case of files - changed.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 15.05.2015
 */
public interface IChangedFile {

	/**
	 * Returns children of file.
	 * 
	 * @return children
	 */
	public List<IChangedFile> getChildren();

	/**
	 * Returns name of file.
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Returns parent of file.
	 * 
	 * @return parent
	 */
	public IChangedFile getParent();

	/**
	 * Returns images used by the workbench, e.g. default image used to indicate
	 * warnings or other default workbench images.
	 * 
	 * @return image
	 */
	public Image getImage();

	/**
	 * Returns creation time.
	 * 
	 * @return time
	 */
	public String getTime();

	/**
	 * Returns indication whether file has children.
	 * 
	 * @return true if file has children; otherwise false
	 */
	public boolean hasChildren();

	/**
	 * Returns full physical path of file.
	 * 
	 * @return path
	 */
	public String getPath();

	/**
	 * Adds a child to this file.
	 * 
	 * @param child
	 *            the child to add
	 */
	public void addChildren(IChangedFile child);

	/**
	 * Sets a new parent for this file.
	 * 
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(IChangedFile parent);

	/**
	 * Links another file to this file. E.g. you could link a patch of a file to
	 * the original file.
	 * 
	 * @param file
	 *            the file to link
	 */
	public void linkFile(File file);

	/**
	 * Sets file status. Status could be added, removed or changed.
	 * 
	 * @param status
	 *            the status to set.
	 */
	public void setStatus(String status);

	/**
	 * Returns file status.
	 * 
	 * @return status
	 */
	public String getStatus();

}