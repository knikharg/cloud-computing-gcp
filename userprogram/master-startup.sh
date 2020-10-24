
#! bin/sh 

java -version


sudo mkdir /master
sudo chmod 777 /master
cd /master
rm -rf map-reduce-gcp
git clone https://github.com/knikharg/map-reduce-gcp.git
cd map-reduce-gcp/
git pull

kvPort=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/kvPort -H "Metadata-Flavor: Google")
masterPort=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/masterPort -H "Metadata-Flavor: Google")

echo kvPort -$kvPort,masterPort-$masterPort

java -jar mapreduce-0.0.1-SNAPSHOT-jar-with-dependencies.jar $kvPort $masterPort


