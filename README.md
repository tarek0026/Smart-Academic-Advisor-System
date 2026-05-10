# Smart Academic Advisor System

A smart academic advising system built using Java and Object-Oriented Programming (OOP) concepts.
The system helps university students plan their semesters intelligently by analyzing academic progress, prerequisites, GPA, track requirements, and course dependencies.

---

# Features

## Available Courses Detection

The system automatically determines which courses a student can register for based on:

* Completed courses
* Prerequisites
* Major and track
* GPA restrictions
* Credit hour requirements
* Internship and graduation project rules

---

## Smart Recommendation System

The project generates a recommended semester plan using a priority-based recommendation algorithm.

The recommendation system prioritizes:

1. Delayed courses from previous semesters
2. Current semester courses
3. Important future courses with high dependency impact

This helps students:

* Avoid graduation delays
* Unlock future courses earlier
* Improve semester planning

---

## Elective Recommendation Groups

Instead of automatically choosing electives, the system creates flexible elective recommendation groups such as:

* Choose 1 Big Data Elective
* Choose 1 Media Elective
* Choose 3 Courses From General Pool

This simulates real university advising systems and gives students flexibility in course selection.

---

# Supported Tracks

## CS Major

* BIGDATA
* MEDIA
* GENERAL

## AI Major

* AI Track

---

# GPA-Based Load Management

The system dynamically determines the allowed semester load based on GPA.

| GPA Range | Load Type   | Max Credit Hours |
| --------- | ----------- | ---------------- |
| Below 2.0 | Half Load   | 12 CH            |
| 2.0 - 3.0 | Normal Load | 19 CH            |
| Above 3.0 | Overload    | 21 CH            |

---

# Technologies Used

* Java
* Object-Oriented Programming (OOP)
* Java Collections Framework
* JSON Data Storage
* JavaFX GUI
* VS Code
* Gson Library

---

# OOP Concepts Used

## Encapsulation

Student and Course data are encapsulated inside classes with getters and setters.

## Abstraction

Business logic is separated into service classes.

## Modularity

The project is divided into:

* Classes
* Services
* Application

## Reusability

Recommendation and advisor logic can be reused in both Terminal and GUI versions.

---

# Project Structure

```text id="q0uhso"
src
│
├── Application
│   ├── MainApp.java
│   └── GUI_App.java
│
├── Classes
│   ├── Student.java
│   └── Course.java
│
├── Services
│   ├── AdvisorService_CS.java
│   ├── AdvisorService_AI.java
│   ├── RecommendationService.java
│   ├── LoadService.java
│   ├── LoadData.java
│   └── RecommendationBlock.java
│
└── Data
    ├── CS_courses.json
    └── AI_courses.json
```

---

# How the Recommendation Algorithm Works

Each available course receives a recommendation score based on:

* Semester priority
* Year priority
* Dependency importance
* Academic progression impact

Courses with higher scores receive higher recommendation priority.

---

# GUI Version

The project also includes a JavaFX GUI version featuring:

* Student information forms
* Course selection panels
* Available courses display
* Recommended semester dashboard
* Elective recommendation sections
* Dynamic credit hour calculations

---

# Future Improvements

* Full database integration
* Authentication system
* Academic transcript generation
* Drag-and-drop semester planning
* Advanced analytics dashboard
* AI-based recommendation enhancement

---

# Example Output

```text id="y2h0vx"
Recommended Courses
1. CSCI313 | Software Engineering | 3 CH
2. CSCI305 | Database Systems | 3 CH
3. CSCI417 | Machine Intelligence | 3 CH

Choose 1 Big Data Elective
1. CSCI463 | Introduction to Computer Networks | 3 CH
2. CSCI464 | Numerical Methods & Math Precision | 3 CH
```

---

# Team Goal

The main goal of this project is to simulate a realistic academic advising system that assists students in making smarter academic decisions while applying software engineering and OOP principles in a real-world educational scenario.
