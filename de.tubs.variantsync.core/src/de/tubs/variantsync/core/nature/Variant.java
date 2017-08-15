package de.tubs.variantsync.core.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class Variant implements IProjectNature {

	public static final String NATURE_ID = "de.tubs.variantsync.core.variant";
	private IProject project;

    @Override
    public void configure() throws CoreException {
        // only called once the nature has been set

        // configure the project...
    }

    @Override
    public void deconfigure() throws CoreException {
        // only called once the nature has been set

        // reset the project configuration...
    }

    @Override
    public IProject getProject() {
        return project;
    }

    @Override
    public void setProject(IProject project) {
        this.project = project;
    }

}
