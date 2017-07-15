# umple.gradle.plugin

***Environment Setup***

The [Umple compiler jar](https://github.com/umple/Umple/releases/latest/) must be present on your local machine and referenced as a buildscript dependency in the `build.gradle` file of the project that is applying the Umple Gradle plugin. You can use the snippet below as a starting point (or see the `Base project` directory of the `umple.gradle` repo for a more complete example):

```
buildscript {
    repositories {
        maven {
      	  url "https://plugins.gradle.org/m2/" // for the Umple Gradle Plugin
    	}
        maven {
          url uri('libs') // for the Umple compiler jar
        }
    }
    dependencies {
		classpath files('libs/umple-latest.jar')
		classpath group: 'cruise.umple', name: 'UmpleGradlePlugin',  version: '0.1.3'
    }
}
apply plugin: 'umple.gradle.plugin'
```

Here, the `libs` folder is relative to your project's root folder (i.e. the folder that contains your project's `build.gradle` file). You should double-check that `0.1.3` is the latest version of the plugin. If it's not, please update this readme!

To keep the Umple Jar up-to-date automatically through Gradle, I recommend the `download` plugin. Once the plugin has been applied in your `build.gradle` file, you can add a `downloadUmpleJar` task that will automatically download newer versions of the jar as they're released. We make the download task `dependOn` `generateSource` so that it runs every time the main Umple tasks run:
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
You'll need to add `classpath `de.undercouch:gradle-download-task:3.1.2'` to your `buildscript` dependencies closure if you want to use the download task.



***Usage Instructions***

*Tasks:*
  - compile{source set name}Umple

The plugin has four configuration properties that are used to customize the `compile{source set name}Umple` task.
  - `language`: The language(s) you want to compile your Umple files to. You can specify a single language or multiple languages.
  - `master`: The name of your master Umple file. The plugin looks for this file in the following location: `{project root directory}/src/{source set name}/umple. Multiple master files may be specified.
  - `customMasterPath`: If you want the plugin to look for a master file in a location that doesn't conform to the `{project root directory}/src/{source set name}/umple` convention, set this flag to true and provide a path to the master file starting from the project root directory of the project that contains the `master` file you want to use
  - `compileGenerated`: A flag that tells the plugin whether or not you want the generated source files to be compiled. Currently, the plugin only supports compiling Java files.
  - `outputDir`: The location you want the generated source files outputted to. This is relative to your project's root directory. The plugin will automatically look in this 
  location if you tell it to compile generated files.
    
These values are set in the `build.gradle` file of the project you're applying the plugin to. If any required fields are not set, the plugin will fall back on the following default values:

`language` = ['Java']
`outputDir` = 'generated'
`master` = ['Master.ump']
`compileGenerated` = true
`customMasterFlag` = false

You can also specify global default values that apply to all `SourceSets` present in your build. Global defaults override the system defaults described above, but will not 
override any SourceSet specific configuration values you provide. For example, consider a project containing a source set named `MyProject`. If you set the `outputDir` configuration value to `/src/custom/path/generated/`, this value takes precedence over any global or system default values that may be specified elsewhere. That is, even if you specify a global default value for `outputDir`, the specific value provided for the `MyProject` source set will be used to alter the behaviour to the `compileMyProjectUmple` task. 

Here's what a `build.gradle` file based on our example above might look like:
  

```
apply plugin: 'java'
sourceSets {
    MyProject { 
        umple {
            language = 'Java'
            outputDir = file('src/custom/MyProject/')
            master = file('master.ump')
            compileGenerated = false
        }
    }
    GeneratedPhp {
        language = 'Php'
    }
}

umple {
      master = file('master_main.ump')
      outputDir = file('src/custom/path/generated')
 }   
```

We've specified three `SourceSet`-specific configuration options for the `MyProject` `SourceSet`. These configuration values have the highest priority, and therefore will be used by the plugin when `compileMyProjectUmple` is invoked. Notice that the `compileGenerated` flag is set to false, so we won't automatically compile the source files generated by the `compileMyProjectUmple` task. 

There's no limit to the number of source sets we can use, and in this example we've also included a `GeneratedPhp` `SourceSet`, for which we've provided a `language` value. To determine the values to use for the the other three configuration values, the Umple plugin first looks to the global defaults we've provided inside of the `umple` closure. It gets a value for `master` and `outputDir` from this source. However, we haven't specified a default configuration value for `compileGenerated` in our global defaults. Since neither `GeneratedPhp` nor the global defaults within the `umple` closure specify a value for `compileGenerated`, we fall back on the system default value, which, as mentioned earlier, is `true`.

For a more complete example, see the `Base project` directory in the Gradle plugin's Github repo (https://github.com/umple/umple.gradle)
