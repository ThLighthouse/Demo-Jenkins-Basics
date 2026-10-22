# Build Automation & CI/CD with Jenkins

A hands-on demo project completed during the **Build Automation & CI/CD with Jenkins** module of the TechWorld with Nana DevOps Bootcamp.

The project started with a simple Java Maven application and gradually became a small CI setup with Jenkins. During the module I configured Jenkins, connected it to GitHub, built and tested the application with Maven, built Docker images, pushed them to Docker Hub and Nexus, worked with Pipeline and Multibranch Pipeline jobs, added a Jenkins Shared Library, configured webhooks and finished with dynamic application versioning.

> The original Java Maven application and starter code were provided as part of the TechWorld with Nana DevOps Bootcamp. This README documents my own implementation, configuration and troubleshooting while following the course.

## Technologies used

- Jenkins
- Docker
- Git and GitHub
- GitLab
- Maven
- Java
- Groovy
- Nexus Repository
- Docker Hub
- Linux
- AWS EC2

## Jenkins setup

I ran Jenkins as a Docker container on an AWS EC2 instance.

The Jenkins home directory was stored in a Docker volume, so Jenkins configuration and jobs would not disappear if the container was recreated.

I also mounted the Docker socket from the EC2 host into the Jenkins container because later Jenkins needed to build Docker images:

```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts
```

Maven and the Jenkins plugins needed during the module were configured from the Jenkins UI.

One issue I faced here was Docker socket permissions. Docker CLI was available inside the Jenkins container, but the Jenkins user initially could not communicate with `/var/run/docker.sock`. I fixed it by giving the Jenkins user access through the Docker socket group instead of trying to use `sudo` from Jenkins jobs.

## Freestyle jobs

I started with Freestyle Jobs to understand the basic Jenkins workflow before moving to Jenkinsfile pipelines.

The first job was used to check that build tools such as npm and Maven were available. After that I connected Jenkins to this GitHub repository through **Source Code Management -> Git** and added GitHub credentials in Jenkins.

I also added a small `freestyle-build.sh` script to the repository and executed it from the Jenkins job.

## Building the Java Maven application

The next job built the actual Java application.

```bash
mvn clean package
```

Maven compiled the project, ran the tests and created the JAR file inside the `target/` directory.

During the demo the test build completed successfully with one test and no failures.

## Docker in Jenkins

After the Maven build was working, I made Docker available to Jenkins.

The Docker CLI runs inside the Jenkins container, while the mounted Docker socket lets it communicate with the Docker daemon on the EC2 host.

This allowed Jenkins to build the application image:

```bash
docker build -t thlighthouse/demo-app:<tag> .
```

The Dockerfile in this repository packages the Maven-generated JAR into a Java runtime image.

## Push image to Docker Hub

I created a private Docker Hub repository and stored the Docker credentials in Jenkins instead of putting them directly into the job.

The login step uses `--password-stdin`:

```bash
echo "$PASSWORD" | docker login -u "$USER" --password-stdin
docker push thlighthouse/demo-app:<tag>
```

After this step Jenkins could build the application image and publish it to Docker Hub automatically.

## Push image to Nexus

I also repeated the same flow with a private Nexus Docker repository.

Nexus was running as a Docker container on a separate AWS EC2 instance. Port `8081` was used for the Nexus UI and port `8083` for the Docker hosted repository.

For this lab the Docker daemon was configured to use the Nexus HTTP registry:

```json
{
  "insecure-registries": ["<NEXUS_HOST>:8083"]
}
```

Jenkins credentials were then used to authenticate and push the image:

```bash
echo "$PASSWORD" | docker login \
  -u "$USERNAME" \
  --password-stdin \
  <NEXUS_HOST>:8083

docker push <NEXUS_HOST>:8083/java-maven-app:<tag>
```

I had a useful troubleshooting case here. Jenkins was timing out when trying to reach the Nexus Docker repository. The reason was that port `8083` was not exposed from the Nexus container and it was also missing from the AWS Security Group. After fixing both, Docker login and push worked.

## Pipeline Job

After the Freestyle Jobs I moved the build logic into a `Jenkinsfile`.

The Pipeline job was configured with **Pipeline script from SCM**, so Jenkins reads the pipeline directly from the repository.

The pipeline automated the same steps I had first configured manually: build the Java application, build the Docker image, authenticate to the registry and push the image.

This was my first step from UI-based Jenkins configuration to Pipeline as Code.

## Multibranch Pipeline

I then created a Multibranch Pipeline for this repository.

Jenkins scans the repository and creates a separate pipeline job for branches that contain a `Jenkinsfile`.

This was useful during the module because I kept different Jenkins examples in separate branches and could test them independently.

## Jenkins Shared Library

For the Shared Library part of the module I created a separate GitLab repository:

https://gitlab.com/alisher.khusainov/jenkins-shared-library

The library contains reusable Groovy functions for common pipeline steps such as building the JAR, building a Docker image, Docker login and Docker push.

The `jenkins-shared-lib` branch in this repository shows how I connected that Shared Library to the application pipeline.

## GitHub webhooks

I configured Jenkins jobs to start automatically after a push to GitHub.

For the regular Pipeline job I used the GitHub webhook endpoint:

```text
<JENKINS_URL>/github-webhook/
```

The Jenkins job uses **GitHub hook trigger for GITScm polling**.

For the Multibranch Pipeline I installed **Multibranch Scan Webhook Trigger** and used:

```text
<JENKINS_URL>/multibranch-webhook-trigger/invoke?token=<TOKEN>
```

After a push, Jenkins scans the repository, detects the changed branch and starts the corresponding branch pipeline.

This part made the CI process feel much more complete because I no longer had to press **Build Now** after every change.

## Dynamic application versioning

The last demo of the module was dynamic versioning. I kept this implementation in the `jenkins-jobs` branch.

The pipeline increments the Maven patch version with:

```bash
mvn build-helper:parse-version versions:set \
  -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} \
  versions:commit
```

Jenkins then reads the new version from `pom.xml` and combines it with the Jenkins build number.

For example, application version `1.1.9` and Jenkins build number `3` produce the image tag `1.1.9-3`.

That tag is used for the Docker image that gets pushed to Docker Hub.

## Commit the updated version back to Git

At first the Maven version was incremented only inside the Jenkins workspace. The next step was to save the new version back in Git.

Jenkins uses its own Git identity:

```bash
git config user.email "jenkins@example.com"
git config user.name "Jenkins"
```

Then the pipeline commits the updated `pom.xml` and pushes it back to the `jenkins-jobs` branch:

```bash
git add pom.xml
git commit -m "ci: increment version"
git push origin HEAD:jenkins-jobs
```

For the push I used a GitHub Personal Access Token stored in Jenkins Credentials.

## Preventing a Jenkins commit loop

Committing the version back to Git introduced another problem: the Jenkins commit also triggered the GitHub webhook.

Without extra configuration this could create a loop where Jenkins keeps starting a new build, incrementing the version and pushing another commit.

To prevent that, Jenkins commits use `jenkins@example.com` as the author email. I installed **Ignore Committer Strategy** and configured the Multibranch Pipeline to ignore commits from that author.

Normal developer commits still trigger the pipeline, while the version commit created by Jenkins does not start another build.

## About the branches

I used several branches during the module because each one represents a different stage of the Jenkins demo.

`jenkins-practice` was my main working branch. Most of the Jenkins setup, Maven and Docker integration, Pipeline practice, webhooks and README work were done there.

`jenkins-jobs` contains the dynamic versioning implementation, including automatic patch version increments, Docker tags with the Jenkins build number, committing the new version back to Git and preventing the CI loop.

`jenkins-shared-lib` contains the application pipeline integrated with the separate Jenkins Shared Library repository.

After the final review, the cleaned project from `jenkins-practice` is intended to be merged into `main`, while the other branches remain as examples of the specific demos.

## Main things I learned

This module gave me hands-on practice with Jenkins installation and configuration, Freestyle Jobs, Pipeline as Code, Multibranch Pipelines, credentials management, Maven builds, Docker integration, Docker Hub and Nexus registries, Jenkins Shared Libraries, GitHub webhooks, dynamic versioning and automated Git commits.

The most valuable part was troubleshooting the connections between all of these tools. Docker socket permissions, registry authentication, AWS ports, GitHub credentials and webhook behavior helped me understand much better how the pieces of a CI workflow actually communicate with each other.

This project also showed me the progression from running a few commands manually in Jenkins to keeping the CI process in code and triggering it automatically from Git.