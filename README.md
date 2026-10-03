###  Install Jenkins on the AWS EC2 and configure plugins (DEMO #1)

Actually there are two ways to install Jenkins on a Server: 

1) Install Jenkins directly on OS:
    - Download package and install on server
    - Create separate Jenkins User on server
    - Download binaries, etc

2) Run Jenkins as Docker container
    - No need to download and configure users, tools
    - All needed staff will be in container


I have installed Jenkins as a docker container on the AWS EC2(jenkins-server). Configured port `8080` and opened Jenkins UI. Hangaround with Jenkins UI and got familiar with build tools and plugins(maven, gradle) 

I have installed `Maven` plugin via Jenkins UI in the `Plugins` section.
Then I have installed nodejs plugin inside the container via `docker CLI`.

First of all I went inside the container itself as a root user to install all needed plugins.

`docker exec -u 0 -it <container_ID> bash`

After that I have downloaded the script that consists all the commands to install `nodejs` and `npm`.

`curl -sL https://deb.nodesource.com/setup_20.x -o nodesource_setup.sh`

```
root@c09429159235:/# node -v
v20.20.2
root@c09429159235:/# npm -v
10.8.2
root@c09429159235:/# 
```

Then I have installed to the jenkins-server very useful plugin such as `Stage View Plugin` via Jenkins UI.
The plugin show:
- the progress of each stage
- let's us see which stages pass or which stage broke the pipeline

---

### Jenkins Basics (DEMO #2)

Firstly, I have created `Freestyle Job` (my-job). This is the most basic type in Jenkins.
- Straightforward to set up and configure - suitable for simple, small-scale projects.
- Lack some advanced features provided by newer job types.

`Started by user alisher
Running as SYSTEM
Building in workspace /var/jenkins_home/workspace/my-job
[my-job] $ /bin/sh -xe /tmp/jenkins11636458972420802004.sh
+ npm --version
10.8.2
Unpacking 
https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.16/apache-maven-3.9.16-bin.zip to /var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven-3.9 on Jenkins
[my-job] $ /var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven-3.9/bin/mvn --version
Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
Maven home: /var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven-3.9
Java version: 21.0.11, vendor: Eclipse Adoptium, runtime: /opt/java/openjdk
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "7.0.0-1006-aws", arch: "amd64", family: "unix"
Finished: SUCCESS`

Then I configured the connection to my github repository.
my-job -> Configure -> Source Code Management -> Git

Docker im Jenkins 

In order to create automatically docker images from my application, I needed to allow access docker from jenkins.

I restarted jenkins container with related volumes and started to configure such things as 

`curl https://get.docker.com/ > dockerinstall && chmod 777 dockerinstall && ./dockerinstall` - fetch docker latest version and allow jenkins execute commands inside the container

docker.sock file is a Unix socket file, used by the Docker daemon to communicate with Docker client

`chmod 666 /var/run/docker.sock` 

As I have understood I gave to the jenkins user permission to rw inside the container where Jenkins running


I configured nexus private repository and configured neccessary files such as `/etc/docker/daemon.json` in the VM, where Jenkins is running in order to reach nexus docker-hosted repository.

