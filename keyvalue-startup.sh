#! bin/sh 

java -version


sudo mkdir /kv
sudo chmod 777 /kv
cd kv/
rm -rf map-reduce-gcp
git clone https://github.com/knikharg/map-reduce-gcp.git
cd map-reduce-gcp/
git pull
java -jar keyvaluestore-0.0.1-SNAPSHOT-jar-with-dependencies.jar
