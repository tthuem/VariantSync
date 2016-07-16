package de.ovgu.variantsync.ui.view.resourcechanges;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class ProjectRoot implements IWorkspaceRoot {
	private List<IProject> list;

	public ProjectRoot(List<IProject> supportProjectList) {
		this.list = supportProjectList;
	}

	@Override
	public IProject[] getProjects() {
		IProject[] result = new IProject[list.size()];
		list.toArray(result);
		return result;
	}

	@Override
	public boolean exists(IPath path) {
		// not required
		return false;
	}

	@Override
	public IResource findMember(String name) {
		// not required
		return null;
	}

	@Override
	public IResource findMember(String name, boolean includePhantoms) {
		// not required
		return null;
	}

	@Override
	public IResource findMember(IPath path) {
		// not required
		return null;
	}

	@Override
	public IResource findMember(IPath path, boolean includePhantoms) {
		// not required
		return null;
	}

	@Override
	public String getDefaultCharset() throws CoreException {
		// not required
		return null;
	}

	@Override
	public String getDefaultCharset(boolean checkImplicit) throws CoreException {
		// not required
		return null;
	}

	@Override
	public IFile getFile(IPath path) {
		// not required
		return null;
	}

	@Override
	public IFolder getFolder(IPath path) {
		// not required
		return null;
	}

	@Override
	public IResource[] members() throws CoreException {
		// not required
		return new IResource[] {};
	}

	@Override
	public IResource[] members(boolean includePhantoms) throws CoreException {
		// not required
		return new IResource[] {};
	}

	@Override
	public IResource[] members(int memberFlags) throws CoreException {
		// not required
		return new IResource[] {};
	}

	@Override
	public IFile[] findDeletedMembersWithHistory(int depth,
			IProgressMonitor monitor) throws CoreException {
		// not required
		return new IFile[] {};
	}

	@Override
	public void setDefaultCharset(String charset) throws CoreException {
		// not required
	}

	@Override
	public void setDefaultCharset(String charset, IProgressMonitor monitor)
			throws CoreException {
		// not required

	}

	@Override
	public IResourceFilterDescription createFilter(int type,
			FileInfoMatcherDescription matcherDescription, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		// not required
		return null;
	}

	@Override
	public IResourceFilterDescription[] getFilters() throws CoreException {
		// not required
		return new IResourceFilterDescription[] {};
	}

	@Override
	public void accept(IResourceProxyVisitor visitor, int memberFlags)
			throws CoreException {
		// not required
	}

	@Override
	public void accept(IResourceVisitor visitor) throws CoreException {
		// not required

	}

	@Override
	public void accept(IResourceVisitor visitor, int depth,
			boolean includePhantoms) throws CoreException {
		// not required
	}

	@Override
	public void accept(IResourceVisitor visitor, int depth, int memberFlags)
			throws CoreException {
		// not required
	}

	@Override
	public void clearHistory(IProgressMonitor monitor) throws CoreException {
		// not required
	}

	@Override
	public void copy(IPath destination, boolean force, IProgressMonitor monitor)
			throws CoreException {
		// not required
	}

	@Override
	public void copy(IPath destination, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		// not required
	}

	@Override
	public void copy(IProjectDescription description, boolean force,
			IProgressMonitor monitor) throws CoreException {
		// not required
	}

	@Override
	public void copy(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		// not required
	}

	@Override
	public IMarker createMarker(String type) throws CoreException {
		// not required
		return null;
	}

	@Override
	public IResourceProxy createProxy() {
		// not required
		return null;
	}

	@Override
	public void delete(boolean force, IProgressMonitor monitor)
			throws CoreException {
		// not required
	}

	@Override
	public void delete(int updateFlags, IProgressMonitor monitor)
			throws CoreException {
		// not required
	}

	@Override
	public void deleteMarkers(String type, boolean includeSubtypes, int depth)
			throws CoreException {
		// not required
	}

	@Override
	public boolean exists() {
		// not required
		return false;
	}

	@Override
	public IMarker findMarker(long id) throws CoreException {
		// not required
		return null;
	}

	@Override
	public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth)
			throws CoreException {
		// not required
		return new IMarker[] {};
	}

	@Override
	public int findMaxProblemSeverity(String type, boolean includeSubtypes,
			int depth) throws CoreException {
		// not required
		return 0;
	}

	@Override
	public String getFileExtension() {
		// not required
		return null;
	}

	@Override
	public IPath getFullPath() {
		// not required
		return null;
	}

	@Override
	public long getLocalTimeStamp() {
		// not required
		return 0;
	}

	@Override
	public IPath getLocation() {
		// not required
		return null;
	}

	@Override
	public URI getLocationURI() {
		// not required
		return null;
	}

	@Override
	public IMarker getMarker(long id) {
		// not required
		return null;
	}

	@Override
	public long getModificationStamp() {
		// not required
		return 0;
	}

	@Override
	public String getName() {
		// not required
		return null;
	}

	@Override
	public IPathVariableManager getPathVariableManager() {
		// not required
		return null;
	}

	@Override
	public IContainer getParent() {
		// not required
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getPersistentProperties() throws CoreException {
		// not required
		return null;
	}

	@Override
	public String getPersistentProperty(QualifiedName key) throws CoreException {
		// not required
		return null;
	}

	@Override
	public IProject getProject() {
		// not required
		return null;
	}

	@Override
	public IPath getProjectRelativePath() {
		// not required
		return null;
	}

	@Override
	public IPath getRawLocation() {
		// not required
		return null;
	}

	@Override
	public URI getRawLocationURI() {
		// not required
		return null;
	}

	@Override
	public ResourceAttributes getResourceAttributes() {
		// not required
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getSessionProperties() throws CoreException {
		// not required
		return null;
	}

	@Override
	public Object getSessionProperty(QualifiedName key) throws CoreException {
		// not required
		return null;
	}

	@Override
	public int getType() {
		// not required
		return 0;
	}

	@Override
	public IWorkspace getWorkspace() {
		// not required
		return null;
	}

	@Override
	public boolean isAccessible() {
		// not required
		return false;
	}

	@Override
	public boolean isDerived() {
		// not required
		return false;
	}

	@Override
	public boolean isDerived(int options) {
		// not required
		return false;
	}

	@Override
	public boolean isHidden() {
		// not required
		return false;
	}

	@Override
	public boolean isHidden(int options) {
		// not required
		return false;
	}

	@Override
	public boolean isLinked() {
		// not required
		return false;
	}

	@Override
	public boolean isVirtual() {
		// not required
		return false;
	}

	@Override
	public boolean isLinked(int options) {
		// not required
		return false;
	}

	@Override
	public boolean isLocal(int depth) {
		// not required
		return false;
	}

	@Override
	public boolean isPhantom() {
		// not required
		return false;
	}

	@Override
	public boolean isReadOnly() {
		// not required
		return false;
	}

	@Override
	public boolean isSynchronized(int depth) {
		// not required
		return false;
	}

	@Override
	public boolean isTeamPrivateMember() {
		// not required
		return false;
	}

	@Override
	public boolean isTeamPrivateMember(int options) {
		// not required
		return false;
	}

	@Override
	public void move(IPath destination, boolean force, IProgressMonitor monitor)
			throws CoreException {
		// not required
	}

	@Override
	public void move(IPath destination, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		// not required
	}

	@Override
	public void move(IProjectDescription description, boolean force,
			boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		// not required
	}

	@Override
	public void move(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		// not required

	}

	@Override
	public void refreshLocal(int depth, IProgressMonitor monitor)
			throws CoreException {
		// not required
	}

	@Override
	public void revertModificationStamp(long value) throws CoreException {
		// not required
	}

	@Override
	public void setDerived(boolean isDerived) throws CoreException {
		// not required
	}

	@Override
	public void setDerived(boolean isDerived, IProgressMonitor monitor)
			throws CoreException {
		// not required

	}

	@Override
	public void setHidden(boolean isHidden) throws CoreException {
		// not required
	}

	@Override
	public void setLocal(boolean flag, int depth, IProgressMonitor monitor)
			throws CoreException {
		// not required
	}

	@Override
	public long setLocalTimeStamp(long value) throws CoreException {
		// not required
		return 0;
	}

	@Override
	public void setPersistentProperty(QualifiedName key, String value)
			throws CoreException {
		// not required
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		// not required
	}

	@Override
	public void setResourceAttributes(ResourceAttributes attributes)
			throws CoreException {
		// not required
	}

	@Override
	public void setSessionProperty(QualifiedName key, Object value)
			throws CoreException {
		// not required

	}

	@Override
	public void setTeamPrivateMember(boolean isTeamPrivate)
			throws CoreException {
		// not required
	}

	@Override
	public void touch(IProgressMonitor monitor) throws CoreException {
		// not required
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		// not required
		return null;
	}

	@Override
	public boolean contains(ISchedulingRule rule) {
		// not required
		return false;
	}

	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		// not required
		return false;
	}

	@Override
	public void delete(boolean deleteContent, boolean force,
			IProgressMonitor monitor) throws CoreException {
		// not required
	}

	@Override
	public IContainer[] findContainersForLocation(IPath location) {
		// not required
		return new IContainer[] {};
	}

	@Override
	public IContainer[] findContainersForLocationURI(URI location) {
		// not required
		return new IContainer[] {};
	}

	@Override
	public IContainer[] findContainersForLocationURI(URI location,
			int memberFlags) {
		// not required
		return new IContainer[] {};
	}

	@Override
	public IFile[] findFilesForLocation(IPath location) {
		// not required
		return new IFile[] {};
	}

	@Override
	public IFile[] findFilesForLocationURI(URI location) {
		// not required
		return new IFile[] {};
	}

	@Override
	public IFile[] findFilesForLocationURI(URI location, int memberFlags) {
		// not required
		return new IFile[] {};
	}

	@Override
	public IContainer getContainerForLocation(IPath location) {
		// not required
		return null;
	}

	@Override
	public IFile getFileForLocation(IPath location) {
		// not required
		return null;
	}

	@Override
	public IProject getProject(String name) {
		// not required
		return null;
	}

	@Override
	public IProject[] getProjects(int memberFlags) {
		// not required
		return new IProject[] {};
	}

	@Override
	public void accept(IResourceProxyVisitor arg0, int arg1, int arg2)
			throws CoreException {
		// not required
	}

}
