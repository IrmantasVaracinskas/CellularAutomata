package neighbourCounters;

import enumerators.CAType;
import enumerators.Neighborhood;
import processing.Sketch;

/**
 * Created by Irmis on 2018-01-07.
 */
public class NeighbourhoodCounterBuilder
{
    public NeighbourCounter buildCounter(String neighbourhood, int radius)
    {
        if(Sketch.type.equals(CAType.LIFE.name().toLowerCase())) {
            if (neighbourhood.toLowerCase().equals(Neighborhood.MOORE.name().toLowerCase())) {
                return new MooreCounter(radius);
            } else if (neighbourhood.toLowerCase().equals(Neighborhood.NEUMAN.name().toLowerCase())) {
                return new NeumanCounter(radius);
            }
        }
        else if(Sketch.type.equals(CAType.LIFEAVERAGE.name().toLowerCase())){
            if (neighbourhood.toLowerCase().equals(Neighborhood.MOORE.name().toLowerCase())) {
                return new MooreCounterSum(radius);
            } else if (neighbourhood.toLowerCase().equals(Neighborhood.NEUMAN.name().toLowerCase())) {
                return new NeumanCounterSum(radius);
            }
        }
        return null;
    }
}
