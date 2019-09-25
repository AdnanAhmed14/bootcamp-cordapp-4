package bootcamp;

import net.corda.core.contracts.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
@BelongsToContract(TokenContract.class)
public class TokenContract implements Contract {
    public static String ID = "bootcamp.TokenContract";
    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getInputStates().size() !=0)
            throw new IllegalArgumentException("Transaction must have zero input");
        if(tx.getOutputStates().size() != 1)
            throw  new IllegalArgumentException("Transaction must have one input");
        if (tx.getCommands().size() != 1)
            throw new IllegalArgumentException("Must Have one command.");

        ContractState output = tx.getOutput(0);
        Command command = tx.getCommand(0);

        if(!(output instanceof TokenState))
            throw new IllegalArgumentException("Output must be a token state");
        if(!(command.getValue() instanceof TokenContract.Commands.Issue))
            throw  new IllegalArgumentException("Command must be issue command.");

        TokenState token=(TokenState) output;
        if (token.getAmount()<0)
            throw new IllegalArgumentException("Token amount must be positive");

        List<PublicKey> requiredSigners= command.getSigners();
        Party issuer = token.getIssuer();
        PublicKey issuerKey= issuer.getOwningKey();
        if (!(requiredSigners.contains(issuerKey)))
            throw new IllegalArgumentException("Issuer must be required signer.");

    }


    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }
}