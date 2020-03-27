# gradle-doctoolchain-plugin


[![Build Status](https://travis-ci.org/moley/gradle-doctoolchain-plugin.svg?branch=master)](https://travis-ci.org/moley/gradle-doctoolchain-plugin)


[![CodeCoverage](https://codecov.io/gh/moley/gradle-doctoolchain-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/moley/gradle-doctoolchain-plugin)


*gradle-doctoolchain-plugin* is a gradle plugin which helps you integrate [doctoolchain](https://github.com/docToolchain/docToolchain) into an existing build. 

# How to use it

Add the follow to your `build.gradle` file: 
```gradle
buildscript {
    repositories {
      maven { url "https://dl.bintray.com/markusoley/gradle-java-apidoc-plugin" }
    }
    dependencies {
      classpath 'gradle.plugin.com.github.moley:gradle-doctoolchain-plugin:0.1'
    }
}

apply {
    plugin 'doctoolchain'
}
```
 
When you call `gradlew tasks` afterwards you get a new chapter *Doctoolchain tasks*, where you 
find the following tasks: 

| Task        | Description |
| ------------- |:-------------:| 
| *generateHTML*        | Downloads the doctoolchain into folder 'doctoolchain' and delegates to task generateHTML of doctoolchain | 
| *generatePDF*         | Downloads the doctoolchain into folder 'doctoolchain' and delegates to task generatePDF of doctoolchain |
| *publishToConfluence* | Downloads the doctoolchain into folder 'doctoolchain' and delegates to task publishToConfluence of doctoolchain |


# Importing existing confluence documentation
If you want to use doctoolchain not on a new project but on a project with existing documentation in confluence 
you can configure *gradle-doctoolchain-plugin* to create tasks to import your content from confluence. 
Configure import like: 
```gradle
doctoolchain {
    importItem('91265015', 'https://myconfluence.company/', 'beginner')
    importItem(PAGE_ID_IN_CONFLUENCE, 'CONFLUENCE_URL', 'FOLDER_TO_SAVE_IT_TO')
}
``` 

Credentials for confluence installation can be configured with system properties: 
- confluence.user = User to be used to access the confluence rest api
- confluence.plainPassword = Password to be used to access the confluence rest api
These configurations can be done in *gradle.properties* file in your project or locally on your machine in file `~/.gradle/gradle.properties`

The import tasks need [pandoc](https://pandoc.org/) to be installed.

For every configured import item a gradle task is created. 
In example above calling `gradlew importFromConfluencebeginner` imports the documentation of the import item named with beginner.
* The page with the page id is downloaded via rest API
* This page is preprocessed (preprocessor from `org.gradle.doctoolchain.tasks.importConfluence.preprocessors`)
* The preprocessed page is migrated to adoc via pandoc