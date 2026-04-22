GitHub Activity CLI (Java)

A simple command-line application that fetches and displays the recent
public activity of any GitHub user using the GitHub REST API.

Features: - Fetch recent GitHub activity - Clean readable output -
Supports Push, Issues, Star events - Shows date & time (IST) - Handles
errors gracefully

Tech Stack: - Java - HttpURLConnection - GitHub API

Project Structure: Main.java

Setup & Run: 1. Clone: git clone
https://github.com/your-username/github-activity-cli.git cd
github-activity-cli

2.  Compile: javac Main.java

3.  Run: java Main

Example: java Main torvalds

Sample Output: - [22 Apr 2026, 03:45 PM] Pushed 2 commits - [21 Apr
2026, 11:10 AM] Starred repo

API: https://api.github.com/users//events

Limitations: - Manual JSON parsing - Limited event types - Only public
data

Future Improvements: - Use Jackson - Add more events - Build Spring Boot
API


