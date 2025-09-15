# DAT250 Experiment Assignment 6

During this exercise I worked on connecting a frontend built with React to my Spring Boot backend from the earlier assignments. One of the main technical problems I met was that the frontend and backend could not talk to each other at first because of CORS restrictions. I had to enable the correct CORS headers in my controllers before the requests worked. Another issue I ran into was that I sometimes built and deployed the frontend to the wrong static folder or had an old Spring application still running on port 8080, which made it look like my changes were not showing. I solved this by making sure I killed old processes, cleaned the static folder, and copied the new build from Vite. I also had some smaller problems with folder names and casing in React that caused import errors on Windows.



There are still some small issues with the deployment setup that I would like to improve. For example I would like a smoother Gradle integration so the frontend can be built and copied automatically before I start the backend, instead of having to do it manually. Otherwise the basic functionality with creating polls, voting and showing the results is working as expected.
