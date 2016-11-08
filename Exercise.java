package Assignment2;
import java.util.*;
public class Exercise{
    public ApparatusType at;
    public Map<WeightPlateSize,Integer> weight;
    public int duration;
    public Exercise(ApparatusType at,Map<WeightPlateSize, Integer> weight, int duration){
        this.at = at;
        this.weight= weight;
        this.duration = duration;

    }

    public static Exercise generateRandom(Map<WeightPlateSize, Integer> x){
        Random rand = new Random();
        Exercise temp = new Exercise(ApparatusType.values()[rand.nextInt(ApparatusType.values().length)],x,rand.nextInt(10));
        return temp;
    }
    public void debug(){
        Set<WeightPlateSize> weightKeys =  weight.keySet();
            System.out.print("apparatus:");
            System.out.print(at);
            System.out.print(" | ");
        for(WeightPlateSize i : weightKeys){
            System.out.print(i);
            System.out.print(":");
            System.out.print(weight.get(i));
            System.out.print(" | ");
        }
        System.out.print("duration:");
        System.out.println(duration);
    }

}

