#!/bin/bash

# this script starts a las2peer node providing the viewer service
# and the general ocd (overlapping community detection) service.
# make sure to put the ocd service jar in the service directory of this project as well.
# pls execute it from the root folder of your deployment, e. g. ./bin/start_network.sh

java -cp "lib/*" i5.las2peer.tools.L2pNodeLauncher -s 9011 - uploadStartupDirectory\(\'etc/startup\'\) startService\(\'i5.las2peer.services.ocd.ServiceClass\',\'ocdPass\'\) startService\(\'i5.las2peer.services.ocdViewer.ServiceClass\',\'ocdViewerPass\'\) startWebConnector interactive
