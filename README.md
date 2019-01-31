# Popular Movies Project Submission By Jasdeep Singh

## Project Overview
Waheguru ji
This is submission project code which is part [udacity android developer course](https://in.udacity.com/course/android-developer-nanodegree-by-google--nd801).
The **Popular Moves** app shows a grid of most popular, top rated and user defined favorite movies along with the details of each movie once it is selected.

[Scroll Down to screenshots](#ss)

## Why this Project
To become an Android developer, one must know how to bring particular mobile experiences to life. Specifically, one needs to know how to build clean and compelling user interfaces (UIs), fetch data from network services, and optimize the experience for various mobile devices. 

By building this app, one can demonstrate understanding of the foundational elements of programming for Android. 
The app communicates with the Internet and provides a responsive and delightful user experience.

## What I learnt?
This project was built in 2 parts. Through this project, I learnt to:

### Part 1
- present the user with a grid arrangement of movie posters upon launch

- allow your user to change sort order via a setting:
  - The sort order can be by most popular or by highest-rated
  
- allow the user to tap on a movie poster and transition to a details screen with additional information such as:
  - original title
  - movie poster image thumbnail
  - a plot synopsis (called overview in the api)
  - user rating (called vote_average in the api)
  - release date
  
- fetch data from the Internet with theMovieDB API.
- use adapters and custom list layouts to populate list views.
- incorporate libraries to simplify the amount of code you need to write

### Part 2
- allow users to view and play trailers (either in the youtube app or a web browser)
- allow users to read reviews of a selected movie
- allow users to mark a movie as a favorite in the details view by tapping a button (star)
- make use of Android Architecture Components (Room, LiveData, ViewModel and Lifecycle) to create a robust and efficient application
- create a database using Room to store the names and ids of the user's favorite movies (and optionally, the rest of the information needed to display their favorites collection while offline)
- modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection
- build a fully featured application that looks and feels natural on the latest Android operating system


## How I completeted this Project?
- I followed a impelementation guide from Udacity (not open source) along with mockups

- [Link](https://github.com/cingh-jasdeep/popular-movies/projects/1) to project to outline my workflow

- <a name="ss"></a>Here are some screenshots

|<img src="/screenshots/movie_grid_view.png" width="150">|<img src="/screenshots/movie_details.png" width="150">|<img src="/screenshots/movie_trailers_screenshots.png" width="150">|<img src="/screenshots/sort_order_setting.png" width="150">|






Please Note: 
This is just a sample app, built for education purposes.
Movie posters copyrights belong to their respective owners.

## How to run?

install `android studio` and import project using `github tool`

### Needs API key to work
To use this project make sure you insert your themoviedb.org API key in [`Constant.java`](/app/src/main/java/example/android/com/popularmovies/data/Constant.java) file to make the project work.

Thank you for your time :)
