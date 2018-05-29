# InsightChallenge
Insight Data Engineering Challenge

This program is written in java and uses Scanner to read in input files and Filewriter to write the sessionization.txt .
First we create a hashmap where the key is a string containing the user's ip, and the value is a made up "Entry" object that contains all the necessary information we need to output for the challenge. Next log.csv and inactivity_period.txt are opened with Scanner. inactivity_period.txt is read and the value in the file is converted to its int representation in seconds. This creates the time limit used to decide whether or not a user session has timed out. We also create an int variable called "clk" that will later be used to see if a user session is inactive. Now, we start reading the log files.

Basically, we have a while loop that calls scanner.next() to retrieve each line in the log (until we reach the end). The line is broken up into several strings (the seperator being a comma) that are then stored into array. First we "update" our clock to match the time of the current line read. Then we iterate through the hash map to see if any users have gone over (this is true when user access time+inactivty_period time < clk). Then, we print these users' data onto the sessionization.txt, remove their entries from the hashmap and move onto analyzing the rest of the current line's data.

The first entry in the array(which corresponds to the user ip address/ "name") is then checked against the hashmap. If an entry with a key matching the user name exists, we only update the stop time and increment the number of accesses. If an entry is not found, we make a new entry with the Entry object filled out with the desired data. This entire process is repeated until the last line has been finally read.

After exiting the while loop, we begin to flush the contents of the hashmap. Initially, I just iterated through the hashmap and printed all the contents of the Entry value. While the contents were the same as the expected outputs, all the lines printed out during the "flush" section of the program were out of order. This resulted in a failing test case. In order to fix this, I backtracked to the beginning of the program and created an arraylist of strings that keeps track of the order the user/keys comes in. Then, at the end, we just iterate through the arraylist of strings, using each one as a key to get its corresponding entry that will then have its contents written out.

(Sorry for not making more test cases. Getting the program to work with the run.sh file took more work than it should have)
