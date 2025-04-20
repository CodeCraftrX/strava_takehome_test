# Steps to run the java application

---
#Programming Language 
 * Java 8 or higher is required
 * Need an Internet connection if running the api to fetch the data

 ---
 To fetch the output, open terminal in the project path, compile and run the project using below commands:

 * javac MainApplication.java
 * java MainApplication
 ---

 Once the app is running , it asks for two options <br/>
 
       Enter mode (file/api):
         1.file
           Enter filename (e.g., example-in.json): 
           (Give the filename  (Assumed the file is in the same folder as the mainapplication file))
         2.api
           Enter endpoint:
             (Give the api endpoint of the live api server)
---
  -- The results are printed as per below:
  
              ayanamala1@Abheeshts-MacBook-Air Strava_Take_Home % javac MainApplication.java
              ayanamala1@Abheeshts-MacBook-Air Strava_Take_Home % java MainApplication 
              Enter mode (file/api):
              file
              Enter filename (e.g., example-in.json): 
              example-in.json
              
              Printing largest indexes by storage size
              Index: express
              Size: 901.26 GB
              Index: secluded
              Size: 689.54 GB
              Index: oblivion
              Size: 537.62 GB
              Index: puzzle
              Size: 506.28 GB
              Index: spry
              Size: 119.40 GB
              
              Printing largest indexes by shard count
              Index: spry
              Shards: 20
              Index: bulldog
              Shards: 13
              Index: oblivion
              Shards: 7
              Index: postage
              Shards: 5
              Index: express
              Shards: 2
              
              Printing least balanced indexes
              Index: secluded
              Size: 689.54 GB
              Shards: 1
              Balance Ratio: 689
              Recommended shard count is 22
              Index: puzzle
              Size: 506.28 GB
              Shards: 1
              Balance Ratio: 506
              Recommended shard count is 16
              Index: express
              Size: 901.26 GB
              Shards: 2
              Balance Ratio: 450
              Recommended shard count is 30
              Index: oblivion
              Size: 537.62 GB
              Shards: 7
              Balance Ratio: 76
              Recommended shard count is 17
              Index: swirly
              Size: 12.78 GB
              Shards: 1
              Balance Ratio: 12
              Recommended shard count is 1
----
