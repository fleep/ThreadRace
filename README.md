Just a little calisthenics with multithreading in Java. 100% of the code can be found in [Main.java](./src/main/java/org/fleep/Main.java).

Simulates a horse race with 5 horses, where each horse is moved by a different thread. The main thread checks horse progress to see if there was a winner and renders the race.

There is a time limit, and if no horse reaches the end in that time, the main thread kills all the child threads and shows that there's no winner.

The child threads explicitly won't terminate without the main thread issuing an interrupt, just as an example of interrupt behavior.

Here's a video showing a win, and a timeout:

https://github.com/user-attachments/assets/a19eb689-d42a-4f43-b499-0200fd6c1ee7

