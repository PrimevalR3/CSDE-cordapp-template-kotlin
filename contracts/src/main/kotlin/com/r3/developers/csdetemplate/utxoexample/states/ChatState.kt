package com.r3.developers.csdetemplate.utxoexample.states

import com.r3.developers.csdetemplate.utxoexample.contracts.IOUContract
import net.corda.v5.base.types.MemberX500Name
import net.corda.v5.ledger.utxo.BelongsToContract
import net.corda.v5.ledger.utxo.ContractState
import java.security.PublicKey
import java.util.*


// The ChatState represents data stored on ledger. A chat consists of a linear series of messages between two
// participants and is represented by a UUID. Any given pair of participants can have multiple chats
// Each ChatState stores one message between the two participants in the chat. The backchain of ChatStates
// represents the history of the chat.

@BelongsToContract(IOUContract::class)
data class IOUState (

    //private variables
    val amount: Int,
    val lender: MemberX500Name,
    val borrower: MemberX500Name,
    val paid: Int,
    val linearId: UUID,
    private val participants: List<PublicKey>
) : ContractState {

    //Helper method for settle flow
    fun pay(amountToPay: Int) : IOUState {
        return IOUState(amount,lender,borrower,paid+amountToPay,linearId,participants)
    }

    override fun getParticipants(): List<PublicKey> {
        return participants
    }
}

