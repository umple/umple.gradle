# umple.gradle.plugin
  To use, you must set the relevant project properties:
  - umpleFileName
  - languageToGenerate
  - outputPath (optional)
  
  example: "project.ext.set("umpleFileName", "test.ump")"
  
  The Umple compiler jar must also be present and referenced as a buildscript dependency.
  
  example: "classpath files('<dir>/umple.jar')"
