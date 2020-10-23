
#! bin/sh 

java -version


sudo mkdir /master
sudo chmod 777 /master
cd /master
rm -rf map-reduce-gcp
git clone https://github.com/knikharg/map-reduce-gcp.git
cd map-reduce-gcp/
git pull
java -jar mapreduce-0.0.1-SNAPSHOT-jar-with-dependencies.jar


