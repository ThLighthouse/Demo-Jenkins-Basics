I installed and configured Jenkins on AWS EC2 as a docker container. Then opened Jenkins UI in my browser.

Then we installed build tools to the Jenkins, like npm via docker cli and maven via Jenkins plugins. Created a freestyle job and configured there needed commands to be exectued in the job.


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