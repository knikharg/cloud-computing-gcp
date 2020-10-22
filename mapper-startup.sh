#! bin/sh 
#! bin/sh 
sudo apt-get -y update
sudo apt-get -y install default-jdk
sudo apt-get -y install maven
sudo apt-get -y install git
java -version


sudo mkdir /mapper
sudo chmod 777 /mapper
cd mapper/
rm -rf map-reduce-gcp
jar = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/jar -H "Metadata-Flavor: Google")
id = jar = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/id -H "Metadata-Flavor: Google")

echo $jar - $id

git clone https://github.com/knikharg/map-reduce-gcp.git
cd map-reduce-gcp/
git pull
java -jar $jar $id 
