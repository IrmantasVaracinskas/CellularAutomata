package neighbourCounters;
import Helpers.*;
import neighbourCounters.NeighbourCounter;
import processing.Sketch;

/**
 * Created by Irmis on 2018-01-07.
 */
public class MooreCounter implements NeighbourCounter
{
    private int radius;
    public MooreCounter(int _radius)
    {
        radius = _radius;
    }
    public float countNeighbours(int x, int y)
    {
        int neighbours = 0;
        for(int i = Helpers.max(0, x - radius); i <= Helpers.min(x + radius, Sketch.board.boardWidth-1); i++)
        {
            for(int j = Helpers.max(0, y - radius); j <= Helpers.min(y + radius, Sketch.board.boardHeight-1); j++)
            {
                if(i != x || j != y)
                {
                    Sketch.Cell c = Sketch.board.getCell(i, j);
                    if(c.state > 0)
                        neighbours++;
                }
            }
        }
        return neighbours;
    }

    public float countNeighbours(Sketch.Cell cell)
    {
        return countNeighbours(cell.x, cell.y);
    }

    public int getTotalNeighbours()
    {
        return Helpers.pow(2*radius + 1, 2);
    }
}