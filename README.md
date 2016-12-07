Deadline: Dec, 23rd

*Given: user-item matrix, item data, user data
*Goal: prediction user-item
*Output: user id \<tab> item id \<tab> predicted rating


File and goal statements are below.


Data set
*	matrix.dat : user – item matrix
*	Format:
*	user id  \<tab> item id \<tab> rating \<tab> timestamp
*	Stat
*	943 users
*	1682 items
*	100000 ratings


*	Item.dat
*	Format
*	movie id | movie title | release date | video release date | IMDb URL | unknown | Action | Adventure | Animation | Children's | Comedy | Crime | Documentary | Drama | Fantasy | Film-Noir | Horror | Musical | Mystery | Romance | Sci-Fi | Thriller | War | Western |


*	user.dat
*	Format
*	user id | age | gender | occupation | zip code


Submission
*	Predict.java
*	Usage: <# of users> <# of items> \<matrix data file> \<item info file> \<user info file> \<test file>
*	Test file
*	Format: user id \<tab> item id
*	Output
*	Format: user id \<tab> item id \<tab> predicted rating


Experimental results
*Prediction by user-based naive similarity: MAPE = 39.17479107428471%, RMSE = 1.431569107949076
*Prediction by user-based similarity which is only rated both active user and community user: MAPE = 36.43034331528833%, RMSE = 1.2602018671656074
