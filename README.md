# SmartFridgeRPI

Application wich gets installed on the Raspberry PI of the Smartfridge.

The functionalities are:
-takes track of sensorechanges (door was opened)
-takes a picture of the inside of the fridge when the door was opened
-sends the picture to the webserver
-listens to the barcodescanner
-gets the productinformation of a barcode from the internet
-sends the gathered productinformation via push notification the the andorid application

# Installation

Java hast to be installed
Java -version
apt-get install oracle-java7-jdk


In order to ensure the access of the server application to the GPIO pins, WiringPI must be installed. You must first install Git: 
sudo apt-get install git-core

Now the git repository of WiringPi can be cloned: 
git clone git://git.drogon.net/wiringPi

Afterwards, WiringPI has to be installed, change it with the help of the terminal into the folder into which WiringPi was cloned and start the installation: 
cd wiringPi ./build

To ensure that the connected camera is detected, the fswebcam software package must be installed: 
sudo apt-get install fswebcam

To create the Database, MySql must be installed: 
sudo apt-get install mysql-server

Before the database image can be imported, a database with the name fridgedb_rpi must be created and selected: 
mysql -u root -p 
Enter password: 
mysql> CREATE DATABASE fridgedb_rpi mysql> USE fridgedb_rpi

Now the database image can be imported: 
$ mysql -u root -p fridgedb_rpi < fridgedb_rpi.sql

In order for the server application to run automatically after a restart of the Raspberry, a file with the name startup.desktop must be created in the / etc / xdg / autostart directory. 

Make sure the FridgeServer.jar is on the desktop, or adjust the path below.
Enter the following code lines into the file: 
[Desktop Entry] Type=Application Name=ServerApplication Exec = gksu „sudo java – jar /home/pi/Desktop/FridgeServer.jar“

If gksu has never been executed on the RPI, call the following command once in the terminal: 
gksu „sudo java – jar /home/pi/Desktop/FridgeServer.jar“

Gksu will now ask you for root access, please confirm this.
