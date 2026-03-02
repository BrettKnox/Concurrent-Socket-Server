# Concurrent Socket Server Benchmarking  
Brett Knox & Gary Li  

## Overview

This project implements and benchmarks:

- A **multithreaded (concurrent) socket server**
- A **multithreaded client** capable of generating configurable load
- Performance comparison between **iterative** and **concurrent** server designs

The objective was to analyze how increasing client load affects:

- Total turnaround time  
- Average per-request turnaround time  
- System scalability behavior  

---

## Project Goals

### Server
- Listen on a specified port
- Accept and handle client requests
- Support concurrent request handling via threads
- Execute system-level operations:
  - Date & Time
  - Server Uptime
  - Memory Usage
  - Network Connections (`netstat`)
  - Current Users (`who`)
  - Running Processes (`ps -e`)
- Return results to client

### Client
- Prompt user for:
  - Server IP address
  - Port number
  - Operation to execute
  - Number of requests (threads)
- Spawn multiple threads
- Measure turnaround time per request
- Compute:
  - Total turnaround time
  - Average individual turnaround time

---

## Architecture

### Server Design

- Uses `ServerSocket` to listen on a fixed port
- Runs indefinitely
- For each connection:
  - Creates a new thread
  - Handles request via `handleClient()`
- Uses `Runtime.exec()` to execute Linux commands when required

Concurrency model:
```java
while (true) {
    Socket clientSocket = serverSocket.accept();
    new Thread(() -> handleClient(clientSocket)).start();
}
