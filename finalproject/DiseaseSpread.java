/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package sim.app.virus;

import sim.field.continuous.*;
import sim.engine.*;
import sim.util.*;


public /*strictfp*/ class DiseaseSpread extends SimState
    {
    public static final double XMIN = 0;
    public static final double XMAX = 800;
    public static final double YMIN = 0;
    public static final double YMAX = 600;

    public static final double DIAMETER = 8;

    public static final double HEALING_DISTANCE = 20;
    public static final double HEALING_DISTANCE_SQUARED = HEALING_DISTANCE * HEALING_DISTANCE;
    public static final double INFECTION_DISTANCE = 20;
    public static final double INFECTION_DISTANCE_SQUARED = INFECTION_DISTANCE * INFECTION_DISTANCE;
    
    public static final int NUM_AGENTS = 100;

    public static final int NUM_FOOD = 20;


    Disease malaria = new Disease(0.2, 0.2, 0.2);

    public Continuous2D environment = null;

    /** Creates a DiseaseSpread simulation with the given random number seed. */
    public DiseaseSpread(long seed)
    {
        super(seed);
    }

    boolean conflict( final Agent agent1, final Double2D a, final Agent agent2, final Double2D b )
    {
        if( ( ( a.x > b.x && a.x < b.x+DIAMETER ) ||
                ( a.x+DIAMETER > b.x && a.x+DIAMETER < b.x+DIAMETER ) ) &&
                ( ( a.y > b.y && a.y < b.y+DIAMETER ) ||
                ( a.y+DIAMETER > b.y && a.y+DIAMETER < b.y+DIAMETER ) ) )
            {
            return true;
            }
        return false;
    }

    public boolean withinInfectionDistance( final Agent agent1, final Double2D a, final Agent agent2, final Double2D b )
    {
        return ( (a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y) <= INFECTION_DISTANCE_SQUARED );
    }

    public boolean withinHealingDistance( final Agent agent1, final Double2D a, final Agent agent2, final Double2D b )
    {
        return ( (a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y) <= HEALING_DISTANCE_SQUARED );
    }

    boolean acceptablePosition( final Agent agent, final Double2D location )
    {
        if( location.x < DIAMETER/2 || location.x > (XMAX-XMIN)/*environment.getXSize()*/-DIAMETER/2 ||
            location.y < DIAMETER/2 || location.y > (YMAX-YMIN)/*environment.getYSize()*/-DIAMETER/2 )
            return false;
        Bag mysteriousObjects = environment.getObjectsWithinDistance( location, 2*DIAMETER );
        if( mysteriousObjects != null ) {
            for( int i = 0 ; i < mysteriousObjects.numObjs ; i++ ) {
                if( mysteriousObjects.objs[i] != null && mysteriousObjects.objs[i] != agent ) {
                    Agent ta = (Agent)(mysteriousObjects.objs[i]);
                    if( conflict( agent, location, ta, environment.getObjectLocation(ta) ) )
                        return false;
                }
            }
        }
        return true;
    }
    // TODO:
    // set some agents infected at beginning of simulation
    //
    public void start()
    {
        super.start();  // clear out the schedule

        environment = new Continuous2D(25.0, (XMAX-XMIN), (YMAX-YMIN) );

        // Schedule the agents -- we could instead use a RandomSequence, which would be faster,
        // but this is a good test of the scheduler
       
        for(int x=0;x<NUM_AGENTS+NUM_FOOD;x++) {
            Double2D loc = null;
            Agent agent = null;
            int times = 0;
            do {
                loc = new Double2D( random.nextDouble()*(XMAX-XMIN-DIAMETER)+XMIN+DIAMETER/2,
                    random.nextDouble()*(YMAX-YMIN-DIAMETER)+YMIN+DIAMETER/2 );
                if( x < NUM_AGENTS )
                    agent = new HealthAgent( "Agent"+x, loc );
                else 
                    agent = new Food( "Food"+(x-NUM_AGENTS), loc );
                times++;
                if( times == 1000 ) {
                    System.err.println( "Cannot place agents. Exiting...." );
                    System.exit(1);
                }
            } while( !acceptablePosition( agent, loc ) );

            environment.setObjectLocation(agent,loc);
            schedule.scheduleRepeating(agent);
        }
    }

    public static void main(String[] args)
    {
        doLoop(DiseaseSpread.class, args);
        System.exit(0);
    }    
}

