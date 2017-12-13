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
    	
    	//TODO: Create .variantHistory file

        // configure the project...
    }

    @Override
    public void deconfigure() throws CoreException {
        // only called once the nature has been set
    	
    	//This should !not! delete the .variantHistory file

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
