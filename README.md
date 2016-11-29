# umple.gradle.plugin

**Tasks:**
  - compileUmpleFile

**Instructions:**
To use, you must set the relevant project properties:
  - umpleFileName (Required)
  - languageToGenerate (Required)
  - outputPath (Optional)

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
            url uri('../../libs')
        }
    }
    dependencies {
		classpath files('../../libs/umple.jar')
		classpath group: 'cruise.umple', name: 'UmpleGradlePlugin',  version: '0.1.0'
    }
}
```
