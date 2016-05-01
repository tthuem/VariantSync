package de.ovgu.variantsync.applicationlayer.merging;
/*
import java.io.File;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.domain.ICompareEditingDomain;
import org.eclipse.emf.compare.domain.impl.EMFCompareEditingDomain;
import org.eclipse.emf.compare.ide.ui.internal.configuration.EMFCompareConfiguration;
import org.eclipse.emf.compare.ide.ui.internal.editor.ComparisonEditorInput;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

public class Test2 {

	public static void main(String[] args) {
		ResourceSet resourceSet1 = new ResourceSetImpl();
		ResourceSet resourceSet2 = new ResourceSetImpl();
		ResourceSet resourceSet3 = new ResourceSetImpl();
		String xmi1 = "C:\\Users\\pfofe\\Desktop\\Bag.java";
		String xmi2 = "C:\\Users\\pfofe\\Desktop\\Bag2.java";
		String xmi3 = "C:\\Users\\pfofe\\Desktop\\Bag3.java";
		load(xmi1, resourceSet1);
		load(xmi2, resourceSet2);
		load(xmi3, resourceSet3);
		TreeIterator<Notifier> it = resourceSet1.getAllContents();
		while (it.hasNext()) {
			Notifier notifier = it.next();
			System.out.println(notifier.toString());
		}
	}

	private static void load(String absolutePath, ResourceSet resourceSet) {
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("java", new XMLResourceFactoryImpl());
		Resource resource = null;
		File f = new File(absolutePath);
		URI uri = URI.createFileURI(f.getAbsolutePath());
		if (!f.exists()) {
			throw new Exception(absolutePath + " does not exist");

		} else {
			resource = resourceSet.getResource(uri, true);
			mapPrepConfiguration = (MapPrepConfiguration) resource
					.getContents().get(0);
		}

		// Resource will be loaded within the resource set
		resourceSet.getResource(uri, true);
	}

	public static void compare(Notifier left, Notifier right, Notifier ancestor) {
		EMFCompare comparator = EMFCompare.builder().build();
		Comparison comparison = comparator.compare(EMFCompare
				.createDefaultScope(left, right, ancestor));

		ICompareEditingDomain editingDomain = EMFCompareEditingDomain.create(
				left, right, ancestor);
		AdapterFactory adapterFactory = new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		CompareEditorInput input = new ComparisonEditorInput(
				(EMFCompareConfiguration) new CompareConfiguration(),
				comparison, editingDomain, adapterFactory);

		CompareUI.openCompareDialog(input); // or
											// CompareUI.openCompareEditor(input);
	}
}
*/