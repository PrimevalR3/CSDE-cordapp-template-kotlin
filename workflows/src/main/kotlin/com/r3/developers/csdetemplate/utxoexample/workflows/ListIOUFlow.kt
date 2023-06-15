package com.r3.developers.csdetemplate.utxoexample.workflows

import com.r3.developers.csdetemplate.utxoexample.states.IOUState
import net.corda.v5.application.flows.ClientRequestBody
import net.corda.v5.application.flows.ClientStartableFlow
import net.corda.v5.application.flows.CordaInject
import net.corda.v5.application.marshalling.JsonMarshallingService
import net.corda.v5.base.annotations.Suspendable
import net.corda.v5.ledger.utxo.UtxoLedgerService
import org.slf4j.LoggerFactory
import java.util.*

// A class to hold the deserialized arguments required to start the flow.

data class ListIOUFlowFin(val id: UUID,val amount: Int,val borrower: String,val lender: String,val paid: Int)

// See Chat CorDapp Design section of the getting started docs for a description of this flow.
class ListIOUFlow: ClientStartableFlow {

    private companion object {
        val log = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }

    @CordaInject
    lateinit var jsonMarshallingService: JsonMarshallingService

    // Injects the UtxoLedgerService to enable the flow to make use of the Ledger API.
    @CordaInject
    lateinit var ledgerService: UtxoLedgerService

    @Suspendable
    override fun call(requestBody: ClientRequestBody): String {

        log.info("ListIOUFlow.call() called")

        // Obtain the deserialized input arguments to the flow from the requestBody.

        // Look up the latest unconsumed ChatState with the given id.
        // Note, this code brings all unconsumed states back, then filters them.
        // This is an inefficient way to perform this operation when there are a large number of chats.
        // Note, you will get this error if you input an id which has no corresponding ChatState (common error).
        val states = ledgerService.findUnconsumedStatesByType(IOUState::class.java)
        val result = states.map { stateAndRef ->
            ListIOUFlowFin(
                stateAndRef.state.contractState.linearId,
                stateAndRef.state.contractState.amount,
                stateAndRef.state.contractState.borrower.toString(),
                stateAndRef.state.contractState.lender.toString(),
                stateAndRef.state.contractState.paid,
            )
        }

        return jsonMarshallingService.format(result)
    }

    // resoveMessageFromBackchain() starts at the stateAndRef provided, which represents the unconsumed head of the
    // backchain for this particular chat, then walks the chain backwards for the number of transaction specified in
    // the numberOfRecords argument. For each transaction it adds the MessageAndSender representing the
    // message and who sent it to a list which is then returned.

}

/*
RequestBody for triggering the flow via REST:
{
    "clientRequestId": "get-1",
    "flowClassName": "com.r3.developers.csdetemplate.utxoexample.workflows.GetChatFlow",
    "requestBody": {
        "id":"** fill in id **",
        "numberOfRecords":"4"
    }
}
 */