package com.example.anhpansplitter.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anhpansplitter.model.Bill
import com.example.anhpansplitter.model.Member
import com.example.anhpansplitter.model.SplitType
import com.example.anhpansplitter.ui.theme.BrandAmber
import com.example.anhpansplitter.ui.theme.BrandBlue
import com.example.anhpansplitter.ui.theme.BrandRed
import com.example.anhpansplitter.util.MoneyUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToLong



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BillsScreen(
    bills: List<Bill>,
    members: List<Member>,
    onSaveBill: (Bill) -> Unit,
    onDeleteBill: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var desc by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var selectedPayerId by remember { mutableStateOf<Long?>(members.firstOrNull()?.id) }
    var selectedMemberIds by remember(members) { mutableStateOf(members.map { it.id }.toSet()) }
    val shares = remember { mutableStateMapOf<Long, Double>() }
    var errorText by remember { mutableStateOf<String?>(null) }

    val parsedAmount = MoneyUtil.parseSmartMoney(amountStr)
    val openBills = bills.filter { it.status == "open" }

    val totalShares = selectedMemberIds.sumOf { shares[it] ?: 1.0 }
    val estPerShare = if (parsedAmount > 0 && totalShares > 0) parsedAmount / totalShares else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "THÊM HÓA ĐƠN MỚI",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = desc,
                    onValueChange = {
                        desc = it
                        errorText = null
                    },
                    label = { Text("Nội dung (VD: Ăn lẩu, Xem phim...)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = {
                        amountStr = it
                        errorText = null
                    },
                    label = { Text("Tổng tiền (VD: 150k, 1.5tr, 200000)") },
                    supportingText = {
                        if (parsedAmount > 0) {
                            Text(
                                text = "= ${MoneyUtil.formatVnd(parsedAmount)}",
                                color = BrandBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    singleLine = true
                )

                // Quick amount chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("50k", "100k", "200k", "500k", "1tr").forEach { chip ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .clickable {
                                    amountStr = chip
                                    errorText = null
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = chip,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "NGƯỜI THANH TOÁN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                )
                Spacer(modifier = Modifier.height(4.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    members.forEach { m ->
                        val isPayer = selectedPayerId == m.id
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = if (isPayer) BrandBlue else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(0.dp)
                                )
                                .background(if (isPayer) BrandBlue else MaterialTheme.colorScheme.surface)
                                .clickable { selectedPayerId = m.id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = m.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPayer) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "THÀNH VIÊN THAM GIA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue
                    )

                    Text(
                        text = if (selectedMemberIds.size == members.size) "Bỏ chọn hết" else "Chọn tất cả",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue,
                        modifier = Modifier.clickable {
                            selectedMemberIds = if (selectedMemberIds.size == members.size) {
                                emptySet()
                            } else {
                                members.map { it.id }.toSet()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                members.forEach { m ->
                    val isChecked = selectedMemberIds.contains(m.id)
                    val currentShare = shares[m.id] ?: 1.0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .border(1.dp, if (isChecked) BrandBlue else MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                            .background(if (isChecked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                selectedMemberIds = if (checked) selectedMemberIds + m.id else selectedMemberIds - m.id
                            },
                            colors = CheckboxDefaults.colors(checkedColor = BrandBlue)
                        )

                        Text(
                            text = m.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (isChecked) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(MaterialTheme.colorScheme.background)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                                        .clickable {
                                            val next = (currentShare - 0.5).coerceAtLeast(0.5)
                                            shares[m.id] = (next * 10).roundToLong() / 10.0
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$currentShare phần",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(MaterialTheme.colorScheme.background)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                                        .clickable {
                                            val next = currentShare + 0.5
                                            shares[m.id] = (next * 10).roundToLong() / 10.0
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                if (selectedMemberIds.isNotEmpty() && parsedAmount > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .border(1.dp, BrandBlue.copy(alpha = 0.3f), RoundedCornerShape(0.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tổng: $totalShares phần",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ước tính: ~${MoneyUtil.formatVnd(estPerShare)} / phần",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        )
                    }
                }

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorText ?: "",
                        color = BrandRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (desc.isBlank()) {
                            errorText = "Vui lòng nhập nội dung hóa đơn!"
                            return@Button
                        }
                        if (parsedAmount <= 0) {
                            errorText = "Số tiền phải lớn hơn 0!"
                            return@Button
                        }
                        if (selectedPayerId == null) {
                            errorText = "Vui lòng chọn người thanh toán!"
                            return@Button
                        }
                        if (selectedMemberIds.isEmpty()) {
                            errorText = "Vui lòng chọn ít nhất 1 người tham gia!"
                            return@Button
                        }

                        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        val newBill = Bill(
                            id = System.currentTimeMillis(),
                            amount = parsedAmount,
                            description = desc.trim(),
                            payer = selectedPayerId!!,
                            date = dateStr,
                            splitType = SplitType.EQUAL,
                            selectedMembers = selectedMemberIds.toList(),
                            shares = shares.toMap()
                        )

                        onSaveBill(newBill)
                        desc = ""
                        amountStr = ""
                        errorText = null
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("LƯU HÓA ĐƠN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "CHỜ THANH TOÁN (${openBills.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (openBills.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Đã hoàn tất! Không có hóa đơn nào đang chờ.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(openBills.reversed(), key = { it.id }) { bill ->
                val payerName = members.find { it.id == bill.payer }?.name ?: "Người dùng"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bill.description,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$payerName thanh toán • ${bill.date}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = MoneyUtil.formatVnd(bill.amount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = { onDeleteBill(bill.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = BrandRed.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
