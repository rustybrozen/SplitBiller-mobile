package com.example.anhpansplitter.model



data class Member(
    val id: Long,
    val name: String
)



enum class SplitType {
    EQUAL,
    ADVANCED
}



data class ExtraSplit(
    val id: Long,
    val name: String,
    val amount: Double,
    val members: List<Long>
)



data class Bill(
    val id: Long,
    val amount: Double,
    val description: String,
    val payer: Long,
    val date: String,
    val splitType: SplitType = SplitType.EQUAL,
    val selectedMembers: List<Long> = emptyList(),
    val extraSplits: List<ExtraSplit> = emptyList(),
    val status: String = "open",
    val batchId: Long? = null,
    val equalAdjustments: Map<Long, Double> = emptyMap(),
    val shares: Map<Long, Double> = emptyMap(),
    val memberSnapshot: Map<Long, String> = emptyMap()
)



data class Transfer(
    val from: Long,
    val to: Long,
    val amount: Double
)



data class SenderDetail(
    val name: String,
    val amount: Double
)



data class GroupedTransfer(
    val receiverId: Long,
    val receiverName: String,
    val totalReceive: Double,
    val senders: List<SenderDetail>
)



data class AppSettings(
    val roundingMode: String = "none",
    val simplifyDebts: Boolean = true,
    val defaultApp: String = "split",
    val myId: Long? = null,
    val bankBin: String = "",
    val bankAccountNo: String = "",
    val bankAccountName: String = ""
)



data class DebtRecord(
    val id: Long,
    val memberId: Long,
    val amount: Double,
    val type: String,
    val note: String,
    val date: String,
    val status: String = "active",
    val memberNameSnapshot: String = ""
)



enum class AppTab {
    MEMBERS,
    BILLS,
    SUMMARY,
    HISTORY,
    SETTINGS
}



enum class AppMode {
    SPLIT_BILLER,
    DEBT_BOOK
}
