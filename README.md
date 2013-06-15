#myqapp

## Release and deploy

### Config

Use [maven-jgitflow-plugin](https://bitbucket.org/atlassian/maven-jgitflow-plugin/wiki/Home)

You may also need to provide a plugin group in your /.m2/settings.xml to be able to use the short name of the plugin on the command line.

To do so, simply add the following to your settings.xml:

	<pluginGroups>
    	<pluginGroup>com.atlassian.maven.plugins</pluginGroup>
	</pluginGroups>

### Workflow

Create the release branch and set the release version. Release-start creates a release branch and sets the pom version.

	mvn jgitflow:release-start

Finish the release and set the next snapshot version. Release-finish creates the release tag, merges the release branch into master, deletes the release branch and push everything to the scm.

	mvn jgitflow:release-finish
	
When something is pushed to master, the application is automatically deployed on the server.

## Install on server

Required:

- java7+
- jetty8+

Configure [deploy.sh](https://github.com/haklop/myqapp/blob/master/deploy.sh):

	HOME=~
	WORKSPACE_MYQAPP=$HOME/infoq/myqapp
	WORKSPACE_WRAPPER=$HOME/infoq/trello-java-wrapper
	JETTY_HOME=$HOME/jetty

Execute deploy.sh
