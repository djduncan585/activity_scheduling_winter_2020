/*
 Dave Duncan
 CSC 406 Algorithms & Data Structures
 Dr. Jerry Ajay
 Homework 1
 18 February 2020
 */

package activityScheduler;

import java.util.*;

//import activityScheduler.ActivityScheduler.Activity;

/**
@author Dave Duncan
@version 1.0
<p>
This is Mr. Duncan's implementation of the ActivityScheduler class 
as specified in Homework 1, based on assignment code that came from
Dr. Ajay. Today, we explore smarter algorithms and data structures
for faster execution as two Greedy By Activity Length top fuel
dragsters battle for the cup. Will the old reliable brute force
insertion dominate, or will it lose to the leaner, hungrier binary
tree insertion and traversal? (I think you already know the result.)
Start your engines!
</p>
*/
public class ActivityScheduler
{
    /** List of the Activities that we want to schedule */
    private  ArrayList<Activity> activityList;

    /** These are the activities that were successfully scheduled 
     *  This is a subset of the input activity list 
     *  None of these activities overlap with each other */
    private  Collection<Activity> solution;

    public static final int TIME_RANGE = 40000;  
    
    private int algorithmType;
    
    public static final int BY_FINISH_TIME = 0;
    public static final int BY_LENGTH_SIMPLE = 1;    
    public static final int BY_LENGTH_SMART = 2;
    
    OverlapComparator overlapcomparator = new OverlapComparator();
    LengthComparator lengthcomparator = new LengthComparator();
    
    
    public static void main ( String[] args )
    {
        ActivityScheduler greedyScheduler;  
        
        greedyScheduler = new ActivityScheduler( BY_FINISH_TIME );
        greedyScheduler.runScheduler();
        
        greedyScheduler = new ActivityScheduler( BY_LENGTH_SIMPLE );
        greedyScheduler.runScheduler();
        
        greedyScheduler = new ActivityScheduler( BY_LENGTH_SMART );
        greedyScheduler.runScheduler();
    }

    
    public ActivityScheduler( int type )
    { 
       this.algorithmType = type;
    }

    
    /** Execute the Scheduling Algorithm and report the solution and the execution time */
    private void runScheduler() {
        
        long startTime;
        long endTime;
         
        initializeActivityList();
        
        startTime = System.nanoTime();
        
        if ( algorithmType == BY_FINISH_TIME ) {
            findSolutionUsingGreedyByFinishTime();
            
        } else if ( algorithmType == BY_LENGTH_SIMPLE ) {
            findSolutionUsingGreedyByLength_SLOW();
            
        } else if ( algorithmType == BY_LENGTH_SMART ) {
           findSolutionUsingGreedyByLength_FAST();
           
        } else
          throw new RuntimeException ( "Unknown greedy scheduling algorithm" );
          
        endTime = System.nanoTime();       
  
        verifySolution();
        
        reportResults( endTime-startTime );
    }
 
    
    private void reportResults ( long executionTime )
    {        
        System.out.print ( "The results of the Greedy " );
        if ( algorithmType == BY_FINISH_TIME ) {
            System.out.print ( "by Finish Times " );
        } else if ( algorithmType == BY_LENGTH_SIMPLE ) {
            System.out.print ( "by Activity Length (using simple List data structure) " );
        } else 
            System.out.print ( "by Activity Length (using better data structure) " );
        System.out.println ( "algorithm are as follows:" );
        
        if ( solution.size() < 10 ) 
           System.out.println ( "The final schedule is: " + this );
        
        System.out.println ( "\tThe number of activites scheduled = " + solution.size() );
        
        System.out.println ( " \tThe total time to schedule the activities was: " +
                             executionTime/(1000000.0) + " milli-seconds\n" ); 
    }
    
    
    /** Initilize the Collection of Activities   */
    private  void initializeActivityList()
    {
        activityList = new ArrayList<Activity>();

        /** Sample data set. */
        activityList.add (new Activity(2,4));
        activityList.add (new Activity(10,12));
        activityList.add (new Activity(5,7));
        activityList.add (new Activity(1,3));
        activityList.add (new Activity(3,6));
        activityList.add (new Activity(8,11));
        activityList.add (new Activity(6,10));       
        activityList.add (new Activity(0,7));      
        activityList.add (new Activity(3,11));

        /** create a large input set for this scheduling problem, filled with random data */
        activityList = new ArrayList<Activity>();
        Random generator = new Random( 314159 );   // "seed" the random number generator with PI
        
        for ( int i = 0; i < TIME_RANGE; i++ ) {
            int startTime = generator.nextInt ( TIME_RANGE );
            int finishTime = (int) ( startTime + 1 + (Math.sqrt(TIME_RANGE) * 0.2 * generator.nextDouble ( )) );
            finishTime = Math.min( finishTime, TIME_RANGE );

            Activity nextActivity = new Activity( startTime, finishTime ); 
            activityList.add (nextActivity);
        }
        
    }
    
    /**<p>
     * Helper function to sort the ArrayList<Activity> entries by finishTime. Since
     * all merge sorts use the same starting process of splitting up a list or vector
     * of items, the functions have been merged together.
     * </p>
     * 
     * @param input The ArrayList<Activity> to be sorted.
     * @param sortType The type of sort performed according to the constants defined
     * at the top of the ActivityScheduler class.
     * 
     * @return ArrayList<Activity>
     */
    
    private ArrayList<Activity> mergeSort(ArrayList<Activity> input, int sortType){
    	ArrayList<Activity> output;
    	int length = input.size();
    	int half = length / 2;
    	if (length > 1) {
    		ArrayList<Activity> leftPart = new ArrayList<Activity>();
    		for(int i = 0; i < half; i++) {
    			leftPart.add(input.get(i));
    		}
    		ArrayList<Activity> rightPart = new ArrayList<Activity>();
    		for(int i = half; i < length; i++) {
    			rightPart.add(input.get(i));
    		}
    		leftPart = mergeSort(leftPart, sortType);
    		rightPart = mergeSort(rightPart, sortType);
    		if(sortType == BY_FINISH_TIME)
    			output = new ArrayList<Activity>(mergeByFinishTime(leftPart, rightPart));
    		else if(sortType == BY_LENGTH_SIMPLE || sortType == BY_LENGTH_SMART) {
    			output = new ArrayList<Activity>(mergeByLength(leftPart, rightPart));
    		}
    		else
    			throw new RuntimeException("Unknown sortType. Looks like you have more code to write. Yay!");
    	}
    	else {
    		output = new ArrayList<Activity>(input);
    	}
    	return output;
    }
    
    /**Helper function to complete the merge sorts by finish time performed in this exercise.
     * 
     * @param leftPart One of the two ArrayList<Activity> objects to be merged.
     * @param rightPart The second of the two ArrayList<Activity> objects to be merged.
     * @return ArrayList<Activity>
     */
    private ArrayList<Activity> mergeByFinishTime(ArrayList<Activity> leftPart, ArrayList<Activity> rightPart){
    	ArrayList<Activity> output = new ArrayList<Activity>();
    	while ((leftPart.size() > 0) && (rightPart.size() > 0)) {
    			if(leftPart.get(0).compareTo(rightPart.get(0)) > 0) {
    				output.add(rightPart.remove(0));
    			}
    			else {
    				output.add(leftPart.remove(0));
    			}
    	}
    	while (leftPart.size() > 0) {
    		output.add(leftPart.remove(0));
    	}
    	while (rightPart.size() > 0) {
    		output.add(rightPart.remove(0));
    	}
    	return output;	
    }

    /**
     * Part 1 of the assignment.
     * After sorting of the activities by finish time (efficient merge sort), they are
     * added to the schedule by a linear comparison with the last activity finish time
     * inserted (O(n), nice!)
     */
    private void findSolutionUsingGreedyByFinishTime()
    {
        //Create new list of activities
        solution = new ArrayList<Activity>();
        ArrayList<Activity> sorted = mergeSort(activityList, BY_FINISH_TIME);
        int lastEnd = 0;
        for(int i = 0; i < sorted.size(); i++) {
        	if(sorted.get(i).getStartTime() >= lastEnd) {
        		solution.add(new Activity(sorted.get(i).getStartTime(), sorted.get(i).getFinishTime()));
        		lastEnd = sorted.get(i).getFinishTime();
        	}
        }
    }
    
    /**
     * Helper function for a merge sort that sorts firstly in order of increasing length,
     * secondly in order of finish time.
     * @param leftPart One of the two lists to be merged.
     * @param rightPart The other of the two lists to be merged.
     * @return ArrayList<Activity>
     */
    private ArrayList<Activity> mergeByLength(ArrayList<Activity> leftPart, ArrayList<Activity> rightPart){
    	ArrayList<Activity> output = new ArrayList<Activity>();
    	while ((leftPart.size() > 0) && (rightPart.size() > 0)) {
    		//This is where the merge by minimal size followed by end time happens
    		if(lengthcomparator.compare(leftPart.get(0), rightPart.get(0)) > 0){
    			output.add(rightPart.remove(0));
    		}
    		else if(lengthcomparator.compare(leftPart.get(0), rightPart.get(0)) < 0){
    			output.add(leftPart.remove(0));
    		}
    		//After this point we know that the left and right part items being compared are the same length,
    		//so we are now comparing for finish time
    		else if(leftPart.get(0).compareTo(rightPart.get(0)) > 0) {
				output.add(rightPart.remove(0));
			}
    		else {//leftPart's finish time <= rightPart's finish time
				output.add(leftPart.remove(0));
			}
		}
		while (leftPart.size() > 0) {
			output.add(leftPart.remove(0));
		}
		while (rightPart.size() > 0) {
			output.add(rightPart.remove(0));
		}
    	return output;
    }
    /**
     * Part 2 of the assignment. After the activities are sorted first by increasing length then by finish time
     * (efficient merge sort algorithm), they are placed in the schedule if there is no overlap with existing
     * activities (not-so-efficient brute force algorithm. O(n^2) :P )
     */
    private void findSolutionUsingGreedyByLength_SLOW()
    {
       solution = new ArrayList<Activity>();
       ArrayList<Activity> sorted = mergeSort(activityList, BY_LENGTH_SIMPLE);
     //iterator candidate keeps track of the object we are attempting to add to the schedule
       for(int candidate = 0; candidate < sorted.size(); candidate++) {
    	   //iterator scheduledtask keeps track of tasks already added to the schedule
    	   boolean overlaps = false;
    	   for(int scheduledtask = 0; scheduledtask < solution.size(); scheduledtask++) {
    		   //If the candidate overlaps with any of the already added tasks, don't add it
    		   if(overlapcomparator.compare(sorted.get(candidate), ((ArrayList<Activity>)solution).get(scheduledtask)) == 0) {
    			   overlaps = true;
    			   break;
    		   }
    	   }
    	   if(!overlaps) {
    		   solution.add(sorted.get(candidate));
    	   }
    	   
       }
    }

    
    /**
     * Part 3 of the assignment. After the activities are sorted as in Part 2, they are placed into a binary tree
     * (O(n log n) best case, O(n^2) worst case), then extracted to output.
     */
    private void findSolutionUsingGreedyByLength_FAST()
    {
        solution = new ArrayList<Activity>();
        Activity nextActivity = null;
        ActivityTree root = new ActivityTree();
        //Insert all the activities into a binary tree
        ArrayList<Activity> sorted = mergeSort(activityList, BY_LENGTH_SMART);
        while(sorted.size() > 0) {
        	nextActivity = sorted.remove(0);
        	root.attemptInsert(nextActivity);
        }
        //Traverse the binary tree and get it all back
        root.traverseTheTreeAndPutTheActivitiesIn((ArrayList<Activity>)solution);
    }

    


    private class OverlapComparator implements Comparator<Activity>
    {
        public int compare ( Activity first, Activity second )
        {
        	//Finish time for first is before start time for second
            if ( first.getFinishTime() <= second.getStartTime() )
                return -1;
            //Finish time for second is before start time for first
            else if ( first.getStartTime() >= second.getFinishTime() )
                return 1;
            else
                return 0;  // means that they overlap
        }
    }


    /** compare two Activites based on the length of each Activity */
    private class LengthComparator implements Comparator<Activity>
    {
        public int compare ( Activity first, Activity second )
        {
            if ( first.getActivityLength() < second.getActivityLength() )
                return -1;
            else if ( first.getActivityLength() > second.getActivityLength() )
                return 1;
            else
                return 0;
        }
    }

    
    /** This class represents a single Activity, with its own startTime and finishTime */
    public class Activity implements Comparable<Activity>
    {
        private int startTime;
        private int finishTime;

        public Activity ( int s, int f ) {
            this.startTime = s;
            this.finishTime = f;
            if ( s > f )
                throw ( new RuntimeException("Invalid Activity") );
        }

        public int getStartTime() { return startTime; }

        public int getFinishTime() { return finishTime; }

        public int getActivityLength() { return finishTime - startTime; }

        public String toString() { return ("[" + startTime + "," + finishTime + "]"); }

        /** the "natural ordering" of activities is based on their "finish times" */
        public int compareTo( Activity otherActivity )
        {           
            if ( this.finishTime < otherActivity.finishTime )
                return -1;
            else if ( this.finishTime > otherActivity.finishTime )
                return 1;
            else
                return 0;
        }
    }
    
    /**
     * This class represents a binary tree for part 3 of the assignment. Since the
     * Activities here are not sorted primarily by time, this will give improved
     * performance over the brute force insertion of Part 2, with the same order
     * of complexity as a Quick Sort (O(n log n) best case, O(n^2) worst case.)
     */
    public class ActivityTree {
    	private Activity node = null;
    	private ActivityTree earlier = null;
    	private ActivityTree later = null;
    	/**
    	 * Attempts to insert an Activity in this ActivityTree. If it overlaps with
    	 * any of the existing activities in the tree, it will not be inserted. If
    	 * there is room for it, it will be inserted in the appropriate branch/leaf
    	 * recursively.
    	 * @param a The activity we are attempting to insert in the ActivityTree.
    	 */
    	public void attemptInsert(Activity a) {
    		//check if there's already an Activity assigned to this node.
    		//If not, put the Activity here.
    		if (this.node == null){
    			this.node = new Activity(a.getStartTime(), a.getFinishTime());
    		}
    		//If there's already an Activity here, test it against the candidate
    		//for insertion. If it's earlier, insert a new ActivityTree node on the
    		//earlier branch if needed. Then recursively try to insert on the earlier
    		//branch.
    		else if (overlapcomparator.compare(a, this.node) < 0) {
    			if (this.earlier == null) {
    				this.earlier = new ActivityTree();
    			}
    			this.earlier.attemptInsert(a);
    		}
    		//If it's later, insert a new ActivityTree node on the
    		//later branch if needed. Then recursively try to insert on the later
    		//branch.
    		else if (overlapcomparator.compare(a, this.node) > 0) {
    			if (this.later == null) {
    				this.later = new ActivityTree();
    			}
    			this.later.attemptInsert(a);
    		}
    		//If it overlaps, let it drop to the rocks below. Splat!
    	}
    	
    	/**
    	 * Performs an in-order traversal of this ActivityTree, successively adding
    	 * Activities to the ArrayList given as an argument to the function.
    	 * @param a The ArrayList<Activity> that you wish to put the contents of the
    	 * ActivityTree in.
    	 */
    	public void traverseTheTreeAndPutTheActivitiesIn(ArrayList<Activity> a){
    		//Go down the left side
    		if(this.earlier != null) {
    			earlier.traverseTheTreeAndPutTheActivitiesIn(a);
    		}
    		//Then add the value in the node
    		a.add(this.node);
    		//Then go down the right side.
    		if(this.later != null) {
    			later.traverseTheTreeAndPutTheActivitiesIn(a);
    		}
    		//And you are done!
    	}
    }

    
    /** Display the activities that were successfully scheduled */
    public String toString()
    {
        if ( solution.isEmpty() )
            return "{}";

        String result = "{";
        for ( Activity a : solution )
            result += a.toString() + ", ";
        return ( result.substring(0, result.length()-2) + "}" );  // remove last comma
    }
    
    
    
    private void verifySolution() {

        if ( solution.isEmpty() )
            return;
            
        if ( algorithmType == BY_LENGTH_SIMPLE ) {
              /**  sort the list of activities based on the "natural ordering".  */
              Collections.sort( (List<Activity>) solution );
        }

        Iterator<Activity> iter = solution.iterator();

        Activity curr = iter.next();

        while ( iter.hasNext() ) {

            Activity next = iter.next();
            if ( next.getStartTime() < curr.getFinishTime() ) {
                throw ( new RuntimeException("illegal schedule"));
            }
            curr = next;
        } 
    }

}