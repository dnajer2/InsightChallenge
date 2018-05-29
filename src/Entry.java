/*
Each instance of this class will represent a user session. For each user session we will save:
-IP/user
-first doc access date
-first doc access time
-last doc access date
-last doc access time
-number of documents requested
 */

public class Entry {


    String name;
    String date;
    String startTime;
    String stopTime;
    String stopDate;
    int requests;


    Entry(String a, String b, String c, String d, String e, int f) {
        name = a;
        date = b;
        startTime = c;
        stopTime = d;
        stopDate = e;
        requests = f;

    }

}
