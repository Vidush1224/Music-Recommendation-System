# Music Recommendation System

An advanced full-stack music recommendation platform built in **Java** and **MySQL**. This application provides **personalized song suggestions** based on user mood and activity context, using a *reverse mood intelligence* algorithm to recommend uplifting songs for improved well-being. Featuring a robust UI, integrated music player, real-time admin monitoring, and a properly normalized relational database.

---

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Database Model](#database-model)
- [Architecture](#architecture)
- [Setup & Installation](#setup--installation)
- [Usage](#usage)
- [Limitations](#limitations)
- [Author](#authors)
- [License](#license)

---

## Features

- **Mood & Activity Based Recommendations:** Match current user mood/activity and offer mood-shifting (uplifting) suggestions.
- **Reverse Mood Intelligence Algorithm:** Maps opposite emotions for better user experience.
- **MVC Architecture:** Separation of UI, business logic, and data layers for maintainability.
- **Integrated Music Player:** Using Java Sound API for playing music with play, pause, stop, and volume controls.
- **Normalized MySQL Database:** 9 tables engineered for users, admins, songs, moods, activities, ratings, favorites, and logs (3NF).
- **Dual Interfaces:**  
  - **User Dashboard (7 tabs):** Recommendations, Mood Shift, Search, Favorites, Top Rated, Music Player, Profile  
  - **Admin Panel (6 tabs):** Manage Songs, Moods, Activities, Tag Songs, Statistics, Activity Logs (live monitoring)
- **Community Ratings & Favorites:** Users can rate music and maintain playlists.
- **Real-Time Monitoring:** All important actions are logged and viewable live in the admin dashboard.
- **Production Quality:** Over 5,200 lines of code, 98% complete and operational.
- **Modern UI:** Designed with gradients and intuitive layouts.
- **Comprehensive Logging:** All database interactions are tracked in detail.

---

## Technology Stack

- **Java 11**
- **MySQL 8.0** (via JDBC)
- **Java Swing** (UI)
- **Java Sound API** (WAV support by default; MP3 via optional plugin)
- **Eclipse IDE / IntelliJ IDEA / MySQL Workbench**
- **Design Patterns:** MVC, DAO, Singleton

---

## Database Model

### Main Tables

- **users** (`userid`, `username`, `email`, `password`)
- **admins** (`adminid`, `username`, `password`)
- **songs** (`songid`, `title`, `artist`, `genre`, `duration`, `filepath`)
- **moods** (`moodid`, `moodname`, `description`)
- **activities** (`activityid`, `activityname`, `description`)
- **songmoods** (`songid`, `moodid`)
- **songactivities** (`songid`, `activityid`)
- **userratings** (`ratingid`, `userid`, `songid`, `rating`)
- **userfavorites** (`userid`, `songid`)
- **activitylogs** (`logid`, `actiontype`, `tablename`, `description`, `timestamp`)

See `schema.sql` in this repo for exact table definitions.

---

## Architecture

The system follows a **three-tier MVC architecture**:

- **Presentation Layer:**  
  - Java Swing UIs — Login, Signup, Dashboard (User/Admin), Music Player, Profile
- **Business Logic Layer:**  
  - `RecommendationService` — mood-matching & mood-shifting algorithms  
  - Multiple DAO classes for secure DB access
- **Data Layer:**  
  - MySQL DB — All user, song, mood, activity, ratings, favorites, and log data

---

## Setup & Installation

1. **Clone this repository**
2. Import into your Java IDE (Eclipse/IntelliJ)
3. Configure MySQL credentials in `DatabaseConfig.java`
4. Use `schema.sql` to create and initialize the required database tables
5. Run `TestJDBCConnection.java` or `JDBCTestDemo.java` to verify DB setup
6. Launch the application from `LoginScreen.java`

---

## Usage

**User Flow:**
- Register and login
- Select mood and activity
- Get personalized recommendations and mood-shifting suggestions
- Play music, manage favorites, rate songs

**Admin Flow:**
- Login to admin panel
- Add/edit/view songs, moods, activities
- Tag songs by mood/activity
- Monitor activity logs in real-time
- View dashboard statistics

---

## Limitations

- **Desktop-only:** Java Swing UI (no web/mobile app)
- **Single-user sessions:** Per app instance
- **Native WAV support:** MP3 requires adding an external library
- **Rule-based recommendations:** No machine learning/AI in current version

---

## Authors

- Vidush Varshneya

---

## License

For academic and educational use only.

---

**For any suggestions, contributions, or issues, please open an issue or submit a pull request!**
