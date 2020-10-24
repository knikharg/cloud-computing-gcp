#! bin/sh 

java -version


sudo mkdir /kv
sudo chmod 777 /kv
cd kv/
rm -rf map-reduce-gcp
git clone https://github.com/knikharg/map-reduce-gcp.git
cd map-reduce-gcp/
git pull

kvPort=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/kvPort -H "Metadata-Flavor: Google")
echo kvPort -$kvPort

java -jar keyvaluestore-0.0.1-SNAPSHOT-jar-with-dependencies.jar $kvPort
