# Popular Movies (Stage 2)

## Project Overview
In second stage, I added more functionality to the app I built in Stage 1.

Added more information to your movie details view:

- allowed users to view and play trailers ( either in the youtube app or a web browser).
- allowed users to read reviews of a selected movie.
- allowed users to mark a movie as a favorite in the details view by tapping a button(star).
- created a **database** and **content provider** to store the names and ids of the user's favorite movies (and optionally, the rest of the information needed to display their favorites collection while offline).
- modified the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.

I built a UI that presented the user with a grid of movie posters, allowed users to change sort order, and presented a screen with additional information on the movie selected by the user:

## What Did I Learn After Stage 2?
I built a fully featured application that looks and feels natural on the latest Android operating system (Nougat, as of November 2016).

## Rubric:

 - Common Project Requirements

   - MEETS SPECIFICATIONS
     - [x] App is written solely in the Java Programming Language.
     - [x] App conforms to common standards found in the Android Nanodegree General Project Guidelines.

 - User Interface - Layout

   - MEETS SPECIFICATIONS
     - [x] UI contains an element (e.g., a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated.
     - [x] Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails.
     - [x] UI contains a screen for displaying the details for a selected movie.
     - [x] Movie Details layout contains title, release date, movie poster, vote average, and plot synopsis.
     - [x] Movie Details layout contains a section for displaying trailer videos and user reviews.

 - User Interface - Function

   - MEETS SPECIFICATIONS
     - [x] When a user changes the sort criteria (most popular, highest rated, and favorites) the main view gets updated correctly.
     - [x] When a movie poster thumbnail is selected, the movie details screen is launched.
     - [x] When a trailer is selected, app uses an Intent to launch the trailer.
     - [x] In the movies detail screen, a user can tap a button(for example, a star) to mark it as a Favorite.

 - Network API Implementation

   - MEETS SPECIFICATIONS
     - [x] In a background thread, app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu.
     - [x] App requests for related videos for a selected movie via the /movie/{id}/videos endpoint in a background thread and displays those details when the user selects a movie.
     - [x] App requests for user reviews for a selected movie via the /movie/{id}/reviews endpoint in a background thread and displays those details when the user selects a movie.

 - Data Persistence

   - MEETS SPECIFICATIONS
     - [x] The titles and ids of the user's favorite movies are stored in a ContentProvider backed by a SQLite database. This ContentProvider is updated whenever the user favorites or unfavorites a movie.
     - [x] When the "favorites" setting option is selected, the main view displays the entire favorites collection based on movie ids stored in the ContentProvider.