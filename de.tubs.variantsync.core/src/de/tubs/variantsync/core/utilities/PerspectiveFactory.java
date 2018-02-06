package de.tubs.variantsync.core.utilities;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.ovgu.featureide.fm.ui.views.FeatureModelEditView;
import de.ovgu.featureide.fm.ui.wizards.NewFeatureModelWizard;
import de.ovgu.featureide.ui.statistics.ui.FeatureStatisticsView;
import de.ovgu.featureide.ui.views.configMap.ConfigurationMap;
import de.ovgu.featureide.ui.wizards.NewConfigurationFileWizard;
import de.ovgu.featureide.ui.wizards.NewFeatureProjectWizard;
import de.tubs.variantsync.core.VariantSyncPlugin;

/**
 * Provides the variantsync perspective settings
 * 
 * @author Christopher Sontag
 */
public class PerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = VariantSyncPlugin.PLUGIN_ID + ".perspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		final String editorArea = layout.getEditorArea();

		layout.addNewWizardShortcut(NewFeatureProjectWizard.ID);
		layout.addNewWizardShortcut(NewConfigurationFileWizard.ID);
		layout.addNewWizardShortcut(NewFeatureModelWizard.ID);
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");

		final IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.23, editorArea);
		final IFolderLayout down = layout.createFolder("down", IPageLayout.BOTTOM, (float) 0.80, editorArea);
		final IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, (float) 0.75, editorArea);

		down.addView(IPageLayout.ID_PROBLEM_VIEW);
		down.addView("org.eclipse.ui.console.ConsoleView");
		down.addView(ConfigurationMap.ID);
		down.addView(de.tubs.variantsync.core.view.resourcechanges.View.ID);
		down.addView(de.tubs.variantsync.core.view.sourcefocus.View.ID);
		down.addView(de.tubs.variantsync.core.view.targetfocus.View.ID);

		right.addView(IPageLayout.ID_OUTLINE);
		right.addView(de.tubs.variantsync.core.view.featurecontext.View.ID);

		left.addView("org.eclipse.ui.navigator.ProjectExplorer");

		layout.addShowViewShortcut(de.tubs.variantsync.core.view.resourcechanges.View.ID);
		layout.addShowViewShortcut(de.tubs.variantsync.core.view.sourcefocus.View.ID);
		layout.addShowViewShortcut(de.tubs.variantsync.core.view.targetfocus.View.ID);
		layout.addShowViewShortcut(FeatureStatisticsView.ID);
		layout.addShowViewShortcut(FeatureModelEditView.ID);
		layout.addShowViewShortcut(ConfigurationMap.ID);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);

	}

}
