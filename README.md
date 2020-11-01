# Map Reduce on Google Cloud Computing Platform 

### Design Details

#### Overview

The Application contains following processes which are created as instances -

● UserProgram -> trigger

● Keyvalues store

● mapReduce

● Worker -> executes mappers and reducers


The start of program is through the UserProgram Trigger, which includes

● init-cluster, launching key value store and map-reduce master

● call map reduce - run_mapreduce

● Delete instances on completion

● Once the VM instances are launched, connection to the keyvaluestore and master is
established.

The input file is uploaded to gcloud storage, run-mapreduce is called, wherein all the information
like mappers, reducers, kv ip address port is passed through gRPC connection from
UserProgram to master node.
The master node establishes a connection to the keyvaluestore to read the input file store on
gcloud and split the file based on the number of mappers. These files are stored in
keyvaluestore, intermediate files are generated in local and later stored to key value store.
Master spawns Workers, passing the jar the worker will execute and the chunk id it will work on.
Output created is written by worker to the keyvaluestore (This works in the same fashion for
both mapper and reducers)
Taking suggestion from the previous assignment, dependency from intermediate files is
eliminated.

Certain changes are implemented to the methods taken fron
https://github.com/GoogleCloudPlatform/java-docs-samples, as it was noticed deletion in this
case need not be a blocking process, making the application a bit faster.


### Configuration Details

The user configuration is present is UserProgram -> resources -> configuration.properties,
change jars to
mapper.jar = invertedindexmapper-0.0.1-SNAPSHOT-jar-with-dependencies.jar

reducer.jar = invertedindexreducer-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Custom image with dependencies -> custom-image is created so that each instance does not
have to individually download dependencies, which was noted to be time consuming.
Start-up scripts passed through metadata while creating an instance is used to run jars on VMs.

The start-up scripts are initially uploaded to cloud storage before instance creation through code
itself.

Startup scripts and the input file, assuming the input file might be huge, are the only files
uploaded to cloud storage.


### Virtual Machines on Google Cloud

All the instances created are created from a custom-image created from an instance of the
following configuration. Additionally, the custom-image has required dependencies as well.

● Machine type n1-standard-1 (1 vCPU, 3.75 GB memory)

● Zone us-central1-a

● Firewalls Rules - Allow HTTP traffic & Allow HTTPS traffic

● CPU platform Intel Haswell

● Image - Ubuntu-1804-bionic-v20201014

● Size 10 GB

● Allow full access to all Cloud API

### Performance Details

The end-to-end program execution takes about 8 minutes, when tested for 2 mappers and 2
reducers. Most of the time spent is to bring up the instance, hence my own custom image ->
custom-image was created.

### Future Scope

Improvements to the current design would make the system completely fault tolerant. Since,
right now the keyvaluestore is a single point of failure, have a distributed keyvaluestore.
Further, I would like to decouple the worker, mapper and reducers more.
The submitted files contain-cloud logs obtained from GUI as well as syslog obtained from ssh
into the VM.

### How to run code

```
sh user-program-startup.sh
```
