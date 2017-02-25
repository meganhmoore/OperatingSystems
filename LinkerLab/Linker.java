/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package linker;

/**
 *
 * @author meganmoore
 * Linker Lab
 * Operating Systems, Spring 2017
 */


import java.util.*;

public class Linker{

    public static ArrayList<String> errorList = new ArrayList<String>();

    public static void main (String[] args){
            Scanner scan = new Scanner(System.in);
            
            HashMap<String, Integer> symbolDefs = new HashMap<>();
            ArrayList<String> definitions = new ArrayList<>();
            ArrayList<Integer> modLengths = new ArrayList<>();
            ArrayList<Module> module = new ArrayList<>();
            HashMap<String, Boolean> symbolUsed = new HashMap<>();
            HashMap<String, Integer> symbolMap = new HashMap<>();
            ArrayList<Integer> lengths = new ArrayList<>();

            
            int numDefs;
            int numUse;
            int numProg;
            
            int baseAddress = 0;
            
            String symbolName;
            int symbolDef;
            int refactor;    
            
            int instructionUse;
            
            int tempAddress;
            int opcode;
            int address;  
                    
            
            Module mod = new Module(baseAddress);
            HashMap<String, Integer>tempDefinitions; 
            
            int numMods = scan.nextInt();
            for(int m=0; m<numMods; m++){
                module.add(mod);
            }
            for(int i=0; i<numMods; i++){
                tempDefinitions = new HashMap<>();
                modLengths.add(baseAddress);//base address for each module
                mod = new Module(baseAddress);

                mod.setUses();

                numDefs = scan.nextInt();//scan for definitions and put them into the symbol table
                for(int j=0; j<numDefs; j++){
                    symbolName = scan.next();
                    symbolDef = scan.nextInt();
                    if(symbolDefs.get(symbolName) != null){//check if symbol multiply defined
                        System.out.println("Error: this symbol was defined multiple times. The first definition has been used");
                        errorList.add("Error: this symbol was defined multiple times. The first definition has been used");
                    }else{
                        symbolDefs.put(symbolName, symbolDef + baseAddress);
                        tempDefinitions.put(symbolName, symbolDef);
                        definitions.add(symbolName);
                        symbolUsed.put(symbolName, false);
                    }
                }

                int negate = -1; 
                numUse = scan.nextInt();//scan uses

                for(int k=0; k<numUse; k++){
                    symbolName = scan.next();
                    instructionUse = scan.nextInt();
                    mod.uses.add(instructionUse, symbolName);
                    while(instructionUse != negate){
                        mod.uses.add(instructionUse, symbolName);
                        instructionUse = scan.nextInt();
                    }
                    symbolUsed.put(symbolName, true);
                }

                numProg = scan.nextInt();

                lengths.add(numProg);

                int tempSize = definitions.size();
                for(int r=0; r<tempSize; r++){//checking if a definition exceeds the size of the module
                    String tempString = definitions.get(r);
                    int tempInt = symbolDefs.get(tempString);
                    if((tempInt-baseAddress)>numProg){
                        System.out.print("Error: the definition address for " +tempString+" exceeds the size of the module, definition set to 0 (relative)");
                        errorList.add("Error: the definition address for exceeds the size of the module, definition set to 0 (relative)");
                        symbolDefs.put(tempString, baseAddress);
                    }
                }
                
                
                
                
                for(int l=0; l<numProg; l++){
                    String tempOperation = scan.next();
                    Character operation = tempOperation.charAt(0);
                    mod.instruct.put(l, operation);
                    if(operation == 'R'|| operation =='E'){
                        mod.flagRefactor.put(l, Boolean.TRUE);
                    }else{
                        mod.flagRefactor.put(l, Boolean.FALSE);
                    }
                    tempAddress = scan.nextInt();
                    opcode = tempAddress/1000;
                    address = tempAddress%1000;
                    mod.opcode.put(l, opcode);
                    mod.address.put(l, address);
                   
                }
                
                module.add(i, mod);
                baseAddress+= numProg;
            }
            


            System.out.println("Symbol Table");
            for(int i=0; i<definitions.size(); i++){
                String tempSymbolName = definitions.get(i);
                System.out.print(tempSymbolName+" : ");

                int tempSymbolDef = symbolDefs.get(definitions.get(i));
                if(tempSymbolDef == -1){//making sure that the symbols were not defined multiple times
                    System.out.print("Error: The symbol " +tempSymbolName + " was not defined, definition set to zero");
                    errorList.add("Error: The symbol " +tempSymbolName+" was not defined, definition set to zero");
                    symbolDefs.put(tempSymbolName, 0);
                }
                if(symbolUsed.get(tempSymbolName) == false){//making sure all symbols were used
                    System.out.print("Error: The symbol" +tempSymbolName+" was declared but not used");
                    errorList.add("Error: The symbol "+tempSymbolName+" was declared but not used");
                }
                symbolMap.put(tempSymbolName, tempSymbolDef);
                System.out.println(tempSymbolDef);
            }

            int numDefinitions;
            numDefinitions = definitions.size();
            for (int v=0; v<numDefinitions; v++){
                String definitionCheck = definitions.get(v);
                System.out.print(definitionCheck);
                System.out.println(symbolMap.get(definitionCheck));
            }

            
            int numInstructs;
            Module tempMod;
            char tempChar;
            int tempOp;
            int tempAdd;
            int newTemp;
            int tempDef;
            int defSize = definitions.size();
            
            int newAddress;
            int addBase;
            
            String tempSymbol;
            int tempSymbolAddress;
            int tempExternalNum;

            boolean alreadyHasSymbol = false;

            String tempDefName; 



            

            
            System.out.println("Memory Map");
            for(int m=0; m<numMods; m++){

                System.out.println(modLengths.get(m));

                tempMod = module.get(m);

                numInstructs = tempMod.address.size();

                for(int n=0; n<numInstructs; n++){
                    System.out.print(n+" ");

                    alreadyHasSymbol = false;

                    tempDef = (tempMod.getBase()+n);
                    for(int p=0; p<defSize; p++){
                        tempDefName = definitions.get(p);
                        if(symbolMap.get(tempDefName) == tempDef){//if there is a definition at this instruction
                            System.out.print(definitions.get(p)+"  ");
                        }else{
                            System.out.print("    ");
                        }
                    }
                    
                    tempChar = tempMod.instruct.get(n);
                    tempOp = tempMod.opcode.get(n);
                    tempAdd = tempMod.address.get(n);
                    newTemp = (tempOp*1000)+tempAdd;

                    
                    System.out.print(tempChar + " " + newTemp);
                    if(tempChar == 'R'){
                        addBase = modLengths.get(m);
                        if(tempAdd>lengths.get(m)){
                            tempAdd=0;
                            errorList.add("Error: this exceeds the module size, it has been reset to 0");
                        }

                        newAddress = (tempOp*1000)+(tempAdd+addBase);
                        System.out.println("        " +newAddress );

                    }else if (tempChar == 'E'){
                        tempSymbol = tempMod.uses.get(n);

                        if(symbolMap.containsKey(tempSymbol) == false){
                            errorList.add("Error: problem with finding symbol Address for this symbol");
                            symbolMap.put(tempSymbol,0);
                        }

                        if(alreadyHasSymbol == true){
                            errorList.add("Error: one symbol has already been used in this instruction, symbol ignored");
                        }

                        if(tempSymbol == "" || tempSymbol == " "){//if the element doesnt exist
                            errorList.add("Error: the symbol does not exist");
                        }
                        
                        tempSymbolAddress = symbolMap.get(tempSymbol);
                        tempExternalNum = (tempOp*1000)+(tempSymbolAddress);
                        System.out.println(" ->"+tempSymbol+"   "+tempExternalNum);
                        alreadyHasSymbol = true;
                        
                    }else if (tempChar == 'I'){
                        newTemp = (tempOp*1000)+tempAdd;
                        System.out.println("        "+ newTemp);

                    }else if (tempChar == 'A'){
                        if (tempAdd > 200){
                            tempAdd = 0;
                            //System.out.print("Error: The absolute address is larger than the machine size. Address set to 0");
                            errorList.add("Error: The absolute address is larger than the machine size. Address set to 0");
                        }
                        newTemp = (tempOp*1000)+tempAdd;
                        System.out.println("        "+ newTemp);
                    }
                    else{
                        System.out.print("There was no instruction type declared");
                        errorList.add("There was no instruction type declared");
                    }
                    
                }
            }
        int errorListSize = errorList.size();
        for(int z=0; z<errorListSize; z++){
            System.out.println(errorList.get(z));
        }   
                
    }
}



