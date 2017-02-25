/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package linker;
import java.util.*;

/**
 *
 * @author meganmoore
 */
public class Module{
	public int base;
    public HashMap <Integer, Character> instruct = new HashMap<>();
    public HashMap <Integer, Boolean> flagRefactor = new HashMap<>(); //flag true if char leads to refactoring
    public HashMap <Integer, Integer> opcode = new HashMap<>();
    public HashMap <Integer, Integer> address = new HashMap<>();
    public ArrayList<String> uses = new ArrayList<String>();
	
    public Module (int base){
        this.base = base;
        this.instruct = new HashMap<>();
        this.flagRefactor = new HashMap<>();
        this.opcode = new HashMap<>();
        this.address = new HashMap<>();
        this.uses = new ArrayList<String>();
	}
    public ArrayList<String> setUses(){
        for(int i=0; i<30; i++){
            this.uses.add("");
        }
        return this.uses;
    }
        
    public void setBase(){
        this.base = base;
    }
        
    public int getBase(){
        return this.base;
    }
        

	
        
        


}