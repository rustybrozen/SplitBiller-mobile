package com.example.anhpansplitter.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anhpansplitter.model.Bill
import com.example.anhpansplitter.model.Member
import com.example.anhpansplitter.ui.theme.BrandBlue
import com.example.anhpansplitter.util.MoneyUtil



@Composable
fun HistoryScreen(
    bills: List<Bill>,
    members: List<Member>,
    modifier: Modifier = Modifier
) {
    val closedBills = bills.filter { it.status == "closed" }
    var selectedBill by remember { mutableStateOf<Bill?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "LỊCH SỬ HÓA ĐƠN",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (closedBills.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có lịch sử giao dịch.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(closedBills.reversed(), key = { it.id }) { bill ->
                    val payerName = bill.memberSnapshot[bill.payer]
                        ?: members.find { it.id == bill.payer }?.name
                        ?: "Người dùng đã xóa"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                            .clickable { selectedBill = bill }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bill.description,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Người trả: $payerName • ${bill.date}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Text(
                            text = MoneyUtil.formatVnd(bill.amount),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BrandBlue
                        )
                    }
                }
            }
        }
    }

    if (selectedBill != null) {
        val b = selectedBill!!
        val payerName = b.memberSnapshot[b.payer] ?: members.find { it.id == b.payer }?.name ?: "Người dùng"

        AlertDialog(
            onDismissRequest = { selectedBill = null },
            shape = RoundedCornerShape(0.dp),
            title = {
                Text(b.description, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    Text("Tổng tiền: ${MoneyUtil.formatVnd(b.amount)}", fontWeight = FontWeight.Bold, color = BrandBlue)
                    Text("Người trả: $payerName", fontSize = 13.sp)
                    Text("Ngày: ${b.date}", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Danh sách chia (${b.selectedMembers.size} người):", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    b.selectedMembers.forEach { id ->
                        val mName = b.memberSnapshot[id] ?: members.find { it.id == id }?.name ?: "Thành viên"
                        val shareWeight = b.shares[id] ?: 1.0
                        Text("- $mName ($shareWeight phần)", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedBill = null },
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("ĐÓNG")
                }
            }
        )
    }
}
