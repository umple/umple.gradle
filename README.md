# umple.gradle.plugin

**Tasks:**
  - compileUmpleFile

**Instructions:**
To use, you must set the relevant project properties:
  - umpleFileName
  - languageToGenerate
  - outputPath (optional)

This can either be done in a build script, or through the command line. Below are some examples.
  
**Script:**
```
project.ext.set("umpleFileName", "test.ump")
project.ext.set("languageToGenerate", "Java")
```

**Command Line**
```
gradle compileUmpleFile -PumpleFileName=test.ump -PlanguageToGenerate=Java
```

The Umple compiler jar must also be present and referenced as a buildscript dependency.
```
buildscript {
    repositories {
        maven {
            url uri('../../libs')
        }
    }
    dependencies {
		classpath files('../../libs/umple.jar')
		classpath group: 'cruise.umple', name: 'UmpleGradlePlugin',  version: '0.1.0'
    }
}
```
