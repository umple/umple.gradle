# umple.gradle.plugin

**Tasks:**
  - generateSource

**Instructions:**
To use the plugin, you set the relevant plugin properties:
  - umpleFilePath
  - languageToGenerate
  - outputPath

These values are set in the build.gradle file of the project you're applyung the plugin to. If any required fields are not set, the plugin will use the following default values:

languageToGenerate = 'Java' 
outputPath = 'generated/java'
umpleFilePath = 'src/umple/master.ump'

The plugin properties are attached to a SourceSet called generatedSource. You can declare the source set and set the plugin properties on the source set by including the following snippet in your project's build.gradle file:
  
```
sourceSets {
    generatedSource { 
       ext.languageToGenerate = 'Java' 
       ext.outputPath = 'path/to/generated/java'
       ext.umpleFilePath = 'path/to/master.ump'
    }
}
```
Make sure you modify the paths to suit your needs.

The [Umple Compiler Jar](https://github.com/umple/Umple/releases/latest/) must also be present on your local machine and referenced as a buildscript dependency in the `build.gradle` file of the project that is applying the gradle plugin.

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
		classpath 'de.undercouch:gradle-download-task:3.1.2'
    }
}
```

**Recommendations**
To download the Umple Jar automatically through Gradle, I recommend the following plugin/script.
```
apply plugin: 'umple.gradle.plugin'
apply plugin: 'de.undercouch.download'

import de.undercouch.gradle.tasks.download.Download

// downloads the umple jar only if the online version is newer
download {
	onlyIfNewer = true
	overwrite = true
    src 'http://cruise.eecs.uottawa.ca/umpleonline/scripts/umple.jar'
    dest 'libs/umple-latest.jar'
}
```

The above buildscript and task should be everything you need. Be sure to set the path from '../../libs/' to something more meaningful.
