:: this script starts both the viewer and the ocd service
:: requires the ocd service jar in the service directory

%~d0
cd %~p0
cd ..
java -cp "lib/*" i5.las2peer.tools.L2pNodeLauncher windows_shell -s 9011 - uploadStartupDirectory('etc/startup') startService('i5.las2peer.services.ocd.ServiceClass','ocdPass') startService('i5.las2peer.services.ocdViewer.ServiceClass','ocdViewerPass') startWebConnector interactive
pause
