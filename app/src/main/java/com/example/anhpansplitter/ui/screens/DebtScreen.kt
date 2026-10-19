package com.example.anhpansplitter.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anhpansplitter.model.AppSettings
import com.example.anhpansplitter.model.DebtRecord
import com.example.anhpansplitter.model.Member
import com.example.anhpansplitter.ui.theme.BrandAmber
import com.example.anhpansplitter.ui.theme.BrandBlue
import com.example.anhpansplitter.ui.theme.BrandDark
import com.example.anhpansplitter.ui.theme.BrandRed
import com.example.anhpansplitter.util.MoneyUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



enum class DebtSubTab {
    INPUT,
    PAY,
    RECEIVE,
    HISTORY,
    SETTINGS
}




@Composable
fun DebtScreen(
    debts: List<DebtRecord>,
    members: List<Member>,
    settings: AppSettings,
    onUpdateSettings: (AppSettings) -> Unit,
    onAddDebt: (Double, String, String, String) -> Boolean,
    onSettleDebt: (Long) -> Unit,
    onDeleteDebt: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(DebtSubTab.INPUT) }
    var debtAmountInput by remember { mutableStateOf("") }
    var debtMemberName by remember { mutableStateOf("") }
    var debtType by remember { mutableStateOf("lend") } // "borrow" or "lend"
    var debtNote by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val activeBorrowDebts = debts.filter { it.type == "borrow" && it.status == "active" }
    val activeLendDebts = debts.filter { it.type == "lend" && it.status == "active" }
    val settledDebts = debts.filter { it.status == "settled" }

    val totalBorrow = activeBorrowDebts.sumOf { it.amount }
    val totalLend = activeLendDebts.sumOf { it.amount }

    val parsedAmount = MoneyUtil.parseSmartMoney(debtAmountInput)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Balance Overview Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "TỔNG CẦN TRẢ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandRed,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = MoneyUtil.formatCurrency(totalBorrow),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandRed
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "TỔNG CẦN THU",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = MoneyUtil.formatCurrency(totalLend),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Subtabs Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
        ) {
            val tabs = listOf(
                DebtSubTab.INPUT to "Ghi nợ",
                DebtSubTab.PAY to "Cần trả (${activeBorrowDebts.size})",
                DebtSubTab.RECEIVE to "Cần thu (${activeLendDebts.size})",
                DebtSubTab.HISTORY to "Lịch sử",
                DebtSubTab.SETTINGS to "Cài đặt"
            )

            tabs.forEach { (tab, title) ->
                val isSelected = activeSubTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isSelected) BrandBlue else Color.Transparent)
                        .clickable { activeSubTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Content
        when (activeSubTab) {
            DebtSubTab.INPUT -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        // Type Selection: Borrow vs Lend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { debtType = "borrow" },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(0.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (debtType == "borrow") BrandRed else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = "ĐI VAY (TÔI NỢ)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (debtType == "borrow") Color.White else Color.Gray
                                )
                            }

                            Button(
                                onClick = { debtType = "lend" },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(0.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (debtType == "lend") BrandBlue else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = "CHO VAY (HỌ NỢ TÔI)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (debtType == "lend") Color.White else Color.Gray
                                )
                            }
                        }
                    }

                    item {
                        // Amount Input
                        Column {
                            OutlinedTextField(
                                value = debtAmountInput,
                                onValueChange = {
                                    debtAmountInput = it
                                    errorMessage = null
                                },
                                label = { Text("Số tiền (VD: 50k, 1.5tr, 500000)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(0.dp),
                                singleLine = true
                            )

                            if (parsedAmount > 0.0) {
                                Text(
                                    text = "= " + MoneyUtil.formatCurrency(parsedAmount),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBlue,
                                    modifier = Modifier.padding(top = 2.dp, start = 4.dp)
                                )
                            }

                            // Quick preset chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("50k", "100k", "200k", "500k", "1tr").forEach { chip ->
                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable { debtAmountInput = chip }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = chip, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Member / Counterpart Name
                        Column {
                            OutlinedTextField(
                                value = debtMemberName,
                                onValueChange = {
                                    debtMemberName = it
                                    errorMessage = null
                                },
                                label = { Text("Người dính nợ (tên)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(0.dp),
                                singleLine = true
                            )

                            if (members.isNotEmpty()) {
                                Text(
                                    text = "Gợi ý nhanh:",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )

                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(members) { m ->
                                        Box(
                                            modifier = Modifier
                                                .border(1.dp, BrandBlue.copy(alpha = 0.5f), RoundedCornerShape(0.dp))
                                                .background(MaterialTheme.colorScheme.surface)
                                                .clickable { debtMemberName = m.name }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = m.name,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandBlue
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = debtNote,
                            onValueChange = { debtNote = it },
                            label = { Text("Ghi chú (tùy chọn)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(0.dp),
                            singleLine = true
                        )
                    }

                    if (errorMessage != null) {
                        item {
                            Text(
                                text = errorMessage ?: "",
                                color = BrandRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                if (debtAmountInput.isBlank() || parsedAmount <= 0.0 || debtMemberName.isBlank()) {
                                    errorMessage = "Vui lòng nhập đầy đủ số tiền và tên người liên quan!"
                                    return@Button
                                }
                                val ok = onAddDebt(parsedAmount, debtMemberName, debtType, debtNote)
                                if (ok) {
                                    debtAmountInput = ""
                                    debtMemberName = ""
                                    debtNote = ""
                                    errorMessage = null
                                    activeSubTab = if (debtType == "borrow") DebtSubTab.PAY else DebtSubTab.RECEIVE
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(0.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                        ) {
                            Text(
                                text = "LƯU VÀO SỔ NỢ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            DebtSubTab.PAY -> {
                if (activeBorrowDebts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không có khoản nợ nào cần trả.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activeBorrowDebts) { item ->
                            val memberName = members.find { it.id == item.memberId }?.name
                                ?: item.memberNameSnapshot.ifBlank { "Thành viên cũ" }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = MoneyUtil.formatCurrency(item.amount),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandRed
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Trả cho: $memberName",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (item.note.isNotBlank()) {
                                            Text(
                                                text = item.note,
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Text(
                                            text = item.date,
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Button(
                                        onClick = { onSettleDebt(item.id) },
                                        shape = RoundedCornerShape(0.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandRed)
                                    ) {
                                        Text(
                                            text = "ĐÃ TRẢ",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            DebtSubTab.RECEIVE -> {
                if (activeLendDebts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không có ai đang nợ bạn cả.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activeLendDebts) { item ->
                            val memberName = members.find { it.id == item.memberId }?.name
                                ?: item.memberNameSnapshot.ifBlank { "Thành viên cũ" }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = MoneyUtil.formatCurrency(item.amount),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandBlue
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Thu từ: $memberName",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (item.note.isNotBlank()) {
                                            Text(
                                                text = item.note,
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Text(
                                            text = item.date,
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Button(
                                        onClick = { onSettleDebt(item.id) },
                                        shape = RoundedCornerShape(0.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                                    ) {
                                        Text(
                                            text = "ĐÃ THU",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            DebtSubTab.HISTORY -> {
                if (settledDebts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lịch sử ghi nợ trống.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(settledDebts) { item ->
                            val memberName = members.find { it.id == item.memberId }?.name
                                ?: item.memberNameSnapshot.ifBlank { "Thành viên cũ" }
                            val isBorrow = item.type == "borrow"

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = MoneyUtil.formatCurrency(item.amount),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = TextDecoration.LineThrough,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = (if (isBorrow) "Đã trả: " else "Đã thu: ") + memberName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (item.note.isNotBlank()) {
                                            Text(
                                                text = item.note,
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Text(
                                            text = item.date + " • Đã tất toán",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    IconButton(onClick = { onDeleteDebt(item.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Xóa",
                                            tint = BrandRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            DebtSubTab.SETTINGS -> {
                SettingsScreen(
                    settings = settings,
                    members = members,
                    onUpdateSettings = onUpdateSettings
                )
            }
        }
    }
}
