package Assignment2;
import java.util.*;
public class Client{
    public int id;
    private List<Exercise> routine;
    public Client(int id){
        this.id = id;
    }
    public void addExcercise(Exercise e){
        routine.add(e);
    }
    public static Client generateRandom(int id){
        //includes 15-20 exercises in routine
        Random rand = new Random();
        Client temp = new Client(id);
        temp.routine = new Vector<Exercise>();
        for(int i=15+rand.nextInt(5)+1; i>0;i--){
                Map<WeightPlateSize,Integer> tempWeight = new HashMap<WeightPlateSize,Integer>();
                tempWeight.put(WeightPlateSize.SMALL_3KG,rand.nextInt(10)+1);
                tempWeight.put(WeightPlateSize.MEDIUM_5KG,rand.nextInt(10)+1);
                tempWeight.put(WeightPlateSize.LARGE_10KG,rand.nextInt(10)+1);
                temp.addExcercise(Exercise.generateRandom(tempWeight));
        }
        return temp;
    }
    public List<Exercise> getRoutine(){
        return routine;
    }
    public void debug(){
        System.out.print("Client id: ");
        System.out.println(id);
        for(Exercise i : routine){
            i.debug();
        }
    }
}

