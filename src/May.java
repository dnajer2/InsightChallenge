import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class May {


    static int TIMEOUT = 0; //holds value from inactivity_period.txt


    /*
    Method to convert string time stamp (in HH:MM:SS) into its corresponding seconds value.
    Takes in a string and outputs an int
     */
    public static int toSeconds(String timeStamp) {
        int sec = 0;
        String[] split = timeStamp.split(":");
        sec = ((Integer.parseInt(split[0])) * 360) + ((Integer.parseInt(split[1])) * 60) + ((Integer.parseInt(split[2])));
        return sec;

    }


    public static void main(String args[]) {
        ArrayList<String> keyOrder = new ArrayList<String>(); //holds hashmap entry keys subject for removal

        //Hashmap will be used to store all current user settings
        //K=string of user's ip, V= Entry object holding the session's info
        HashMap<String, Entry> hmap = new HashMap<String, Entry>();

        //two instances of scanner for inactivity_period.txt and log.csv
        Scanner scanner = null;
        Scanner timeScan = null;

        try {

            //Creates sessionization.txt for writing to
            // File file = new File("./output/sessionization.txt");
            // if(!file.exists()) {
            //     file.getParentFile().mkdirs();
            //     file.createNewFile();
            // }
            FileWriter writer = new FileWriter("../output/sessionization.txt", true);

            //Get the scanner instance
            scanner = new Scanner(new File("../input/log.csv"));
            timeScan = new Scanner(new File("../input/inactivity_period.txt"));

            int clk; //keeps track of current time (in seconds) based on read-in line
            //will be used later on to detect when a session is over


            TIMEOUT=Integer.parseInt(timeScan.next()); //read in and assign value inactivity_period.txt
            scanner.next(); //skip headers




            /*
            This entire while loop reads in log.csv line by line and performs several actions
            -split read-in line where commas occur and place broken strings into array
            -iterate through the hash map that contains all the user sessions and see if any timeouts have occurred
                 -if timeouts have occurred, then that current session is written to sessionization.txt
                  and the entry is removed
            -from string array, read in the ip/name for user and see if it is in the hash map
                -if the user is in the map, only update the stop time, stop date and requests
                -if the user is not in the map, create a new entry and fill it out with data from the line
             -Repeat for all the lines in input file
             */
            while (scanner.hasNext()) {
                String txt[] = scanner.next().split(","); //split read-in line here
                /*
                txt format
                ip,      date,  time, zone,cik,accession,extention,code,size,idx,norefer,noagent,find,crawler,browser
                txt[0], txt[1], txt[2], .......................................................................txt[15]

                 */
                clk=toSeconds(txt[2]);//update our clock here



                ArrayList<String> removal = new ArrayList<String>(); //holds hashmap entry keys subject for removal

                //for loop to iterate through user hash map. Here we search for any sessions that have timed out
                for (HashMap.Entry<String, Entry> flushHMap : hmap.entrySet()) {
                    Entry clear = flushHMap.getValue(); //get Entry object from hash map


                    //Timeout occurs when the current time exceeds the last entry time + inactivity period,
                    //so when this occurs, write the current session info into sessionization.txt and prep the entry
                    //for removal
                    if ((toSeconds(clear.stopTime)+ TIMEOUT) < clk) {
                        //write all the necessary info to file
                        writer.write(clear.name + "," + clear.date + " " + clear.startTime + "," + clear.stopDate + " " + clear.stopTime + ","
                                + (toSeconds(clear.stopTime) - toSeconds(clear.startTime) + 1) + "," + clear.requests);
                        writer.write("\r\n");   // write new line

                        removal.add(clear.name); //prep entry for removal

                    }
                }



                //Can't remove hash map entries while iterating through it, and I wasn't too
                //sure on how to use the iterator, so I just stored the hash map keys into an array
                //and then had the entries removed outside the previous block
                for (int i=0; i<removal.size(); i++){
                    hmap.remove(removal.get(i));
                    keyOrder.remove(removal.get(i));

                }
                removal.clear(); //clear array to prevent repeated lines


                //IF block to see whether or not an entry exists. If it does, update the required information.
                // if not, add a new entry to the hash map
                if (hmap.containsKey(txt[0])) {

                    Entry temp = hmap.get(txt[0]); //get entry object

                    //update stop time, stop date, and number of requests
                        temp.stopTime = txt[2];
                        temp.stopDate = txt[1];
                        temp.requests = temp.requests + 1;
                        //System.out.println(txt[0] + " " + temp.requests);

                } else {

                    //create new entry into hash map
                    Entry session = new Entry(txt[0], txt[1], txt[2], txt[2], txt[1], 1);
                    hmap.put(txt[0], session);
                    keyOrder.add(txt[0]);
                }
            }
             System.out.println(hmap);


            /*
             //once all the lines have finally been read, the hash map will be flushed of its contents, which will
            //be written into sessionization.txt
            for (HashMap.Entry<String, Entry> entry : hmap.entrySet()) {
                Entry temp = entry.getValue();
                writer.write(temp.name + "," + temp.date + " " + temp.startTime + "," + temp.stopDate +
                        " " + temp.stopTime + "," + ((toSeconds(temp.stopTime) - toSeconds(temp.startTime)) + 1) +
                        "," + temp.requests);
                writer.write("\r\n");   // write new line


            }
            */


            /*
            Originally, I iterated through the hash map to retrieve all the user sessions at the end of the file,
            but all the entries were not in order.

            In order to keep the proper order, I made an arraylist that keeps track of user sessions as they come in,
            (deleting any expiring ones along the way). This preserves order, at the expense of more resources/speed

             */

            for (int i=0; i<keyOrder.size(); i++){
                Entry temp= hmap.get(keyOrder.get(i));
                  writer.write(temp.name + "," + temp.date + " " + temp.startTime + "," + temp.stopDate +
                          " " + temp.stopTime + "," + ((toSeconds(temp.stopTime) - toSeconds(temp.startTime)) + 1) +
                          "," + temp.requests);
                  writer.write("\r\n");   // write new line
              }


            writer.close(); //close sessionization.txt


           // error catching
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close read in files
            timeScan.close();
            scanner.close();
        }
    }
}
