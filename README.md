# VariantSync ![Travis](https://travis-ci.org/Kogoro/VariantSync.svg?branch=master)
=== Automating the Synchronization of Software Variants ===

VariantSync is a tool to develop variants separately as in clone-and-own and to automate change propagation by using domain knowledge of developers. VariantSync detects and logs changes during development, tags these changes to feature expressions and automates the synchronization
of changes between variants. As a side effect, using VariantSync for a while may increase the feature-to-code map-ping and, thus, ease a potential later migration to a product line. VariantSync is an open-source Eclipse plug-in distributed under L-GPL.

## System Requirements
* JDK 1.8 or higher
* Eclipse IDE with the following plug-ins:
  * Plug-in Development Environment (PDE - minimum version is Luna)
  * Feature IDE (version 3.1.0 required)
	* Eclipse Update-Site for version v3.x: http://featureide.cs.ovgu.de/update/v3/
	* see http://wwwiti.cs.uni-magdeburg.de/iti_db/research/featureide for more information about FeatureIDE
	
## Installation
* Import the VariantSync as existing project in an eclipse workspace/clone VariantSync as git project in an eclipse workspace
* Run MANIFEST.MF in folder META-INF as eclipse application with the following run configuration:
  * Program arguments:
    * -os ${target.os} -ws ${target.ws} -arch ${target.arch} -nl ${target.nl} -consoleLog
  * VM arguments (minimum): 
    * -Dosgi.requiredJavaVersion=1.7 -Xms40m -Xmx512m

## First Steps
1. Create a FeatureIDE-Project with the VariantSync composer
2. Create a feature model for your variants (this feature model describes the domain for the variants)
3. Create a feature configuration in your FeatureIDE project for each variant - the configuration file needs to have the same name as the variant
4. Import your variants (if they are not already in the workspace)
5. Right-Click on each project that is a variant and choose VariantSyncTool -> Add VariantSync Support in the context menu. Alternativly you can create projects for each variant by clicking VariantSync -> Create Variant project in the configuration context menu
6. now, VariantSync is ready to support variant development


## Hints
* Do not use eclipse code formatting function (CTRL + SHIFT + F) wiwhen using line diffs as patch technique.
