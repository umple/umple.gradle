# umple.gradle.plugin

**Tasks:**
  - generateSource - compileGeneratedSourceJava

**Instructions:**
The plugin relies on three key configuration values:
  - umpleFilePath
  - languageToGenerate
  - outputPath

These values can be set in the build.gradle file of the project you're applying the plugin to. If any of the fields are not set, the plugin will use the following default values:

umpleFilePath = 'src/umple/master.ump'
languageToGenerate = 'Java' 
outputPath = 'generated/java'

To set the plugin properties manually, you attach them to a `SourceSet` called `generatedSource`. You can declare the source set and set the plugin properties by including the following snippet in your project's `build.gradle` file (making sure you modify the provided paths to suit your needs):
```
apply plugin: 'java'
sourceSets {
    generatedSource { 
       ext.languageToGenerate = 'Java' 
       ext.outputPath = 'path/to/generated/java'
       ext.umpleFilePath = 'path/to/master.ump'
    }
}
```
Note that the `generatedSource` source set *must* be included in the build file of the project you're applying the plugin to, regardless of whether you override the default configuration values or not. The generated source files that the plugin produces will be automatically associated with the `generatedSource` source set, which enables easy compilation of generated source files via the `compileGeneratedSourceJava` task, so long as the generated files are Java (simply run the compileGeneratedSourceJava task). 

The [Umple Compiler Jar](https://github.com/umple/Umple/releases/latest/) must also be present on your local machine and referenced as a buildscript dependency in the `build.gradle` file of the project that is applying the gradle plugin. You can use the snippet below as a template:

**Buildscript**
```
buildscript {
    repositories {
        maven {
      	  url "https://plugins.gradle.org/m2/"
    	}
        maven {
          url uri('libs')
        }
    }
    dependencies {
		classpath files('libs/umple-latest.jar')
		classpath group: 'cruise.umple', name: 'UmpleGradlePlugin',  version: '0.1.3'
    }
}
apply plugin: 'umple.gradle.plugin'
```

Here, the `libs` folder is relative to your project's root folder (i.e. the folder that contains your project's `build.gradle` file).  

**Recommendations**
To keep the Umple Jar up-to-date automatically through Gradle, I recommend the download plugin:
```
apply plugin: 'de.undercouch.download'

import de.undercouch.gradle.tasks.download.Download

// downloads the umple jar only if the online version is newer
task downloadUmpleJar(type: de.undercouch.gradle.tasks.download.Download) {
    onlyIfNewer true
    overwrite true
    src 'http://cruise.eecs.uottawa.ca/umpleonline/scripts/umple.jar'
    dest 'libs/umple-latest.jar'

}

generateSource.dependsOn('downloadUmpleJar')
```

You'll also need to add 'classpath `de.undercouch:gradle-download-task:3.1.2'` to your `buildscript` dependencies closure if you want to use the download task. 

You now have everything necessary to use the Umple Gradle Plugin. Run the `generateSource` task to generate source files from `.ump` files, and `compileGeneratedSourceJava` to compile the generated source if the generated files are Java.