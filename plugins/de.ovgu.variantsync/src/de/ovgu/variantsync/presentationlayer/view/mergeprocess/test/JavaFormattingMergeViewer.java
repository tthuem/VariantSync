package de.ovgu.variantsync.presentationlayer.view.mergeprocess.test;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.jdt.internal.ui.compare.JavaMergeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class JavaFormattingMergeViewer extends JavaMergeViewer {


	private FormattedCompareInput theInput;
	private static final ImageDescriptor imageEnable = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW);
	private static final ImageDescriptor imageDisable = ImageDescriptor.createFromFile(JavaFormattingMergeViewer.class, "error_obj.gif");

	public JavaFormattingMergeViewer(final Composite parent, final int styles,
			final CompareConfiguration mp) {
		super(parent, styles, mp);
	}

	public void setInput(final Object input) {

		if (input instanceof ICompareInput) {
			final ICompareInput compareInput = (ICompareInput) input;
			this.theInput = new FormattedCompareInput(compareInput);
		}

		super.setInput(this.theInput);
	}

	protected void createToolItems(final ToolBarManager tbm) {
		super.createToolItems(tbm);
		final Action a = new Action() {
			public void run() {
				JavaFormattingMergeViewer.this.theInput.toggleFormattingIgnore();
				if (JavaFormattingMergeViewer.this.theInput.isFormattingIgnoreEnabled()) {
					setToolTipText("Disables Ignore java formatting feature. After clicking here you will see the original source.");
					setImageDescriptor(imageDisable);
				} else {
					setToolTipText("Enables Ignore java formatting feature. After clicking here you the same code formatter will be applied to both sides.");
					setImageDescriptor(imageEnable);
				}
										
				refresh();
			}
		};

		a.setText("AutoFormatting");
		a.setToolTipText("Enables Ignore java formatting feature. After clicking here you the same code formatter will be applied to both sides.");
		a.setImageDescriptor(imageEnable);

		final ActionContributionItem toggleFormatting = new ActionContributionItem(a);
		tbm.appendToGroup("navigation", toggleFormatting); //$NON-NLS-1$

	}

}
