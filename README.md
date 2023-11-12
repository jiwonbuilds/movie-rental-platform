## CS 122B Project 3 (Jenny Lee)

### Video Demo Link
(TBD)

### Website Link
(TBD)

***

### Files with Prepared Statements
- MainSAXParser.java

### Two Parsing Time Optimization Strategies
1. **Streamlining Data Parsing and Insertion Through Batch Insertion**

    In parsing XML files for database population, a time optimization strategy was implemented using batch insertion for database operations. This involved grouping records into batches, significantly reducing both database connections and execution frequency. The efficiency gains were substantial, resulting in a notable improvement in overall performance. Comparative analysis with individual insertions demonstrated a significant reduction in overhead, as the batching strategy streamlined the process and minimized latency. This optimization proved highly effective in expediting insertion tasks for improved operational efficiency.

2. **Leveraging In-Memory Hash Tables for Efficient Database Operations**

   Another time optimization technique involved pre-loading database data into hash maps and subsequently updating the in-memory hash maps. This approach mitigated the need for frequent database queries to check for data existence during the parsing process. By updating these in-memory hash maps with parsed data, we effectively circumvented the necessity of continuous database connections and query calls. This resulted in a notable time-saving advantage, as the absence of these calls streamlined the data parsing workflow. The in-memory hash maps provided a rapid and efficient mechanism for data existence checks, contributing significantly to the overall acceleration of the parsing and insertion tasks.

### Inconsistency Data Reports
- ```DuplicateMovies.txt``` includes movie entries that had duplicate ids or information. They were not entered into the 
movies.moviedb table.
- ```InconsistentMovies.txt``` contains movies with incomplete information, which means at least one of the fields was 
not provided in the xml. They were not entered into the movies.moviedb table.
- ```MissingMovies.txt``` lists movie ids that exist in *casts124.xml* but not in *mains243.xml*. They
  were not entered into the movies.moviedb table. 
- ```MissingStars.txt``` enumerates star names in *casts124.xml* but not in *mains243.xml*. They were not entered into the 
stars.moviedb table.
- ```NoStarMovies.txt``` lists movie ids of movies in *mains243.xml* that lacked star information in *casts124.xml*. They 
were not entered into the movies.moviedb table. They were entered into the movies.moviedb table without stars.