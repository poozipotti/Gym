package Assignment2;
import java.util.*;
import java.util.concurrent.*;

public class Gym implements Runnable{
    private static final int GYM_SIZE = 30;
    private static final int GYM_REGISTERED_CLIENTS = 10000;
    private static final int AMOUNT_OF_EACH_MACHINE = 5;
    private Map<WeightPlateSize,Integer> noOfWeightPlates;
    private Set<Integer> clients; //for generating fresh client ids, holds client ids
    private ExecutorService executor = Executors.newFixedThreadPool(GYM_SIZE);
    private final Semaphore moveWeights = new Semaphore(1);
    private final Semaphore checkWeights = new Semaphore(1);
    private Map<ApparatusType, Semaphore> apparatusMutex = new HashMap<ApparatusType,Semaphore>();
    private final Semaphore IDMutex = new Semaphore(1);
    private final Semaphore debugMutex = new Semaphore(1);
    int test = 0;
    int i;
    //various semaphores - declaration o;mitted
    //five of each apparatus
    //shared resources apparatus and weight plates gym is a shared resource but it won't be handled with semaphores
    public Gym(){
        noOfWeightPlates = new HashMap<WeightPlateSize, Integer>();
        noOfWeightPlates.put(WeightPlateSize.SMALL_3KG,40);
        noOfWeightPlates.put(WeightPlateSize.MEDIUM_5KG,30);
        noOfWeightPlates.put(WeightPlateSize.LARGE_10KG,20);
        for(ApparatusType i : ApparatusType.values()){
            apparatusMutex.put(i, new Semaphore(5));
        }
        clients = new HashSet<Integer>();
    }
    public void debug(){
        System.out.print("Number of gym plates | Small Plates: ");
        System.out.print(noOfWeightPlates.get(WeightPlateSize.SMALL_3KG));
        System.out.print(" | Medium Plates: ");
        System.out.print(noOfWeightPlates.get(WeightPlateSize.MEDIUM_5KG));
        System.out.print(" | Large Plates:  ");
        System.out.print(noOfWeightPlates.get(WeightPlateSize.LARGE_10KG));
        System.out.println(" |");
    }
    public void run(){
        for(i = 0; i<GYM_REGISTERED_CLIENTS; i++){
            executor.execute(new Runnable() {
                public void run() {
                    try{
                        IDMutex.acquire();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    ///////// ID CRITICAL SECITON///////////
                    Client client = Client.generateRandom(clients.size());
                    clients.add(client.id);
                    IDMutex.release();
                    ///////// ID CRITICAL SECTION END //////////
                    for(Exercise i : client.getRoutine()){
                        while(true){
                            try{
                                apparatusMutex.get(i.at).acquire(); //one of five machines acquired
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }
                            try{
                                checkWeights.acquire();
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }
                            /////////// CRITICAL SECTION CHECK TO SEE IF ENOUGH WEIGHTS ARE THERE//////////////
                            if(noOfWeightPlates.get(WeightPlateSize.SMALL_3KG)<i.weight.get(WeightPlateSize.SMALL_3KG)  ||
                               noOfWeightPlates.get(WeightPlateSize.MEDIUM_5KG)<i.weight.get(WeightPlateSize.MEDIUM_5KG)||
                               noOfWeightPlates.get(WeightPlateSize.LARGE_10KG)<i.weight.get(WeightPlateSize.LARGE_10KG)){
                                //////leave critical sections for apparatus and checking weights then try to reenter//////
                                /* DEBUGGING
                               i.debug();
                                System.out.print("Didn't have enough weights for this excercise in client ");
                                System.out.println(client.id);
                                Gym.this.debug();*/
                                //////leave critical sections for apparatus and checking weights then try to reenter//////
                                apparatusMutex.get(i.at).release(); //release current machine
                                checkWeights.release();
                                continue;
                            }else{
                                try{
                                    moveWeights.acquire();
                                }catch(InterruptedException e){
                                    e.printStackTrace();
                                }
                                /////////////////////CRITICAL SECTION TO TAKE WEIGHTS
                                noOfWeightPlates.put(WeightPlateSize.SMALL_3KG,noOfWeightPlates.get(WeightPlateSize.SMALL_3KG)-i.weight.get(WeightPlateSize.SMALL_3KG));
                                noOfWeightPlates.put(WeightPlateSize.MEDIUM_5KG,noOfWeightPlates.get(WeightPlateSize.MEDIUM_5KG)-i.weight.get(WeightPlateSize.MEDIUM_5KG));
                                noOfWeightPlates.put(WeightPlateSize.LARGE_10KG,noOfWeightPlates.get(WeightPlateSize.LARGE_10KG)-i.weight.get(WeightPlateSize.LARGE_10KG));
                                ///////////////////////END BOTH WEIGHT CRITICAL SECTIONS ////////////////////////
                                moveWeights.release();
                                checkWeights.release();
                                try{
                                    Thread.sleep(i.duration);
                                }catch(InterruptedException e){
                                    e.printStackTrace();
                                }
                                apparatusMutex.get(i.at).release(); //release current machine
                                try{
                                    moveWeights.acquire();
                                }catch(InterruptedException e){
                                    e.printStackTrace();
                                }
                                noOfWeightPlates.put(WeightPlateSize.SMALL_3KG,noOfWeightPlates.get(WeightPlateSize.SMALL_3KG)+i.weight.get(WeightPlateSize.SMALL_3KG));
                                noOfWeightPlates.put(WeightPlateSize.MEDIUM_5KG,noOfWeightPlates.get(WeightPlateSize.MEDIUM_5KG)+i.weight.get(WeightPlateSize.MEDIUM_5KG));
                                noOfWeightPlates.put(WeightPlateSize.LARGE_10KG,noOfWeightPlates.get(WeightPlateSize.LARGE_10KG)+i.weight.get(WeightPlateSize.LARGE_10KG));
                                moveWeights.release();
                                break;

                            }
                        }
                    }
                    try{
                        debugMutex.acquire();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    client.debug();
                    debugMutex.release();
                }
            });
        }
        executor.shutdown();
    }
}
