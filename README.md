# umple.gradle.plugin

**Tasks:**
  - compileUmpleFile

**Instructions:**
To use, you must set the relevant project properties:
  - umpleFileName (**required**)
  - languageToGenerate (**required**)
  - outputPath (**optional**)

These can be set either in a script, or through the command line. If any required fields are not set, an exception will be thrown.
  
**Script:**
```
project.ext.set("umpleFileName", "test.ump")
project.ext.set("languageToGenerate", "Java")
```

**Command Line**
```
gradle compileUmpleFile -PumpleFileName=test.ump -PlanguageToGenerate=Java
```

The [Umple Compiler Jar](https://github.com/umple/Umple/releases/latest/) must also be present and referenced as a buildscript dependency.

**Buildscript**
```
buildscript {
    repositories {
        maven {
            url uri('libs')
        }
    }
    dependencies {
		classpath files('libs/umple-latest.jar')
		classpath group: 'cruise.umple', name: 'UmpleGradlePlugin',  version: '0.1.0'
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
