package neighbourCounters;

import processing.Sketch;

/**
 * Created by Irmis on 2018-01-07.
 */
public interface NeighbourCounter
{
    public float countNeighbours(int x, int y);
    public float countNeighbours(Sketch.Cell cell);

    public int getTotalNeighbours();
}