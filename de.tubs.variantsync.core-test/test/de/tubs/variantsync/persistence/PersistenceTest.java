package de.tubs.variantsync.persistence;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.tubs.variantsync.core.data.CodeMapping;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.data.FeatureExpression;
import de.tubs.variantsync.core.data.SourceFile;
import de.tubs.variantsync.core.patch.APatch;
import de.tubs.variantsync.core.patch.AMarkerInformation;
import de.tubs.variantsync.core.patch.base.DefaultDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.persistence.CodeMappingFormat;
import de.tubs.variantsync.core.persistence.ContextFormat;
import de.tubs.variantsync.core.persistence.FeatureExpressionFormat;
import de.tubs.variantsync.core.persistence.PatchFormat;

/**
 * Tests persistence properties
 * 
 * @author Christopher Sontag
 */
public class PersistenceTest {

	@Test
	public void testLoadFeatureExpressions() {
		List<FeatureExpression> featureExpressions = new ArrayList<>();
		FileHandler.load(Paths.get("assets/featureExpressions.xml"), featureExpressions, new FeatureExpressionFormat());
		assertTrue("Read FeatureExpressions", featureExpressions.size() > 0);
	}

	@Test
	public void testWriteFeatureExpressions() {
		List<FeatureExpression> featureExpressions = new ArrayList<>();
		featureExpressions.add(new FeatureExpression("Test"));
		featureExpressions.add(0, new FeatureExpression());
		featureExpressions.add(new FeatureExpression("Base", FeatureColor.Light_Gray));

		String path = "tmp_" + System.currentTimeMillis() + "_" + FeatureExpressionFormat.FILENAME;
		FileHandler.save(Paths.get(path), featureExpressions, new FeatureExpressionFormat());
		assertTrue("Write FeatureExpressions", new File(path).exists());
	}

	@Test
	public void testLoadContext() {
		Context context = new Context();
		FileHandler.load(Paths.get("assets/context.xml"), context, new ContextFormat());
		assertTrue("Read Context", context.getActualContext().equals("Test"));
	}

	@Test
	public void testWriteContext() {
		Context context = new Context();
		context.setActualContext("Test");

		String path = "tmp_" + System.currentTimeMillis() + "_" + ContextFormat.FILENAME;
		FileHandler.save(Paths.get(path), context, new ContextFormat());
		assertTrue("Write Context", new File(path).exists());
	}

//	@Test
	public void testLoadCodeMapping() {
		List<SourceFile> sourceFiles = new ArrayList<>();
		FileHandler.load(Paths.get("assets/mapping.xml"), sourceFiles, new CodeMappingFormat(null));
		assertTrue("Read Code Mapping", sourceFiles.size() > 0);
	}

	@Test
	public void testWriteCodeMapping() {
		List<SourceFile> sourceFiles = new ArrayList<>();
		SourceFile sourceFile = new SourceFile(null);
		sourceFile.addMapping(new CodeMapping("", new AMarkerInformation(1)));

		String path = "tmp_" + System.currentTimeMillis() + "_" + CodeMappingFormat.FILENAME;
		FileHandler.save(Paths.get(path), sourceFiles, new CodeMappingFormat(null));
		assertTrue("Write Code Mapping", new File(path).exists());
	}

//	@Test
	public void testLoadPatches() {
		List<IPatch<?>> patches = new ArrayList<>();
		FileHandler.load(Paths.get("assets/patches.xml"), patches, new PatchFormat(null));
		assertTrue("Read Patches", patches.size() > 0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Test
	public void testWritePatches() {
		List<IPatch<?>> patches = new ArrayList<>();
		IPatch patch = new APatch();
		patch.addDelta(new DefaultDelta(null, "de.tubs.variantsync.core.diff"));
		patches.add(patch);

		String path = "tmp_" + System.currentTimeMillis() + "_" + PatchFormat.FILENAME;
		FileHandler.save(Paths.get(path), patches, new PatchFormat(null));
		assertTrue("Write Patches", ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(path)).exists());
	}

}
