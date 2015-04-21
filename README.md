Fusion Engine
==============

This documentation provides information on how to setup one instance of the Fusion Engine (FE) service. Once setup and executed an OCD (Open City Database) will be available in a database that you can query. The FE has two parts:
- FrontEnd part: this is a Tomcat application that allows you to configure OCDs
- Service part: this is the service that implements the fusion
Both components are linked by means of a database, thus configuration files are similar in both parts
 
For the moment the FE does not provide means to input user generated content (UCG), though it may be possible.
Anyway you may export the POI database (or part of it) to other POI related databases:

- OCDB ((https://github.com/fraunhoferfokus/OCDB): This is a Specific Enabler developed within FiContent2
- POI DP (http://catalogue.fiware.org/enablers/poi-data-provider): This is a Generic Enabler developed within FIWARE 
 
The FE provides a selected list of POIs (Points of Interests) from a certain city.
Currently the FE only supports CitySDK (http://www.citysdk.eu/developers/) interfaces for accessing data sources and providing data access.

The development carried out in this project has been carried out within the FICONTENT2 project (http://mediafi.org/)
This is version v03. If you are using previous version, we recommend you to update to this version


Installation
------------
You may want to read the 'installation_guide.pdf' file located under the doc folder


User and Administration
-----------------------
You may want to read the 'user_admin_guide.pdf' file located under the doc folder



Developer
---------
If you are a developer and want to further extend this tool you may want to read the 'developer_guide.pdf' file located under the doc folder



Problems
--------
Please use the issue tracker to report any problems you might encounter.



License
-------
Copyright 2014 SATRD-Universitat Politecnica de Valencia

```
/*******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright 2014 SATRD-Universitat Politecnica de Valencia
 *******************************************************************************/
```
