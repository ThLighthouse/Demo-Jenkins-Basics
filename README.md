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
## Showcase how to configure build tools and execute commands.

Firstly, I have created `Freestyle Job` (my-job). This is the most basic type in Jenkins.
- Straightforward to set up and configure - suitable for simple, small-scale projects.
- Lack some advanced features provided by newer job types.

Inside the `Freestyle Job` I have configured first jobs in `Build Steps` section and I have choosen `Exectue shell` option for `npm` commands, which allows to execute normal shell commands. 

```
Started by user alisher
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
Finished: SUCCESS
```
## Configure Git Repository

Then I configured the connection to my github repository.
my-job -> Configure -> Source Code Management -> Git

In order to allow Jenkins to work with my github repository, I have added my repo to the Repository URL section: `https://github.com/ThLighthouse/Demo-Jenkins-Basics.git` and configured its credentials to authenticate and clone git repo.

In order to Jenkins does some jobs in my repository I have added Shell script(freestyle-build.sh) which executes `--npm version` command.

```
Started by user alisher
Running as SYSTEM
Building in workspace /var/jenkins_home/workspace/my-job
The recommended git tool is: NONE
using credential github-credentials
 > git rev-parse --resolve-git-dir /var/jenkins_home/workspace/my-job/.git # timeout=10
Fetching changes from the remote Git repository
 > git config remote.origin.url 
https://github.com/ThLighthouse/Demo-Jenkins-Basics.git # timeout=10
Fetching upstream changes from https://github.com/ThLighthouse/Demo-Jenkins-Basics.git
 > git --version # timeout=10
 > git --version # 'git version 2.47.3'
using GIT_ASKPASS to set credentials 
 > git fetch --tags --force --progress -- https://github.com/ThLighthouse/Demo-Jenkins-Basics.git +refs/heads/*:refs/remotes/origin/* # timeout=10
 > git rev-parse refs/remotes/origin/jenkins-practice^{commit} # timeout=10
Checking out Revision 782424c1f7ea34271824083a51182350c3c74c54 (refs/remotes/origin/jenkins-practice)
 > git config core.sparsecheckout # timeout=10
 > git checkout -f 782424c1f7ea34271824083a51182350c3c74c54 # timeout=10
Commit message: "jenkins(demo): add freestyle-build script and add git configuration step to README.md"
First time build. Skipping changelog.
[my-job] $ /bin/sh -xe /tmp/jenkins4897465575917074776.sh
+ chmod +x freestyle-build.sh
+ ./freestyle-build.sh
10.8.2
[my-job] $ /var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven-3.9/bin/mvn --version
Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
Maven home: /var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven-3.9
Java version: 21.0.11, vendor: Eclipse Adoptium, runtime: /opt/java/openjdk
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "7.0.0-1012-aws", arch: "amd64", family: "unix"
Finished: SUCCESS
```

---

## Build actual Demo project
# Run tests and build Java Application

I have created a new `Freestyle Job`(java-maven-build). And here we actually run tests on java-maven app and build a jar file of that application.

In order to run test I have copied test file(AppTest.java) from `jenkins-job` branch and added it to my work branch `jenkins-practice`.
And run the configured build on the Jenkins.

```
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running AppTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.124 s -- in AppTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] 
[INFO] --- jar:3.5.0:jar (default-jar) @ java-maven-app ---
[INFO] Building jar: /var/jenkins_home/workspace/java-maven-build/target/java-maven-app-1.1.0-SNAPSHOT.jar
[INFO] 
[INFO] --- spring-boot:3.5.5:repackage (default) @ java-maven-app ---
[INFO] Replacing main artifact /var/jenkins_home/workspace/java-maven-build/target/java-maven-app-1.1.0-SNAPSHOT.jar with repackaged archive, adding nested dependencies in BOOT-INF/.
[INFO] The original artifact has been renamed to /var/jenkins_home/workspace/java-maven-build/target/java-maven-app-1.1.0-SNAPSHOT.jar.original
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  4.614 s
[INFO] Finished at: 2026-10-03T08:18:20Z
[INFO] ------------------------------------------------------------------------
Finished: SUCCESS
```

# Docker in Jenkins

In order to create the image from our application we need to make available docker commands inside our Jenkins container. The common way is to mount `Docker runtime directory` from our EC2 instance into the container as a volume. And this makes docker available inside the container.

Run the container and mount `docker runtime directory` into it:

```
docker run -p 8080:8080 -p 50000:50000 -d \
-v jenkins_home:/var/jenkins_home \
-v /var/run/docker.sock:/var/run/docker.sock jenkins/jenkins:lts
```

Inside the container:

`curl https://get.docker.com/ > dockerinstall && chmod 777 dockerinstall && ./dockerinstall` - fetch docker latest version and allow jenkins execute commands inside the container

`docker.sock` file is a Unix socket file, used by the Docker daemon to communicate with Docker client
`chmod 666 /var/run/docker.sock` - give everebodu rw permissions to execute docker commands. 
```
ls -l /var/run/docker.sock 
srw-rw-rw- 1 root docker-host 0 Oct  6 06:35 /var/run/docker.sock
``` 

# Building docker image stage

To build the `docker image` I have copied `Dockerfile` from `jenkins-job` branch.
Then I have configured `docker commands` inside the `Jenkins job`:

`docker build -t java-maven-app:1.0 .`

```
[INFO] Replacing main artifact /var/jenkins_home/workspace/java-maven-build/target/java-maven-app-1.1.0-SNAPSHOT.jar with repackaged archive, adding nested dependencies in BOOT-INF/.
[INFO] The original artifact has been renamed to /var/jenkins_home/workspace/java-maven-build/target/java-maven-app-1.1.0-SNAPSHOT.jar.original
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.673 s
[INFO] Finished at: 2026-10-06T07:17:37Z
[INFO] ------------------------------------------------------------------------
[java-maven-build] $ /bin/sh -xe /tmp/jenkins8780785760807523452.sh
+ docker build -t java-maven-app:1.0 .
```

The image: 

```
docker images
IMAGE                 ID             DISK USAGE   CONTENT SIZE   EXTRA
java-maven-app:1.0    91f4571d0ed5        545MB          196MB        
```

# Push Image to Docker Hub

We need to create credentials firstly to push images to our Private Repository.
I have created the an account the Dockerhub `https://hub.docker.com/u/thlighthouse` and configured the private repository there and added credentials to that repo `https://hub.docker.com/r/thlighthouse/demo-app`.

In order to push the image to our private repository we need to configure:
    - docker credentials
    - docker tag

To add my credentials for Private Docker repository into the Jenkins job I have configured `Use secret text(s) or file(s)` plugin with `Username and password(separated)` binding.

The way we push the Docker image to repository is to tag the Image with your DockerHub repository.

```
docker build -t thlighthouse/demo-app:jma-1.0 .
docker login -u $USERNAME -p $PASSWORD
docker push thlighthouse/demo-app:jma-1.0
```

# Push Image to Nexus Private Repository

In order to get access to the Nexus Private Repository we need to configure credentials at the `Jenkins-server`. We need to create such file as a `daemon.json`.

`cat /etc/docker/daemon.json`

```
{
	"insecure-registries":["3.73.121.15:8083"]
}
```

The next process is the same as with the `DockerHub Repo`. I have configured repository on the `Nexus`, configured credentials on the `Jenkins Job` and added credentials. Tag the image again with Nexus name: 

```
docker build -t 3.73.121.15:8083/java-maven-app:1.1 .
echo $PASSWORD | docker login -u $USERNAME --password-stdin 3.73.121.15:8083
docker push 3.73.121.15:8083/java-maven-app:1.1
```


```
Login Succeeded
+ docker push 3.73.121.15:8083/java-maven-app:1.1
The push refers to repository [3.73.121.15:8083/java-maven-app]
4f4fb700ef54: Waiting
e2de96513ba9: Waiting
0c470d9f3e7b: Waiting
6e8f492806ec: Waiting
356064565af6: Waiting
4f4fb700ef54: Waiting
4f4fb700ef54: Waiting
4f4fb700ef54: Waiting
4f4fb700ef54: Waiting
4f4fb700ef54: Waiting
356064565af6: Pushed
4f4fb700ef54: Layer already exists
e2de96513ba9: Pushed
6e8f492806ec: Pushed
0c470d9f3e7b: Pushed
1.1: digest: sha256:5ff58a42a82904098be7f0ad0d645baeaaa67757989befe8b4bf03e4c859ea11 size: 856
Finished: SUCCESS
```

# Pipeline Job

I have created a `pipeline job`. First thing then I have connected my git repository for this pipeline "Pipeline script from SCM": `https://github.com/ThLighthouse/Demo-Jenkins-Basics.git`.

