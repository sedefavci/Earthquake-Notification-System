# Earthquake Notification & Simulation System

A Java-based **Discrete Event Simulation** engine that monitors earthquake activities and notifies registered "Watchers" based on their proximity to the epicenter. The system processes two asynchronous data streams (watchers and earthquakes) and synchronizes them into a unified chronological timeline.

## System Architecture & Logic

This project demonstrates advanced data management and simulation techniques:

### 1. Chronological Event Management
- **Time-Step Simulation:** The system iterates through `currentHour`, processing events only when their scheduled time arrives.
- **Data Persistence:** Uses a "Storage" list to prevent data loss from stream-reading, ensuring all records are accessible throughout the simulation lifecycle.

### 2. Custom Data Structures
To ensure maximum control over memory and performance, the system utilizes:
- **`SinglyLinkedList`:** For managing the 6-hour active earthquake window.
- **`DoublyLinkedList`:** For efficient addition and removal of watchers.
- **Memory Optimization:** Automatically purges earthquake records older than 6 hours to maintain an efficient runtime footprint.

### 3. XML Data Parsing
- Implemented a robust **Manual XML Parser** using `Scanner`. 
- Handles multi-word location strings (e.g., "San Francisco, CA") and coordinate extraction with cross-platform decimal formatting (Double.parseDouble).



## Key Features

- **Proximity Alerts:** Calculates Euclidean distance between coordinates and triggers notifications based on a magnitude-scaled radius formula ($distance < 2 \times magnitude^3$).
- **Dynamic Watcher Management:** Supports real-time `add` and `delete` commands for watchers via input files.
- **Query Engine:** Instant retrieval of the largest earthquake recorded within the last 6-hour sliding window.
- **Error Handling:** Robust protection against `FileNotFoundException` and input mismatch errors.

## Project Structure

- `NotificationSystem.java`: The simulation orchestrator and main execution engine.
- `Earthquake.java` & `Watcher.java`: Entity classes representing the core data models.
- `SinglyLinkedList.java` & `DoublyLinkedList.java`: Custom-built linear data structures.

## How to Run

1. Prepare your `watcher` and `earthquake` text files in the project root.
2. Run `NotificationSystem.java`.
3. Provide the filenames when prompted.

---
