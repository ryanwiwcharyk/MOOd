# Project Proposal
## Team composition: Hana Louiza Moussaoui, Taryn Beaupre, Ryan Wiwcharyk
Application Name: MOOd

Application Logo: 

Description: MOOd is an internal application with the cutest little cow friend that lets you input your daily mood, give you AI-powered suggestions to support you, and manage a simple user account :3.

Application scope: Internal

Application Layout:

Repository: 

Four screens: 
- Sign in page: Allows the user to login or create a new account.
- Homepage: Displays a personalized message to the user encouraging them to log their mood for the day if they haven’t already, or gives them AI-Powered suggestions based on the mood they logged. 
	- Mood Logging Page: Allows the user to log their mood for the day and shows a calendar view of the moods of the past month. 
	- My Account Page: Allows the user to view their username and password and change it if necessary. 
- Navigation: Buttons allow the user to navigate between the views.
- Local Database: One “User” table with ID, username and password, preferred theme. One “Mood” table with mood logged, user ID and date logged.

Parameters to be saved: Light or dark theme

AI Integration: AI will be used to write custom messages to the user depending on their mood. 

## Database Schema
```mermaid
erDiagram
        User ||--o{ MoodHistory: has
    UserMood ||--|| MoodHistory: has
   UserMood }o--|| MoodType: has

    User {
        int user_ID PK
        string name
        string email
        string password
        date created_at
        date edited_at
        string profile_picture
	string theme
    }
    UserMood {
	int mood_id PK
	string entry
	int type_id FK
    }

   MoodHistory {
	int history_id PK
	date date_logged
	int user_id FK
	int mood_id FK
}
   MoodType {
	int type_id PK
	string name
	string description
}
```
