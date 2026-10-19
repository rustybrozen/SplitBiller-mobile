package com.example.anhpansplitter.util

import com.example.anhpansplitter.model.Bill
import com.example.anhpansplitter.model.GroupedTransfer
import com.example.anhpansplitter.model.Member
import com.example.anhpansplitter.model.SenderDetail
import com.example.anhpansplitter.model.SplitType
import com.example.anhpansplitter.model.Transfer
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min



object BillCalculator {

    fun smartRound(amount: Double, mode: String): Double {
        if (mode != "smart") return amount
        val remainder = amount % 1000.0
        if (remainder == 500.0) return amount
        return if (remainder < 500.0) {
            floor(amount / 1000.0) * 1000.0
        } else {
            ceil(amount / 1000.0) * 1000.0
        }
    }



    fun calculateTransfers(
        bills: List<Bill>,
        members: List<Member>,
        roundingMode: String,
        simplifyDebts: Boolean
    ): List<Transfer> {
        val openBills = bills.filter { it.status == "open" }
        if (openBills.isEmpty()) return emptyList()

        if (simplifyDebts) {
            val balances = mutableMapOf<Long, Double>()
            members.forEach { balances[it.id] = 0.0 }

            openBills.forEach { bill ->
                val payer = bill.payer
                val totalAmount = bill.amount
                val shares = mutableMapOf<Long, Double>()

                if (bill.splitType == SplitType.EQUAL) {
                    val selected = if (bill.selectedMembers.isNotEmpty()) {
                        bill.selectedMembers
                    } else {
                        members.map { it.id }
                    }

                    var totalSharesWeight = 0.0
                    selected.forEach { id ->
                        val weight = bill.shares[id] ?: 1.0
                        totalSharesWeight += weight
                    }
                    if (totalSharesWeight <= 0.0) {
                        totalSharesWeight = selected.size.toDouble().coerceAtLeast(1.0)
                    }

                    val baseShares = mutableMapOf<Long, Double>()
                    selected.forEach { id ->
                        val weight = bill.shares[id] ?: 1.0
                        baseShares[id] = (totalAmount * weight) / totalSharesWeight
                    }

                    var totalAdjustments = 0.0
                    val unadjustedMembers = mutableListOf<Long>()

                    selected.forEach { id ->
                        val adj = bill.equalAdjustments[id] ?: 0.0
                        if (adj == 0.0) {
                            unadjustedMembers.add(id)
                        } else {
                            totalAdjustments += adj
                        }
                    }

                    val spreadCount = if (unadjustedMembers.isNotEmpty()) {
                        unadjustedMembers.size
                    } else {
                        selected.size
                    }
                    val spreadAmount = if (spreadCount > 0) -totalAdjustments / spreadCount else 0.0

                    selected.forEach { id ->
                        val adj = bill.equalAdjustments[id] ?: 0.0
                        val base = baseShares[id] ?: 0.0
                        shares[id] = if (adj != 0.0) {
                            base + adj + if (unadjustedMembers.isEmpty()) spreadAmount else 0.0
                        } else {
                            base + spreadAmount
                        }
                    }
                } else {
                    var allocatedAmount = 0.0
                    bill.extraSplits.forEach { split ->
                        val count = split.members.size.coerceAtLeast(1)
                        val splitShare = split.amount / count
                        split.members.forEach { id ->
                            shares[id] = (shares[id] ?: 0.0) + splitShare
                        }
                        allocatedAmount += split.amount
                    }

                    val remaining = totalAmount - allocatedAmount
                    if (remaining > 0.0) {
                        val poolToSplit = if (bill.selectedMembers.isNotEmpty()) {
                            bill.selectedMembers
                        } else {
                            members.map { it.id }
                        }
                        val count = poolToSplit.size.coerceAtLeast(1)
                        val share = remaining / count
                        poolToSplit.forEach { id ->
                            shares[id] = (shares[id] ?: 0.0) + share
                        }
                    }
                }

                balances[payer] = (balances[payer] ?: 0.0) + totalAmount
                shares.forEach { (memberId, amount) ->
                    balances[memberId] = (balances[memberId] ?: 0.0) - amount
                }
            }

            class ParticipantBalance(val id: Long, var amount: Double)

            val debtors = mutableListOf<ParticipantBalance>()
            val creditors = mutableListOf<ParticipantBalance>()

            balances.forEach { (id, amount) ->
                if (amount < -1.0) debtors.add(ParticipantBalance(id, amount))
                if (amount > 1.0) creditors.add(ParticipantBalance(id, amount))
            }

            debtors.sortBy { it.amount }
            creditors.sortByDescending { it.amount }

            val transfers = mutableListOf<Transfer>()
            var i = 0
            var j = 0

            while (i < debtors.size && j < creditors.size) {
                val debtor = debtors[i]
                val creditor = creditors[j]
                val matchAmount = min(abs(debtor.amount), creditor.amount)

                if (matchAmount > 0.0) {
                    val finalAmount = smartRound(matchAmount, roundingMode)
                    if (finalAmount > 0.0) {
                        transfers.add(Transfer(debtor.id, creditor.id, finalAmount))
                    }
                }

                debtor.amount += matchAmount
                creditor.amount -= matchAmount
                if (abs(debtor.amount) < 1.0) i++
                if (creditor.amount < 1.0) j++
            }

            return transfers
        } else {
            val debtsMap = mutableMapOf<Long, MutableMap<Long, Double>>()

            fun addDebt(from: Long, to: Long, amount: Double) {
                if (from == to || amount <= 0.0) return
                val target = debtsMap.getOrPut(from) { mutableMapOf() }
                target[to] = (target[to] ?: 0.0) + amount
            }

            openBills.forEach { bill ->
                val payer = bill.payer
                if (bill.splitType == SplitType.EQUAL) {
                    val selected = if (bill.selectedMembers.isNotEmpty()) bill.selectedMembers else members.map { it.id }
                    var totalWeight = 0.0
                    selected.forEach { totalWeight += (bill.shares[it] ?: 1.0) }
                    if (totalWeight <= 0.0) totalWeight = selected.size.toDouble().coerceAtLeast(1.0)

                    val baseShare = bill.amount / totalWeight
                    selected.forEach { memberId ->
                        val weight = bill.shares[memberId] ?: 1.0
                        addDebt(memberId, payer, baseShare * weight)
                    }
                } else {
                    var allocated = 0.0
                    bill.extraSplits.forEach { split ->
                        val count = split.members.size.coerceAtLeast(1)
                        val share = split.amount / count
                        split.members.forEach { mId -> addDebt(mId, payer, share) }
                        allocated += split.amount
                    }
                    val remaining = bill.amount - allocated
                    if (remaining > 0.0) {
                        val pool = if (bill.selectedMembers.isNotEmpty()) bill.selectedMembers else members.map { it.id }
                        val share = remaining / pool.size.coerceAtLeast(1)
                        pool.forEach { addDebt(it, payer, share) }
                    }
                }
            }

            val transfers = mutableListOf<Transfer>()
            debtsMap.forEach { (fromId, targets) ->
                targets.forEach { (toId, amount) ->
                    val reverseDebt = debtsMap[toId]?.get(fromId) ?: 0.0
                    if (amount > reverseDebt) {
                        val finalAmt = smartRound(amount - reverseDebt, roundingMode)
                        if (finalAmt > 0.0) {
                            transfers.add(Transfer(fromId, toId, finalAmt))
                        }
                    }
                }
            }
            return transfers
        }
    }



    fun groupTransfers(transfers: List<Transfer>, members: List<Member>): List<GroupedTransfer> {
        val groups = mutableMapOf<Long, MutableList<SenderDetail>>()

        transfers.forEach { t ->
            val senderName = members.find { it.id == t.from }?.name ?: "Nguoi dung"
            groups.getOrPut(t.to) { mutableListOf() }.add(SenderDetail(senderName, t.amount))
        }

        return groups.map { (receiverId, senders) ->
            val receiverName = members.find { it.id == receiverId }?.name ?: "Nguoi nhan"
            val total = senders.sumOf { it.amount }
            GroupedTransfer(receiverId, receiverName, total, senders)
        }
    }
}
