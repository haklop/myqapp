#!/bin/sh

HOME=~
WORKSPACE_MYQAPP=$HOME/infoq/myqapp
WORKSPACE_WRAPPER=$HOME/infoq/trello-java-wrapper
JETTY_HOME=$HOME/jetty

# Get and install the last snapshot of trello-java-wrapper
# ---------------

echo "Retrieve latest version of trello-java-wrapper"

cd $WORKSPACE_WRAPPER
git pull

echo "Build trello-java-wrapper"

mvn clean install

# Build war
# ---------------

echo "Retrieve latest version of MyQApp"

cd $WORKSPACE_MYQAPP
git pull

echo "Build MyQApp"

mvn clean package



# Deploy war
# ---------------

# TODO: hot deploy
cd $JETTY_HOME

java -jar start.jar -DSTOP.PORT=9966 -DSTOP.KEY=myqappStop --stop

cp $WORKSPACE_MYQAPP/target/myqapp-*.war $JETTY_HOME/webapps/ROOT.war

java -jar start.jar jetty.port=2305  -Dspring.profiles.active="prod" -DSTOP.PORT=9966 -DSTOP.KEY=myqappStop --daemon &

