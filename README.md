# Library Management System

A Java-based GUI Library Management System designed to simplify the process of managing books, members, and transactions.  
It provides a secure login for admins and members, along with features for book management, member management, and transaction tracking.  
Built using Java Swing for the interface and text-based files for data storage, this system is lightweight, user-friendly, and effective for library operations.

## Objectives
- Design a user-friendly interface for librarians and members
- Add, update, and delete book and member records
- Manage book loans and returns
- Provide search and filter capabilities for books and members
- Secure login with role-based access control

## Technologies Used
- Java Programming Language
- Visual Studio Code
- Launcher4j (create executable from JAR)
- Inno Setup (create Windows installer)

## Features

### 1. Book Management
- Add, update, and delete book records
- Track availability status of books

### 2. Member Management
- Add, update, and delete member records
- View borrowed books

### 3. Transaction Management
- Loan and return book functionality

### 4. Search & Filter
- Search books by title or author name
- Search members by name or roll number

### 5. Role-based Access Control
- Admin: Full access to manage books, members, and transactions  
- Member: View borrowed books, reset password

### 6. Login System
- Secure login credentials for different roles

## Implementation
The project is developed using Java Swing for the GUI, with separate dashboards for librarians and members.  
Data is stored in text files (`books.txt`, `librarian.txt`, `member.txt`) and updated dynamically based on user actions.

**Workflow:**
1. Login – Users authenticate and are redirected to their dashboards.  
2. Librarian – Manage books, members, loans, returns, and search/filter records.  
3. Member – View borrowed books, update password.  
4. GUI – Tabs, dialog boxes, and organized layouts for smooth navigation.

