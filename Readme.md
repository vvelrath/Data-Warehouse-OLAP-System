Data Warehouse/OLAP System
===========================

### Description

In this project, we implemented a genomic data warehouse application using AngularJS and Java Servlets.
Star schema design was used in this project. 

This data warehouse supports the following operations:
1) supports regular and statistical OLAP operations
2) robust to potential changes in the future
3) support knowledge discovery

### Data files

The original data files have been given as tab delimited text files. These text files have to be imported into Oracle Database for this project

### Answers Queries

The Data warehouse answers the following queries

	•	List the number of patients who had "tumor" (disease description). "leukemia" (disease type) and "ALL" (disease name), separately.
	•	List the types of drugs which have been applied to patients with "tumor"
	•	For each sample of patients with "ALL", list the mRNA values (expression) of probes in cluster id "00002" for each experiment with measure unit id = "001". (Note: measure unit id corresponds to mu_id in microarray_fact.txt, cluster_id corresponds to cl_id in gene fact.txt, mRNA expression value corresponds to exp in microarray_fact.txt, UID in probe.txt is a foreign key referring to gene fact.txt)

### Performs Statistical operations

	•	For probes belonging to GO with id "0012502", calculate the t statistics of the expression values between patients with "ALL" and patients without "ALL". (Note: Assume the expression values of patients in both groups have equal variance, use the t test for unequal sample site, equal variance)
	•	For probes belonging to GO with id "0007154", calculate the F statistics of the expression values among patients with "ALL", "AML", "colon tumor" and "breast tumour (Note: Assume the variances of expression values of all four patient groups are equal. You need to calculate six F-Statistics for ALL:AML, ALL:Colon-Tumor, AML: Colon Tumor etc...)
	•	For probes belonging to G0 with id="0007154", calculate the average correlation of
	expression values between two patients with "ALL", and calculate the average correlation of the expression values between one "ALL" patient and one "AML" patient. (Note: For each patient, there is a list of gene expression values belonging to GO with id="0007154" Suppose you get N1 "ALL" patients and N2 "AML" patients. For the average correlation of the expression values between two patients with "ALL", you need first calculate N1 x (N1 - 1)/2 Pearson correlations, then calculate the average value. For the average correlation of the expression values between one "ALL" patient and one "AML" patient, you need first calculate N1 x N2 Pearson Correlations then calculate the average value.) 
 
### Knowledge Discovery

	• Given a specific disease, find the informative genes. For example. suppose we are interested in the cancer "ALL". 
	1) Find all the patients with "ALL" (group A), while the other patients serve as the control (group B). 
	2) For each gene, calculate the t-statistics for the expression values between group A and group B. 
	3) If the p-value of the t-test is smaller than 0.01, this gene is regarded as an "informative" gene. 

	• Use informative genes to classify a new patient (five test cases in test_samples.txt are given in the data). For example, given a new patient PN, we want to predict whether he/she has "ALL". 
	1) Find the informative genes w.r.t. "ALL". 
	2) Find all the patients with "ALL" (group A). 
	3) For each patient PA in group A. calculate the correlation rA of the expression values of the informative genes between PN and PA. 
	4) Patients without "ALL" serve as the control (group B). 
	5) For each patient PB in group B, calculate the correlation rB of the expression values of the informative genes between PN and PB. 
	6) Apply t-test on rA and rB, if the p-value is smaller than 0.01, the patient is classified as "ALL".

### Steps to deploy the Data warehouse

• The Data files have to be imported into an oracle database
• The Connection URL of the database have to be changed in each of the servlets before deploying it
• The "DataWarehouseApp" folder represents the front end deliverables for this application.
• The "Query" folder represents the back end deliverables(servlets) for this application.
• Deploy the above two folders in a web application server like Tomcat and start using the Application with the URL ...http://localhost:8080/DataWarehouseApp 