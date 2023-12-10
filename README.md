- # General
  - #### Team#: mochi

  - #### Name: Jenny Lee

  - #### Project 5 Video Demo Link: (TBD)

  - #### Instruction of deployment:
  > 

  - #### Collaborations and Work Distribution: N/A


- # Connection Pooling
  - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
  > - a
  > - b

  - #### Explain how Connection Pooling is utilized in the Fabflix code.

  - #### Explain how Connection Pooling works with two backend SQL.


- # Master/Slave
  - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
  > - AddMovieServlet.java 
  > - AddStarServlet.java 
  > - AutoCompleteServlet.java 
  > - BrowseServlet.java 
  > - EmployeeLoginServlet.java 
  > - IndexServlet.java 
  > - LoginServlet.java 
  > - PaymentServlet.java 
  > - SearchServlet.java 
  > - SelectionServlet.java 
  > - ShoppingCartServlet.java 
  > - SingleMovieServlet.java 
  > - SingleStarServlet.java 
  > - Top20Servlet.java

  - #### How read/write requests were routed to Master/Slave SQL?
  > WRITE SOMETHING HERE

- # JMeter TS/TJ Time Logs
  - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot**            | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|-----------------------------------------|----------------------------|-------------------------------|-------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](logs/imgs/single_http_1thread.png)              | 191                        | 3.44127                              | 3.02437                  | ??           |
| Case 2: HTTP/10 threads                        | ![](logs/imgs/single_http_10threads.png)            | 235                        | 5.28237                              | 4.69535                  | ??           |
| Case 3: HTTPS/10 threads                       | ![](logs/imgs/single_https_10threads.png)            | 219                        | 4.72400                              | 4.06265                  | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](logs/imgs/single_http_10threads_ncp.png) | 273                        | 5.30742                            | 3.71432                  | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot**                | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|---------------------------------------------|----------------------------|-----------------------------------|-------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)                  | ??                         | ??                                | ??                      | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)                  | ??                         | ??                                | ??                      | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](logs/imgs/scaled_http_10threads_ncp.png) | 222                        | 10.84194                                  | 6.32775                        | ??           |