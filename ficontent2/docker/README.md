Docker Usage
============

If you plan to test the FE with Docker, you should follow these steps:

1) 	Install Docker (https://docs.docker.com/installation/#installation)
	We have tested it with the Ubuntu installation (https://docs.docker.com/installation/ubuntulinux/)
	
2) 	Build Docker Image
	2.1 Copy Dockerfile, install.sh, start.sh and test.sh in a directory
	2.2 docker build -t <name:tag> <directory>
	
3) 	Push Docker Image to Repository (optional)
	3.1 Create account in https://hub.docker.com/
	3.2 docker push [name:tag]
	3.3 Enter login credentials when asked
	
4) Run Docker Image
	4.1 docker run -p [hostport:8080] -d [image:tag]