package activityScheduler;

import java.util.*;

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
        Random generator = new Random( 314159 );   /** "seed" the random number generator with PI */
        for ( int i = 0; i < TIME_RANGE; i++ ) {
            int startTime = generator.nextInt ( TIME_RANGE );
            int finishTime = (int) ( startTime + 1 + (Math.sqrt(TIME_RANGE) * 0.2 * generator.nextDouble ( )) );
            finishTime = Math.min( finishTime, TIME_RANGE );

            Activity nextActivity = new Activity( startTime, finishTime ); 
            activityList.add (nextActivity);
        }
    }


    private void findSolutionUsingGreedyByFinishTime()
    {
                //Implement your solution here.
        solution = new ArrayList<Activity>(); 
    }
    
    
    private void findSolutionUsingGreedyByLength_SLOW()
    {
                //Implement your solution here.
       solution = new ArrayList<Activity>(); 
    }

    
    
    private void findSolutionUsingGreedyByLength_FAST()
    {
                //Implement your solution here.
        solution = new ArrayList<Activity>(); 
    }

    


    private class OverlapComparator implements Comparator<Activity>
    {
        public int compare ( Activity first, Activity second )
        {
            if ( first.getFinishTime() <= second.getStartTime() )
                return -1;
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
    private class Activity implements Comparable<Activity>
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

    
    /** Display the activities that were successfuly scheduled */
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