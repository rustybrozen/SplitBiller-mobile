package com.example.anhpansplitter.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.anhpansplitter.model.AppMode
import com.example.anhpansplitter.model.AppSettings
import com.example.anhpansplitter.model.AppTab
import com.example.anhpansplitter.model.Bill
import com.example.anhpansplitter.model.DebtRecord
import com.example.anhpansplitter.model.GroupedTransfer
import com.example.anhpansplitter.model.Member
import com.example.anhpansplitter.model.Transfer
import com.example.anhpansplitter.util.BillCalculator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class MainViewModel : ViewModel() {

    var currentMode by mutableStateOf(AppMode.SPLIT_BILLER)
        private set

    var currentTab by mutableStateOf(AppTab.MEMBERS)
        private set

    var members by mutableStateOf<List<Member>>(emptyList())
        private set

    var bills by mutableStateOf<List<Bill>>(emptyList())
        private set

    var debts by mutableStateOf<List<DebtRecord>>(emptyList())
        private set

    var settings by mutableStateOf(AppSettings())
        private set

    init {
        // Pre-populate demo seed data if needed
        val m1 = Member(1L, "Anh")
        val m2 = Member(2L, "Binh")
        val m3 = Member(3L, "Cuong")
        members = listOf(m1, m2, m3)
    }

    fun setMode(mode: AppMode) {
        currentMode = mode
    }

    fun setTab(tab: AppTab) {
        currentTab = tab
    }

    fun updateSettings(newSettings: AppSettings) {
        settings = newSettings
    }



    fun addMember(name: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.isBlank() || members.any { it.name.equals(trimmed, ignoreCase = true) }) {
            return false
        }
        val newMember = Member(id = System.currentTimeMillis(), name = trimmed)
        members = members + newMember
        return true
    }



    fun removeMember(id: Long): Boolean {
        val inOpenBill = bills.any {
            it.status == "open" && (it.payer == id || it.selectedMembers.contains(id) || it.extraSplits.any { ex -> ex.members.contains(id) })
        }
        val inActiveDebt = debts.any { it.status == "active" && it.memberId == id }

        if (inOpenBill || inActiveDebt) {
            return false
        }

        members = members.filter { it.id != id }
        return true
    }



    fun saveBill(bill: Bill) {
        val existingIndex = bills.indexOfFirst { it.id == bill.id }
        bills = if (existingIndex >= 0) {
            bills.toMutableList().apply { set(existingIndex, bill) }
        } else {
            bills + bill
        }
    }



    fun deleteBill(id: Long) {
        bills = bills.filter { it.id != id }
    }



    fun settleAllBills() {
        val batch = System.currentTimeMillis()
        val snapshot = members.associate { it.id to it.name }
        bills = bills.map {
            if (it.status == "open") it.copy(status = "closed", batchId = batch, memberSnapshot = snapshot) else it
        }
    }



    fun addDebt(amount: Double, memberName: String, type: String, note: String): Boolean {
        val trimmedName = memberName.trim()
        if (trimmedName.isBlank() || amount <= 0.0) return false

        var targetMember = members.find { it.name.equals(trimmedName, ignoreCase = true) }
        if (targetMember == null) {
            targetMember = Member(System.currentTimeMillis(), trimmedName)
            members = members + targetMember
        }

        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val record = DebtRecord(
            id = System.currentTimeMillis(),
            memberId = targetMember.id,
            amount = amount,
            type = type,
            note = note.trim(),
            date = dateStr,
            status = "active",
            memberNameSnapshot = targetMember.name
        )

        debts = listOf(record) + debts
        return true
    }



    fun settleDebt(id: Long) {
        debts = debts.map { if (it.id == id) it.copy(status = "settled") else it }
    }



    fun deleteDebt(id: Long) {
        debts = debts.filter { it.id != id }
    }



    val openBillsCount: Int
        get() = bills.count { it.status == "open" }

    val calculateTransfers: List<Transfer>
        get() = BillCalculator.calculateTransfers(
            bills = bills,
            members = members,
            roundingMode = settings.roundingMode,
            simplifyDebts = settings.simplifyDebts
        )

    val groupedTransfers: List<GroupedTransfer>
        get() = BillCalculator.groupTransfers(calculateTransfers, members)
}
