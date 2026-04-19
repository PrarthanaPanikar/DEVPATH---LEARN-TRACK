# DEVPATH-LEARN-TRACK

DevPath is an integrated student-faculty ecosystem designed to align academic technical training with specific corporate standards. By providing a centralized platform for curriculum mapping, DevPath enables institutions to deliver targeted learning paths that enhance student employability for top-tier technology firms.

🛠 Technical Stack
Application Logic: Java (JDK 8+)

Graphical Interface: Java Swing / JavaFX (Desktop-based GUI)

Data Persistence: MySQL (Relational Database)

Database Connectivity: JDBC (Java Database Connectivity)

Environment: Cross-platform (Windows, macOS, Linux)

🚀 Core Functionalities
1. Faculty Administration
The Faculty Dashboard serves as the command center for curriculum design, allowing educators to manage the "Course Library" with precision.

Direct Corporate Mapping: Courses are assigned directly to a specific company (e.g., Google, Meta, Netflix), ensuring a focused learning track.

Dynamic CRUD Operations: Comprehensive capabilities to Create, Read, Update, and Delete course modules.

Metadata Management: Granular control over course difficulty (Easy, Medium, Hard) and duration tracking.

2. Student Experience
The Student Dashboard provides a personalized, distraction-free interface focused on their target career goals.

Targeted Visibility: Students only view courses mapped to the company they are preparing for.

Progress Tracking: Ability to view available coursework, monitor progress, and mark modules as complete.

Intuitive Navigation: A streamlined layout featuring "View Courses," "Mark Complete," and "View Progress" modules.

💾 Database Schema Optimization
To ensure maximum scalability and reduce query complexity, DevPath utilizes a Direct Company-to-Course Mapping architecture. This prevents "Phase-dependency" errors and ensures data is accessible even before a full roadmap is finalized.
Security & Integrity
SQL Injection Prevention: All database interactions utilize PreparedStatement to sanitize inputs and prevent malicious code execution.

Resource Management: Implementation of try-with-resources blocks ensures that database connections, statements, and result sets are closed automatically, preventing memory leaks in an enterprise environment.

UI Safety: Deletion operations include a "Double-Confirm" validation that checks the existing company mapping before removal to prevent data loss.
