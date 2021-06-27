# ImageColorBatch

### Summary

ImageColorBatch uses the Thread Pool pattern in order to save resources while processing each image and saving into a CSV file. This pattern involves Task Submitters and an Execution Service that manages a thread pool and an unbounded queue.

![Image of Thread Pattern](https://www.baeldung.com/wp-content/uploads/2016/08/2016-08-10_10-16-52-1024x572.png)


### Algorithm
```java
ExecutorService taskExecutor = Executors.newFixedThreadPool(4);
while(...) {
  taskExecutor.execute(new Task());
}
taskExecutor.shutdown();
try {
  taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
} catch (InterruptedException e) {
  ...
}
```

### Installation

Java 8+<br>
https://www.oracle.com/ca-en/java/technologies/javase/javase-jdk8-downloads.html

Maven 3.0+<br>
https://maven.apache.org/download.cgi

### Build with Maven
mvn clean install

### Run with Maven
mvn exec:java -Dexec.mainClass=com.pex.ImageColorBatch

### Notes
- To limit memory consumption, I set THREAD_POOL_SIZE = 5
- Allow printing memory consumption at the batch start, batch end and during process task start and end
- Allowing printing pool and queue size at every process task start
