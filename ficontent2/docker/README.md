Docker Usage
============

If you plan to test the FE with Docker, you should follow these steps:

1.	Install [Docker](https://docs.docker.com/installation/#installation)  
	We have tested it with the [Ubuntu installation](https://docs.docker.com/installation/ubuntulinux/)
	
2.	Build Docker Image  
	*You can skip this step and pull the last successful build from the docker hub*  
	2.1	Copy **Dockerfile** and **install.sh** in the same directory  
	2.2 `cd` to the directory and execute:  
		`docker build -t poi-fusion-engine3 .`
	
3.	Run Docker Image  
	-	If you built the image locally (step 2) execute:  
		`docker run -ti -p 8080:8080 --name fe3 poi-fusion-engine3`  
	-	If you want to run the last build from the docker hub execute:  
		`docker run -ti -p 8080:8080 --name fe3 enolgor/poi-fusion-engine3`
	
4.	To test and use the fusion engine go to [http://localhost:8080/fic2_fe_v3_frontend](http://localhost:8080/fic2_fe_v3_frontend)  

5.	To terminate the container press `Ctrl+p` `Ctrol+q` to unattach and then execute:  
	`docker rm -f fe3`  