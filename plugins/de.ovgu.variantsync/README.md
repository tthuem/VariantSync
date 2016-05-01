# VariantSync
=== automating the synchronization of software variants ===

VariantSync is an eclipse-plugin to support conventional development of a small number of software variants by introducing feature-orientation. The plugin not only considers single changes on a low code level, but also extends the granularity of changes by adapting features and feature-expressions which are known from the research area of software product-lines. VariantSync implements a strategy to synchronize features of software variants by combining low-level changes and assign them to features or feature-expressions. The goal is to synchronize features and feature-expressions between variants.

## System Requirements
* glpk 4.55 (in case of using a Windows OS: http://sourceforge.net/projects/winglpk/files/winglpk/GLPK-4.55/)
* Eclipse IDE with the following plug-ins:
  * Plug-in Development Environment (PDE - version luna recommended)
  * Feature IDE (version 2.7.4 recommended)

## Installation
* Download and extract glpk in a folder of your choice. If you do not use the precompiled winglpk, you probably need to compile and install your version of glpk.
* Import the VariantSync as existing project in an eclipse workspace/ clone VariantSync as git project in an eclipse workspace
* Run MANIFEST.MF in folder META-INF as eclipse application with the following run configuration:
  * Program arguments:
    * -os ${target.os} -ws ${target.ws} -arch ${target.arch} -nl ${target.nl} -consoleLog
  * VM arguments: 
    * -Dosgi.requiredJavaVersion=1.6 -Xms40m -Xmx512m
    * -Djava.library.path=ABSOLUTE_PATH_TO_FOLDER\winglpk-4.55\glpk-4.55\w64 (or \w32 for a 32-bit operating system)

## First Steps
1. Create a FeatureIDE-Project named variantsyncFeatureInfo
2. Create a feature model for variantsyncFeatureInfo (this feature model describes the domain for the variants)
3. Create a feature configuration in variantsyncFeatureInfo for each variant - the configuration file needs to have the same name as the variant
4. Import the variants (if they are not already in the workspace)
5. Right-Click on each project that is a variant and choose VariantSyncTool -> Add VariantSync Support in the context menu - now, VariantSync is ready to support development of the variants

## Workflow
* See VariantSync/Workflow.pdf for an instruction how to use this tool to synchronize variants.

## Hints
* Do not use eclipse code formatting function (CTRL + SHIFT + F).

## Known Misbehavior
* Color for code-highlighting can only be changed if the active context is stopped.
* At program start: If a class is opened in the editor after starting VariantSync, then existing annotations and code-highlighting for this class will sometimes disappear. To avoid misbehavior, open any other file in the code editor and navigate back to the first file. Then, annotations and code-highlighting are displayed correctly.
* After performing a synchronization, code in synchronized file is sometimes not correctly tagged.
* Synchronization of feature expressions is not yet possible.
* Automatic synchronization is only possible for java-files.
* Code tagging only monitors changes inside a file. It does not monitor adding or removing of whole files.

## Open Points
* Retaining existing code tagging of a file after synchronization. Actually, merging corrupts tagging of the merge target file. Only code is tagged that was inserted by the merge.
* Implementing the product-view to provide a variant-centric synchronization.
* Computing synchronization targets for feature expressions. (Validating feature expressions for feature configurations)
* Extending automatic synchronization for other programming languages than java. VariantSync is designed to be language-independent, but the syntactic merge is only implemented for java-files.
* Extending code tagging to monitor adding or removing of whole files. Actually, only changes inside a file are tagged.
