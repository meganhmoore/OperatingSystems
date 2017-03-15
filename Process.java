//import java.util.*;
//import java.io.*;

public class Process implements Comparable<Process>{
    private int PID;//processID
    private final int arrival;//the only one that can be zero
    private final int compBurst;//blocks after x amount of time
    private final int totalCompTime;//total CPU time
    private final int IOBurst;//blocks for x amount of time
    
    private String status;//unstarted, ready, blocked, running or terminated
    private int remainingCompTime;//time that still needs to run
    private int timeBecameReady;//time the process became ready
    private int remainingBurst;//remaining time in IO or CPU burst
    private int previousBurst;
    private int firstBurst;
    private boolean first;
    private int timeFinished;//time that the process finished
    private int totalIO;//time spent blocked waiting for IO
    private int timeWaiting;//time spent waiting in ready state
    
    
    
    public Process(int arrival, int compBurst, int totalCompTime, int IOBurst){
        this.arrival = arrival;
        this.compBurst = compBurst;
        this.totalCompTime = totalCompTime;
        this.IOBurst = IOBurst;
        
        this.remainingCompTime = totalCompTime;
        this.totalIO = 0;
        this.timeWaiting = 0;
        this.status = "unstarted";
        this.first = true;
        
    }    

    @Override
    public int compareTo(Process process){
        return this.arrival - process.getArrival();
    }
    
    public boolean isBlocked(){
        if(status == "blocked"){
            return true;
        }else{
            return false;
        }
    }
    public boolean isUnstarted(){
        if(status == "unstarted"){
            return true;
        }else{
            return false;
        }
    }   
    public boolean isReady(){
        if(status == "ready"){
            return true;
        }else{
            return false;
        }
    }
    public boolean isRunning(){
        if(status == "running"){
            return true;
        }else{
            return false;
        }
    }
    public boolean isTerminated(){
        if(status == "terminated"){
            return true;
        }else{
            return false;
        }
    }
            
    public void setBlocked(int IOBurstTime){//set the status to blocked given an IO burst time
        status = "blocked";
        remainingBurst = IOBurstTime;
        first = true;
        
    }
    public void setTerminated(int finishTime){//sets the status to terminated when all of the bursts are finished
        status = "terminated";
        timeFinished = finishTime;
        remainingBurst = 0;
    }
    public void setRunning(int burst){
        status = "running";
        previousBurst = burst;
        remainingBurst = burst;
        if(first == true){
            firstBurst = burst;
            first = false;
        }
    }
    public void setReady(int readyTime){
        status = "ready";
        timeBecameReady = readyTime;
    }
    public void setRemainingTime(int remainingTime){
        remainingCompTime = remainingTime;
    }
    
     public void setPID(int val){
        this.PID = val;
    }
    
    public int getArrival(){
        return this.arrival;
    }
    
    public int getCompBurst(){
        return this.compBurst;
    }
    
    public int getTotalCompTime(){
        return this.totalCompTime;
    }
    
    public int getRemainingCompTime(){
        return this.remainingCompTime;
    }
    
    public int getTimeReady(){
        return this.timeBecameReady;
    }
    
    public int getRemainingBurst(){
        return this.remainingBurst;
    }
    
    public int getIOBurst(){
        return this.IOBurst;
    }
    
    public int getTimeFinished(){
        return this.timeFinished;
    }
    
    public String getStatus(){
        return this.status;
    }
    
    public int getPID(){
        return this.PID;
    }
    
    public int getTurnaroundTime(){
        return this.timeFinished - this.arrival;
    }
    
    public int getWaitingTime(){
        return this.timeWaiting;
    }
    public int getTotalIOTime(){
        return this.totalIO;
    }
    
    public int getPreviousBurst(){
        return this.previousBurst;
    }
    
    public int getFirstBurst(){
        return this.firstBurst;
    }
    
    public boolean hasBurstLeft(){
        return this.remainingBurst != 0;
    }
    
    public boolean hasCompTimeLeft(){
        return this.remainingCompTime != 0;
    }
    //changes the designated values for a process that spends the round blocked
    public void blockedRound(){
        remainingBurst--;
        totalIO++;
    }
    //changes the designated values for a process that spends the round ready
    public void readyRound(){
        timeWaiting++;
    }
    //changes the designated values for a process that spends the round running
    public void runningRound(){
        remainingBurst--;
        remainingCompTime--;
    }
    
}
