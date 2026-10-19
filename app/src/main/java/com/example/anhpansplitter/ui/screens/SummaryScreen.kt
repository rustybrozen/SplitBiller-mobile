package com.example.anhpansplitter.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anhpansplitter.model.GroupedTransfer
import com.example.anhpansplitter.model.Transfer
import com.example.anhpansplitter.ui.theme.BrandBlue
import com.example.anhpansplitter.ui.theme.BrandDark
import com.example.anhpansplitter.util.MoneyUtil



@Composable
fun SummaryScreen(
    groupedTransfers: List<GroupedTransfer>,
    totalTransfers: List<Transfer>,
    onSettleAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val summaryText = buildString {
        groupedTransfers.forEach { group ->
            append("Chuyển khoản cho ${group.receiverName}:\n")
            group.senders.forEach { s ->
                append("- ${s.name}: ${MoneyUtil.formatVnd(s.amount)}\n")
            }
            append("\n")
        }
    }.trim()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TỔNG KẾT CÔNG NỢ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 1.sp
            )

            Text(
                text = "(${totalTransfers.size} giao dịch)",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (totalTransfers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có dữ liệu công nợ hoặc đã cấn trừ xong!",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(groupedTransfers, key = { it.receiverId }) { group ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Chuyển khoản cho ${group.receiverName}:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BrandBlue
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        group.senders.forEach { sender ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "- ${sender.name}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = MoneyUtil.formatVnd(sender.amount),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Summary", summaryText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Đã sao chép nội dung đòi nợ!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("SAO CHÉP NỘI DUNG", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, summaryText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ qua")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Chia sẻ")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSettleAll,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                colors = ButtonDefaults.buttonColors(containerColor = BrandDark)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Tất toán")
                Spacer(modifier = Modifier.width(8.dp))
                Text("XÁC NHẬN ĐÃ TẤT TOÁN XONG", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
