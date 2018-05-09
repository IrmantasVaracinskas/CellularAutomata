package processing;

import cellUpdaters.CellUpdater;
import cellUpdaters.CellUpdaterLife;
import cellUpdaters.CellUpdaterLifeAverage;
import enumerators.CAType;
import neighbourCounters.NeighbourCounter;

/**
 * Created by irmis on 15/01/2018.
 */
public class CellUpdaterBuilder {
    public CellUpdater buildUpdater(String type, NeighbourCounter counter)
    {
        if(type.toLowerCase().equals(CAType.LIFE.name().toLowerCase()))
        {
            return new CellUpdaterLife(counter);
        } else if(type.toLowerCase().equals(CAType.LIFEAVERAGE.name().toLowerCase()))
        {
            return new CellUpdaterLifeAverage(counter);
        }
        return null;
    }
}
