package de.tubs.variantsync.core.syncronization;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.ZipFileStructureCreator;
import org.eclipse.compare.internal.BufferedResourceNode;
import org.eclipse.compare.internal.CompareUIPlugin;
import org.eclipse.compare.internal.Utilities;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.DiffTreeViewer;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

/**
 * Resource compare input for the manual merge dialog
 *
 * TODO Enable difference highlighting and feature context background
 *
 * @author Christopher Sontag
 */
public class ResourceCompareInput extends CompareEditorInput {

	private static final boolean NORMALIZE_CASE = true;

	private final boolean fThreeWay = true;
	private Object fRoot;
	private final IStructureComparator fAncestor;
	private final IStructureComparator fLeft;
	private final IStructureComparator fRight;
	private final IFile fAncestorResource;
	private final IFile fLeftResource;
	private final IFile fRightResource;
	private DiffTreeViewer fDiffViewer;
	private Action fOpenAction;

	/*
	 * Creates an compare editor input for the given selection.
	 */
	public ResourceCompareInput(CompareConfiguration config, IFile fAncestorResource, IFile fLeftResource, IFile fRightResource) {
		super(config);
		this.fAncestorResource = fAncestorResource;
		this.fLeftResource = fLeftResource;
		this.fRightResource = fRightResource;
		fAncestor = getStructure(fAncestorResource);
		fLeft = getStructure(fLeftResource);
		fRight = getStructure(fRightResource);
	}

	class MyDiffNode extends DiffNode {

		@Override
		public String toString() {
			return "MyDiffNode [fDirty=" + fDirty + ", fLastId=" + fLastId + ", fLastName=" + fLastName + "]";
		}

		private boolean fDirty = false;
		private ITypedElement fLastId;
		private String fLastName;

		public MyDiffNode(IDiffContainer parent, int description, ITypedElement ancestor, ITypedElement left, ITypedElement right) {
			super(parent, description, ancestor, left, right);
		}

		@Override
		public void fireChange() {
			super.fireChange();
			setDirty(true);
			fDirty = true;
			if (fDiffViewer != null) {
				fDiffViewer.refresh(this);
			}
		}

		void clearDirty() {
			fDirty = false;
		}

		@Override
		public String getName() {
			if (fLastName == null) {
				fLastName = super.getName();
			}
			if (fDirty) {
				return '<' + fLastName + '>';
			}
			return fLastName;
		}

		@Override
		public ITypedElement getId() {
			final ITypedElement id = super.getId();
			if (id == null) {
				return fLastId;
			}
			fLastId = id;
			return id;
		}
	}

	static class FilteredBufferedResourceNode extends BufferedResourceNode {

		FilteredBufferedResourceNode(IResource resource) {
			super(resource);
		}

		@Override
		protected IStructureComparator createChild(IResource child) {
			final String name = child.getName();
			if (CompareUIPlugin.getDefault().filter(name, child instanceof IContainer, false)) {
				return null;
			}
			return new FilteredBufferedResourceNode(child);
		}
	}

	@Override
	public Viewer createDiffViewer(Composite parent) {
		fDiffViewer = new DiffTreeViewer(parent, getCompareConfiguration()) {

			@Override
			protected void fillContextMenu(IMenuManager manager) {

				if (fOpenAction == null) {
					fOpenAction = new Action() {

						@Override
						public void run() {
							handleOpen(null);
						}
					};
					Utilities.initAction(fOpenAction, getBundle(), "action.CompareContents."); //$NON-NLS-1$
				}

				boolean enable = false;
				final ISelection selection = getSelection();
				if (selection instanceof IStructuredSelection) {
					final IStructuredSelection ss = (IStructuredSelection) selection;
					if (ss.size() == 1) {
						final Object element = ss.getFirstElement();
						if (element instanceof MyDiffNode) {
							final ITypedElement te = ((MyDiffNode) element).getId();
							if (te != null) {
								enable = !ITypedElement.FOLDER_TYPE.equals(te.getType());
							}
						} else {
							enable = true;
						}
					}
				}
				fOpenAction.setEnabled(enable);

				manager.add(fOpenAction);

				super.fillContextMenu(manager);
			}
		};
		return fDiffViewer;
	}

	/*
	 * Returns true if compare can be executed for the given selection.
	 */
	public boolean isEnabled(ISelection s) {

		final IResource[] selection = Utilities.getResources(s);
		if ((selection.length < 2) || (selection.length > 3)) {
			return false;
		}

		final boolean threeWay = selection.length == 3;

		if (threeWay) {
			// It only makes sense if they're all mutually comparable.
			// If not, the user should compare two of them.
			return comparable(selection[0], selection[1]) && comparable(selection[0], selection[2]) && comparable(selection[1], selection[2]);
		}

		return comparable(selection[0], selection[1]);
	}

	/**
	 * Initializes the images in the compare configuration.
	 */
	void initializeCompareConfiguration() {
		final CompareConfiguration cc = getCompareConfiguration();
		if (fLeftResource != null) {
			cc.setLeftLabel(buildLabel(fLeftResource));
			cc.setLeftImage(CompareUIPlugin.getImage(fLeftResource));
		}
		if (fRightResource != null) {
			cc.setRightLabel(buildLabel(fRightResource));
			cc.setRightImage(CompareUIPlugin.getImage(fRightResource));
		}
		if (fThreeWay && (fAncestorResource != null)) {
			cc.setAncestorLabel(buildLabel(fAncestorResource));
			cc.setAncestorImage(CompareUIPlugin.getImage(fAncestorResource));
		}
	}

	/*
	 * Returns true if both resources are either structured or unstructured.
	 */
	private boolean comparable(IResource c1, IResource c2) {
		return hasStructure(c1) == hasStructure(c2);
	}

	/*
	 * Returns true if the given argument has a structure.
	 */
	private boolean hasStructure(IResource input) {

		if (input instanceof IContainer) {
			return true;
		}

		if (input instanceof IFile) {
			final IFile file = (IFile) input;
			String type = file.getFileExtension();
			if (type != null) {
				type = normalizeCase(type);
				return "JAR".equals(type) || "ZIP".equals(type); //$NON-NLS-2$ //$NON-NLS-1$
			}
		}

		return false;
	}

	/*
	 * Creates a <code>IStructureComparator</code> for the given input. Returns <code>null</code> if no <code>IStructureComparator</code> can be found for the
	 * <code>IResource</code>.
	 */
	private IStructureComparator getStructure(IResource input) {

		if (input instanceof IContainer) {
			return new FilteredBufferedResourceNode(input);
		}

		if (input instanceof IFile) {
			final IStructureComparator rn = new FilteredBufferedResourceNode(input);
			final IFile file = (IFile) input;
			final String type = normalizeCase(file.getFileExtension());
			if ("JAR".equals(type) || "ZIP".equals(type)) {
				return new ZipFileStructureCreator().getStructure(rn);
			}
			return rn;
		}
		return null;
	}

	/*
	 * Performs a two-way or three-way diff on the current selection.
	 */
	@Override
	public Object prepareInput(IProgressMonitor pm) throws InvocationTargetException {

		try {
			// fix for PR 1GFMLFB: ITPUI:WIN2000 - files that are out of sync
			// with the file system appear as empty
			fLeftResource.refreshLocal(IResource.DEPTH_INFINITE, pm);
			fRightResource.refreshLocal(IResource.DEPTH_INFINITE, pm);
			if (fThreeWay && (fAncestorResource != null)) {
				fAncestorResource.refreshLocal(IResource.DEPTH_INFINITE, pm);
				// end fix
			}

			pm.beginTask(Utilities.getString("ResourceCompare.taskName"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$

			final String leftLabel = fLeftResource.getName();
			final String rightLabel = fRightResource.getName();

			String title;
			if (fThreeWay) {
				final String format = Utilities.getString("ResourceCompare.threeWay.title"); //$NON-NLS-1$
				final String ancestorLabel = fAncestorResource.getName();
				title = MessageFormat.format(format, new String[] { ancestorLabel, leftLabel, rightLabel });
			} else {
				final String format = Utilities.getString("ResourceCompare.twoWay.title"); //$NON-NLS-1$
				title = MessageFormat.format(format, new String[] { leftLabel, rightLabel });
			}
			setTitle(title);

			final Differencer d = new Differencer() {

				@Override
				protected Object visit(Object parent, int description, Object ancestor, Object left, Object right) {
					return new MyDiffNode((IDiffContainer) parent, description, (ITypedElement) ancestor, (ITypedElement) left, (ITypedElement) right);
				}
			};

			fRoot = d.findDifferences(fThreeWay, pm, null, fAncestor, fLeft, fRight);
			return fRoot;

		} catch (final CoreException ex) {
			throw new InvocationTargetException(ex);
		} finally {
			pm.done();
		}
	}

	@Override
	public String getToolTipText() {
		if ((fLeftResource != null) && (fRightResource != null)) {
			final String leftLabel = fLeftResource.getFullPath().makeRelative().toString();
			final String rightLabel = fRightResource.getFullPath().makeRelative().toString();
			if (fThreeWay) {
				final String format = Utilities.getString("ResourceCompare.threeWay.tooltip"); //$NON-NLS-1$
				final String ancestorLabel = fAncestorResource.getFullPath().makeRelative().toString();
				return MessageFormat.format(format, new String[] { ancestorLabel, leftLabel, rightLabel });
			}
			final String format = Utilities.getString("ResourceCompare.twoWay.tooltip"); //$NON-NLS-1$
			return MessageFormat.format(format, new String[] { leftLabel, rightLabel });
		}
		// fall back
		return super.getToolTipText();
	}

	private String buildLabel(IResource r) {
		// for a linked resource in a hidden project use its local file system
		// location
		if (r.isLinked() && r.getProject().isHidden()) {
			return r.getLocation().toString();
		}
		final String n = r.getFullPath().toString();
		if (n.charAt(0) == IPath.SEPARATOR) {
			return n.substring(1);
		}
		return n;
	}

	@Override
	public void saveChanges(IProgressMonitor pm) throws CoreException {
		super.saveChanges(pm);
		if (fRoot instanceof DiffNode) {
			try {
				commit(pm, (DiffNode) fRoot);
			} finally {
				if (fDiffViewer != null) {
					fDiffViewer.refresh();
				}
				setDirty(false);
			}
		}
	}

	/*
	 * Recursively walks the diff tree and commits all changes.
	 */
	private static void commit(IProgressMonitor pm, DiffNode node) throws CoreException {

		if (node instanceof MyDiffNode) {
			((MyDiffNode) node).clearDirty();
		}

		final ITypedElement left = node.getLeft();
		if (left instanceof BufferedResourceNode) {
			((BufferedResourceNode) left).commit(pm);
		}

		final ITypedElement right = node.getRight();
		if (right instanceof BufferedResourceNode) {
			((BufferedResourceNode) right).commit(pm);
		}

		final IDiffElement[] children = node.getChildren();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				final IDiffElement element = children[i];
				if (element instanceof DiffNode) {
					commit(pm, (DiffNode) element);
				}
			}
		}
	}

	private static String normalizeCase(String s) {
		if (NORMALIZE_CASE && (s != null)) {
			return s.toUpperCase();
		}
		return s;
	}

	@Override
	public boolean canRunAsJob() {
		return true;
	}

}
