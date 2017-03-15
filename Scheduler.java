import java.util.*;
import java.io.*;
/**
 *
 * @author meganmoore (mm7148)
 * Operating Systems Spring 2017
 * Lab 2
 */
public class Scheduler {
    
    private static boolean isVerbose;
    private static boolean showRandomVals;
    private static final File inputFile = new File ("random-numbers.txt");
    private static int numProcesses;
    private static Scanner random;
    private static boolean burst = false;
    private static boolean CPUBurst = false;
    
    
    public static void main(String[] args) throws FileNotFoundException {
        String input;//name of the input file
        boolean details;
        
        //determines the type of input and the values associated for output
        if(args[0].equals("--verbose")){
            isVerbose = true;
            showRandomVals = false;
            input = args[1];
            details = true;
        }else if (args[0].equals("show-random")){
            isVerbose = true;
            showRandomVals = true;
            input = args[1];
            details = true;
        }else{
            isVerbose = false;
            showRandomVals = false;
            input = args[0];
            details = false;
        }
        
        File file = new File(input);
        Scanner scan = new Scanner(file);
        
        int processID;
        numProcesses = scan.nextInt();
        
        ArrayList<Process> tempProcesses = new ArrayList<Process> (numProcesses);//array to sort the processes
        ArrayList<Integer> inputArray = new ArrayList<Integer> ((numProcesses*4)+1);//array to store the inputs
        inputArray.add(numProcesses);
        
        boolean set;
        for(int i=0; i<numProcesses; i++){
            set = false;
            int tempArrival = scan.nextInt();//A
            inputArray.add(tempArrival);
            int tempCompBurst = scan.nextInt();//B
            inputArray.add(tempCompBurst);
            int tempTotalCompTime = scan.nextInt();//C
            inputArray.add(tempTotalCompTime);
            int tempIOBurst = scan.nextInt();//IO
            inputArray.add(tempIOBurst);
            
            Process tempProcess = new Process(tempArrival, tempCompBurst, tempTotalCompTime, tempIOBurst);
            
            //sorts according to arrival time
            int size = tempProcesses.size();
            for(int j=0; j<size; j++){
                int compVal = tempProcesses.get(j).getArrival();
                if(tempArrival < compVal){
                    Process swap = tempProcesses.get(j);
                    Process tempSwap = tempProcesses.get(j+1);
                    tempProcesses.add(j, tempProcess);
                    for(int k=j+1; k<size+1; k++){
                        tempProcesses.add(k, swap);
                        swap = tempSwap;
                        tempSwap = tempProcesses.get(k+1);
                    }
                    tempProcesses.add(swap);
                    set = true;
                    break;
                }
            }
            if(set == false){
                tempProcesses.add(tempProcess);
            }
        }
        System.out.println();
        
        for(int i=0; i<numProcesses; i++){
            tempProcesses.get(i).setPID(i);//sets the unique ID for each process
        }

        System.out.println();
       
        Process[] processArray = new Process[tempProcesses.size()];
        
        processArray = tempProcesses.toArray(processArray);
        
        random = new Scanner(inputFile);
        
        
        if(isVerbose == true){
           System.out.println("This detailed printout gives the state and remaining burst for each process");
        }

        //do FCFS
        if((details == true && args[2].equals("FCFS"))||(details == false && args[1].equals("FCFS"))){
            printOriginal(inputArray);
            printSorted(processArray);
            System.out.println("First Come First Served");
            Scanner randomNums = new Scanner(inputFile);
            Process[] copy = new Process[numProcesses];
            for(int i=0; i<numProcesses; i++){
                copy[i] = new Process(processArray[i].getArrival(), processArray[i].getCompBurst(), processArray[i].getTotalCompTime(), processArray[i].getIOBurst());
            }
            FCFS(copy);
        }

        //Do RR
        if((details == true && args[2].equals("RR"))||(details == false && args[1].equals("RR"))){
            printOriginal(inputArray);
            printSorted(processArray);
            System.out.println("Round Robin");
            Scanner randomNums = new Scanner(inputFile);
            Process[] copy = new Process[numProcesses];
            for(int i=0; i<numProcesses; i++){
                copy[i] = new Process(processArray[i].getArrival(), processArray[i].getCompBurst(), processArray[i].getTotalCompTime(), processArray[i].getIOBurst());
            }
            RR(copy);
        }   
        
        //Do LCFS
        if((details == true && args[2].equals("LCFS"))||(details == false && args[1].equals("LCFS"))){
            printOriginal(inputArray);
            printSorted(processArray);
            System.out.println("Last Come First Served");
            Scanner randomNums = new Scanner(inputFile);
            Process[] copy = new Process[numProcesses];
            for(int i=0; i<numProcesses; i++){
                copy[i] = new Process(processArray[i].getArrival(), processArray[i].getCompBurst(), processArray[i].getTotalCompTime(), processArray[i].getIOBurst());
            }
            LCFS(copy);
        }
        
        //Do SJF
        if((details == true && args[2].equals("PSJF"))||(details == false && args[1].equals("PSJF")))
            printOriginal(inputArray);
            printSorted(processArray);
            System.out.println("Shortest job first");
            Scanner randomNums = new Scanner(inputFile);
            Process[] copy = new Process[numProcesses];
            for(int i=0; i<numProcesses; i++){
                copy[i] = new Process(processArray[i].getArrival(), processArray[i].getCompBurst(), processArray[i].getTotalCompTime(), processArray[i].getIOBurst());
            }
            PSJF(copy);
        }
    //prints the original input values
    private static void printOriginal(ArrayList<Integer> inputArray){
        System.out.print("The original input was:");
        for(int i=0; i<inputArray.size(); i++){
            if(((i-1)%4)==0){
                System.out.print(" ");
            }
            System.out.print(" "+inputArray.get(i));
        }
        System.out.println();
    }
    //prints the sorted processes
    private static void printSorted(Process[] processes){
        System.out.print("The (Sorted) input is : "+numProcesses+"");
        for(int i=0; i<numProcesses; i++){
            Process printProcess=processes[i];
            System.out.print("  "+printProcess.getArrival()+" "+printProcess.getCompBurst()+
                    " "+printProcess.getTotalCompTime()+" "+printProcess.getIOBurst());
        }
        System.out.println();
    }

    //first come first served, if the process arrives first then it will be run first
    private static void FCFS(Process[] proc){// Using the first come first served scheduling algorithm
        int time =0;
        int comp =0;
        int IO = 0;
        boolean boolIO;
        boolean boolCPU;
        boolean allDone = allDone(proc);
        
        while(allDone == false){
            if(isVerbose == true){
                printRound(proc, time);
            }
            
            boolIO = doBlockedProcesses(proc, time);
            boolCPU = doRunningProcesses(proc, time);
            doArrivingProcesses(proc, time);
            FCFSreadyProcesses(proc, time);
            
            if(boolIO == true){
                //System.out.println("IN IO");
                IO++;
            }
            if(boolCPU == true){
                //System.out.println("IN CPU");
                comp++;
            }
            time++;
            allDone = allDone(proc);
        }
        time--;
        
        System.out.println("The scheduling algorithm used was First Come First Served");
        printProcesses(proc);
        printSummary(proc, time, comp, IO);
    }
    //determines and runs ready processes 
    private static void FCFSreadyProcesses(Process[] proc, int time){
        boolean busyRunning = false;
        for(int i=0; i<numProcesses; i++){
            if(proc[i].isRunning() == true){
                busyRunning = true;
            }
        }
        int tempTime = 999999;
        int jobToRun = -1;
        if(busyRunning == false){//there is availability for processes to run
            for(int i=0; i<numProcesses; i++){
                int tempProcessTime = proc[i].getTimeReady();
                if(proc[i].isReady() && tempProcessTime < tempTime){
                    tempTime = tempProcessTime;
                    jobToRun = i;
                }
            }
            if(jobToRun != -1){
                CPUBurst = true;
                int newCompBurst = randomOS(proc[jobToRun].getCompBurst());
                proc[jobToRun].setRunning(newCompBurst);
            }
        }
        for(int i=0; i<numProcesses; i++){
            if(proc[i].isReady() == true){
                proc[i].readyRound();
            }
        }
    }
    //round robin deals with preemption to rotate processes through running
    private static void RR(Process[] processes){
        int time =0;
        int comp =0;
        int IO =0;
        boolean boolCPU;
        boolean boolIO;
        int quantum = 2;
        int quantumRemaining = quantum;
        int tempQuantum;
    
        while(allDone(processes) == false){//until all processes have terminated
            if(isVerbose == true){
                printRound(processes, time);
            }

            boolIO = doBlockedProcesses(processes, time);
            tempQuantum = quantumRemaining; 
            quantumRemaining = RRrunningProcesses(processes, time, quantum);
            if(quantumRemaining < tempQuantum){
                boolCPU = true;
            }else{
                boolCPU = false;
            }
            
            doArrivingProcesses(processes, time);
            
            if(RRreadyProcesses(processes, time) == true){
                quantumRemaining = quantum;
            }
            if(boolIO == true){
                IO++;
            }
            if(boolCPU == true){
                comp++;
            }
            time++;
        }
        time--;
        
        System.out.println("The Scheduling Algorithm used was round robin");
        printProcesses(processes);
        printSummary(processes, time, comp, IO);
    }
    
    //deals with processes that are running in RR
    private static int RRrunningProcesses(Process[] processes, int time, int quantumRemaining){
        for(int i=0; i<numProcesses; i++){
            if(processes[i].isRunning() == true){
                processes[i].runningRound();
                quantumRemaining--;
                
                if(processes[i].hasCompTimeLeft() == false){
                    processes[i].setTerminated(time);
                }
                else if(processes[i].hasBurstLeft() == false){
                    burst = true;
                    int newIOBurst = randomOS(processes[i].getIOBurst());
                }else if(quantumRemaining <=0){
                    processes[i].setReady(time);
                }
            }
        }
        return quantumRemaining;
    }
    
    //determines processes that are ready to run in RR
    private static boolean RRreadyProcesses(Process[] processes, int time){
        boolean busyRunning = false;
        boolean isProcess = false;
        for(int i=0; i<numProcesses; i++){
            if(processes[i].isRunning()){
                busyRunning = true;
            }
        }
        int tempTime = 99999;//bigger than all times
        int jobToRun = -1;
        if (busyRunning == false){
            for(int i=0; i<numProcesses; i++){
                int tempProcessTime = processes[i].getTimeReady();
                if(processes[i].isReady() && (tempProcessTime < tempTime)){
                    tempTime = tempProcessTime; 
                    jobToRun = i;
                }
            }
            
            if(jobToRun != -1){
                isProcess = true;
                if(processes[jobToRun].hasBurstLeft()){
                    int tempTimeRemaining = processes[jobToRun].getRemainingBurst();
                    processes[jobToRun].setRunning(tempTimeRemaining);
                }else{
                    CPUBurst = true;
                    int newCompBurst = randomOS(processes[jobToRun].getCompBurst());
                    processes[jobToRun].setRunning(newCompBurst);
                }
            }
        }
        for(int i=0; i<numProcesses; i++){
            if(processes[i].isReady() == true){
                processes[i].readyRound();
            }
        }
        return (busyRunning && (isProcess));
    }
    
    //last come first served, basically the opposite of fcfs, if it arrives 
    //later it gets priority to run
    private static void LCFS(Process[] processes){
        int time = 0;
        int comp = 0;
        int IO = 0;
        boolean boolIO;
        boolean boolCPU;
        
        while(allDone(processes) == false){
            if(isVerbose == true){
                printRound(processes, time);
            }

            boolIO = doBlockedProcesses(processes, time);
            boolCPU = doRunningProcesses(processes, time);
            doArrivingProcesses(processes, time);
            LCFSReadyProcesses(processes, time);
            if(boolIO == true){
                IO++;
            }
            if(boolCPU == true){
                comp++;
            }
            time++;
        }
        time--;
        
        System.out.println("The scheduling algorithm used was Last Come First Served");
        printProcesses(processes);
        printSummary(processes, time, comp, IO);
    }
    
    //deals with ready processes in LCFS
    private static void LCFSReadyProcesses(Process[] processes, int time){
        boolean busyRunning = false;
        for(int i=0; i<numProcesses; i++){
            if(processes[i].isRunning() == true){
                busyRunning = true;
            }
        }
        int tempTime = -1;
        int jobToRun = -1;
        if(busyRunning == false){//there is availability for processes to run
            for(int i=0; i<numProcesses; i++){
                int tempProcessTime = processes[i].getTimeReady();
                if(processes[i].isReady() && tempProcessTime > tempTime){
                    tempTime = tempProcessTime;
                    jobToRun = i;
                }
            }
            if(jobToRun != -1){
                CPUBurst = true;
                int newCompBurst = randomOS(processes[jobToRun].getCompBurst());
                processes[jobToRun].setRunning(newCompBurst);
            }
        }
        for(int i=0; i<numProcesses; i++){
            if(processes[i].isReady() == true){
                processes[i].readyRound();
            }
        }
         
    }
    
    //preemptive shortest job first runs the shortest jobs first with preemption
    private static void PSJF(Process[] processes){
        int time = 0;
        int comp = 0;
        int IO = 0;
        boolean boolIO;
        boolean boolCPU;
        
        while(allDone(processes) != true){
            if(isVerbose == true){
                printRound(processes, time);
            }

            boolIO = doBlockedProcesses(processes, time);
            boolCPU = doRunningProcesses(processes, time);
            doArrivingProcesses(processes, time);
            PSJFDoReadyProcesses(processes, time);
            
            if(boolIO == true){
            IO++;
            }
            if(boolCPU == true){
                comp++;
            }
            time++;
        }
        time--;
       
        System.out.println("The scheduling algorithm used was preemptive shortest job first");
        printProcesses(processes);
        printSummary(processes, time, comp, IO);
    }
    
    //deals with ready processes for PSJF
    private static void PSJFDoReadyProcesses(Process[] processes, int time){
        int tempTime = 99999999;
        int jobToRun = -1;
        boolean busyRunning = false;
        
        for(int i=0; i<numProcesses; i++){
            if(processes[i].isRunning() == true){
                busyRunning =true;
            }
        }
        
        if(busyRunning == false){
            for(int i =0; i<numProcesses; i++){
                if(processes[i].isReady() == true){
                    if(processes[i].getRemainingCompTime()<tempTime){//chooses the shortest job
                        tempTime = processes[i].getRemainingCompTime();
                        jobToRun = i;
                    }
                }
            }
            if(jobToRun != -1){//if there is a process that is ready
                if(processes[jobToRun].hasBurstLeft() == true){
                    processes[jobToRun].setRunning(processes[jobToRun].getRemainingBurst());
                }else{
                    CPUBurst = true;
                    int newCompBurst = randomOS(processes[jobToRun].getCompBurst());
                    processes[jobToRun].setRunning(newCompBurst);
                }
            }
        }
        
        for(int i=0; i<numProcesses; i++){
            if(processes[i].isReady() == true){
                processes[i].readyRound();
            }
        }
    }
    //gets a random number from the file to determine bursts
    private static int randomOS(int U){
        int randomNum = random.nextInt();
        if(showRandomVals == true){
            if(CPUBurst == true){
                System.out.println("Find burst when choosing ready process to run:" +randomNum);
                CPUBurst = false;
            }else if(burst == true){
                System.out.println("Find I/O burst when blocking a processs:" + randomNum);
                burst = false;
            }
        }
        return (1 + (randomNum % U));
    }
    
    //handles processes when they are blocked, returns true if there is a process that was blocked
    //during this round
    private static boolean doBlockedProcesses(Process[] processes, int time){
        boolean IORound = false;
        
        for(int i=0; i<numProcesses; i++){
            if(processes[i].isBlocked()){
                IORound = true;
                processes[i].blockedRound();
                //System.out.println("In blocked");
                if(processes[i].hasBurstLeft() == false){
                    processes[i].setReady(time);
                }
            }
        }
        return IORound;
    }
    
    
    //handles a process that is running during this round
    private static boolean doRunningProcesses(Process[] processes, int time){
        boolean CPURound = false;
        
        for(int i=0; i<numProcesses; i++){
            if(processes[i].isRunning()){
                CPURound = true;
                processes[i].runningRound();
                //System.out.println("In running");
                if(processes[i].hasCompTimeLeft() == false){
                    processes[i].setTerminated(time);
                }else if(processes[i].hasBurstLeft() == false){
                    burst = true;
                    int newIO = randomOS(processes[i].getIOBurst());
                    //processes[i].getIOBurst() * processes[i].getPreviousBurst();
                    processes[i].setBlocked(newIO);
                }
            }
        }
       return CPURound;  
    }
    
    //handles processes that arrive during this round
    private static void doArrivingProcesses(Process[] processes, int time){
        for(int i=0; i<numProcesses; i++){
            //System.out.println("In arriving");
            if(processes[i].isUnstarted() && (processes[i].getArrival() == time)){
                processes[i].setReady(time);
            }
        }  
    }  
    
    //checks if all of the processes have terminated
    private static boolean allDone(Process[] processes){
        for (int i=0; i<numProcesses; i++){
            //System.out.println("In Terminated");
            if(processes[i].isTerminated() == false){
                return false;
            }
        }
        return true;
    }
    
    //prints out the round with each state of each process before the next round runs
    private static void printRound(Process[] processes, int time){
        String status;
        int remainingTime;
        
        System.out.print("Before cycle    "+time+":  ");
        for(int i=0; i<numProcesses; i++){
            status = processes[i].getStatus();
            remainingTime = 0;
            if(status == "running" || status == "blocked"){
                remainingTime = processes[i].getRemainingBurst();
            }
            System.out.printf(" %10s %2d", status, remainingTime);

        }
        System.out.println();
    }
    //prints out the processes and its data
    private static void printProcesses(Process[] processes){
        for(int i=0; i<numProcesses; i++){
            System.out.println("Process "+i+":");
            System.out.print("(A,B,C,IO) = ("+processes[i].getArrival()+",");
            System.out.print(processes[i].getCompBurst()+",");
            System.out.print(processes[i].getTotalCompTime()+",");
            System.out.println(processes[i].getIOBurst()+")");
            System.out.println("Finishing time: "+processes[i].getTimeFinished());
            System.out.println("Turnaround time: "+processes[i].getTurnaroundTime());
            System.out.println("I/O time: "+ processes[i].getTotalIOTime());
            System.out.println("Waiting time: "+processes[i].getWaitingTime());
            System.out.println();
        }
    }
    //prints the final summary of the performance
    private static void printSummary(Process[] processes, int time, int compTime, int IOtime){
        float floatTime = (float)time;
        float floatNumProcesses = (float)numProcesses;
        float compUtilization = compTime/ floatTime;
        float IOUtilization = IOtime/ floatTime;
        float throughput = 100 * (processes.length/floatTime);
        int turnaround = 0;
        int waiting = 0; 
        float averageTurnaround;
        float averageWaiting;
        
        for(int i=0; i<numProcesses; i++){
            turnaround += processes[i].getTurnaroundTime();
            waiting += processes[i].getWaitingTime();       
        }
        averageTurnaround = turnaround / floatNumProcesses;
        averageWaiting = waiting/ floatNumProcesses;
        
        System.out.println("Summary Data:");
        System.out.println("Finishing time: " + time);
        System.out.println("CPU utilization: " + compUtilization);
        System.out.println("I/O Utilization: "+ IOUtilization);
        System.out.println("Throughput: "+throughput+ " processes per hundred cycles");
        System.out.println("Average Turnaround Time: "+ averageTurnaround);
        System.out.println("Average Waiting Time: "+ averageWaiting);
        System.out.println();
        
    }
}