package com.example.anhpansplitter.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anhpansplitter.model.AppSettings
import com.example.anhpansplitter.model.Member
import com.example.anhpansplitter.ui.theme.BrandBlue



@Composable
fun SettingsScreen(
    settings: AppSettings,
    members: List<Member>,
    onUpdateSettings: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "CÀI ĐẶT HỆ THỐNG",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Simplify Debts
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tối giản công nợ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tự động cấn trừ chéo để giảm số lần chuyển khoản",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Switch(
                    checked = settings.simplifyDebts,
                    onCheckedChange = { onUpdateSettings(settings.copy(simplifyDebts = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = BrandBlue)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Rounding Mode
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "CHẾ ĐỘ LÀM TRÒN TIỀN",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = BrandBlue
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("none" to "Tắt làm tròn", "smart" to "Thông minh (1k)").forEach { (mode, label) ->
                    val isSelected = settings.roundingMode == mode
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (isSelected) BrandBlue else MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                            .background(if (isSelected) BrandBlue.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
                            .clickable { onUpdateSettings(settings.copy(roundingMode = mode)) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BrandBlue else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Default App
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "TRANG MẶC ĐỊNH KHI MỞ APP",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = BrandBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tự động mở phần này khi khởi động ứng dụng",
                fontSize = 11.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("split" to "Chia Bill", "debt" to "Sổ Nợ").forEach { (app, label) ->
                    val isSelected = settings.defaultApp == app
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (isSelected) BrandBlue else MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                            .background(if (isSelected) BrandBlue else MaterialTheme.colorScheme.surface)
                            .clickable { onUpdateSettings(settings.copy(defaultApp = app)) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // VietQR bank
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "TÀI KHOẢN NGÂN HÀNG (VIETQR)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = BrandBlue
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = settings.bankAccountNo,
                onValueChange = { onUpdateSettings(settings.copy(bankAccountNo = it)) },
                label = { Text("Số tài khoản nhận") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = settings.bankAccountName,
                onValueChange = { onUpdateSettings(settings.copy(bankAccountName = it.uppercase())) },
                label = { Text("Tên chủ tài khoản (VIẾT HOA)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Version 3.3
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PHIÊN BẢN 3.3 • ANHPAN SPLITBILLER",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 2.sp
            )
        }
    }
}
