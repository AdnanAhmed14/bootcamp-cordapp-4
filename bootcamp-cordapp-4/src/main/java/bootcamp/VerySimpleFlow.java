package bootcamp;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;

@InitiatingFlow
@StartableByRPC
public class VerySimpleFlow extends FlowLogic<Integer> {
    @Suspendable
    public Integer call() throws FlowException {
        int a=callOne();
        int b=callTwo();
        return a+b;
    }
    public Integer callOne(){
        return 4;
    }
    public Integer callTwo(){
        return 5;
    }
}
