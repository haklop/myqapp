#myqapp

## Install on dev mode

- Start mongodb on port 27017
- Use of port 8080 is mandatory. 8080 is used by the Google Authentication callback
- Insert your user on mongodb (install script is upcoming)
- mvn jetty:run

**Always commit on branch dev**

## Release and deploy

### Config

Use [maven-jgitflow-plugin](https://bitbucket.org/atlassian/maven-jgitflow-plugin/wiki/Home)

You may also need to provide a plugin group in your /.m2/settings.xml to be able to use the short name of the plugin on the command line.

To do so, simply add the following to your settings.xml:
```xml
<pluginGroups>
  <pluginGroup>com.atlassian.maven.plugins</pluginGroup>
</pluginGroups>
```

### Workflow

Create the release branch and set the release version. Release-start creates a release branch and sets the pom version.

	mvn jgitflow:release-start

Finish the release and set the next snapshot version. Release-finish creates the release tag, merges the release branch into master, deletes the release branch and push everything to the scm.

	mvn jgitflow:release-finish
	
When something is pushed to master, the application is automatically deployed on the server.
