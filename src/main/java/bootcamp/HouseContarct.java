package bootcamp;

import afu.org.checkerframework.checker.igj.qual.I;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

public class HouseContarct implements Contract {

    public void verify( LedgerTransaction tx) throws IllegalArgumentException
    {
        if(tx.getCommands().size()!= 1)
            throw new IllegalArgumentException("Transaction must have one command");
        Command command=tx.getCommand(0);
        List<PublicKey> requiredSingers = command.getSigners();
        CommandData commandType = command.getValue();
        if(commandType instanceof Register){
            //Shape constraints.
             if(tx.getInputStates().size() != 0)
                 throw new IllegalArgumentException("Registration transaction must have no inputs ");
             if(tx.getOutputStates().size()!=1)
                 throw new IllegalArgumentException("Registration transaction must have one output");
             //Content Constraints.
            ContractState outputState= tx.getOutput(0);
            if(!(outputState instanceof HouseState))
                throw new IllegalArgumentException("Output must be HouseState");
            HouseState houseState=(HouseState) outputState;
            if(houseState.getAddress().length() <= 3)
                throw  new IllegalArgumentException(" Address must be longer then 3 character.");
            if (houseState.getOwner().getName().getCountry().equals("India"))
                throw  new IllegalArgumentException("Not allowed to register for Indian owners.");
            //Required signer constraints
            Party owner = houseState.getOwner();
            PublicKey ownerKey = owner.getOwningKey();
            if(!(requiredSingers.contains(ownerKey)))
                throw new IllegalArgumentException("Owner of house must sign registration.");

        }else if(commandType instanceof Transfer){
            //Shape constraints.
            if (tx.getInputStates().size() !=1)
                throw new IllegalArgumentException("Must have one input");

            if (tx.getOutputStates().size() !=1)
                throw new IllegalArgumentException("Must have one output");
            //Contents constraints.
            ContractState input=tx.getInput(0);
            ContractState output=tx.getOutput(0);
            if (!(input instanceof HouseState))
                throw new IllegalArgumentException("Input must be a HouseState.");
            if (!(output instanceof HouseState))
                throw new IllegalArgumentException("Output must be a HouseState.");
            HouseState inputHouse=(HouseState) input;
            HouseState outputHouse=(HouseState) output;

            if(!(inputHouse.getAddress().equals(outputHouse.getAddress())))
                throw new IllegalArgumentException("In a transfer address can't change");
            if(inputHouse.getOwner().equals(outputHouse.getOwner()))
                throw new IllegalArgumentException("In a transfer, owner must change");
            // Signer constraints.
            Party inputOwner= inputHouse.getOwner();
            Party outputOwner= outputHouse.getOwner();

            if (!(requiredSingers.contains(inputOwner.getOwningKey())))
                throw new IllegalArgumentException("Current owner must sign transfer.");
            if (!(requiredSingers.contains(outputOwner.getOwningKey())))
                throw new IllegalArgumentException("New owner must sign transfer.");

        }else {
            throw new IllegalArgumentException("Command type not recognised.");
        }

    }
    public static class Register implements CommandData{}
    public static class Transfer implements CommandData{}
}
