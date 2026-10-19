package com.example.anhpansplitter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anhpansplitter.model.AppMode
import com.example.anhpansplitter.model.AppTab
import com.example.anhpansplitter.ui.theme.BrandAmber
import com.example.anhpansplitter.ui.theme.BrandBlue
import com.example.anhpansplitter.ui.theme.BrandDark
import com.example.anhpansplitter.ui.theme.BrandRed



@Composable
fun AppTopBar(
    currentMode: AppMode,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSplit = currentMode == AppMode.SPLIT_BILLER
    val modeName = if (isSplit) "SPLITBILLER" else "DEBTBOOK"
    val targetModeName = if (isSplit) "DebtBook" else "SplitBiller"
    val subText = if (isSplit) "Sổ nợ" else "Chia bill"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ANHPAN",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BrandBlue,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = modeName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 1.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .clickable { onToggleMode() }
                .padding(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = targetModeName.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Switch Mode",
                    tint = BrandBlue,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = subText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}



@Composable
fun AppBottomBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    memberCount: Int,
    openBillsCount: Int,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == AppTab.MEMBERS,
            onClick = { onTabSelected(AppTab.MEMBERS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (memberCount > 0) {
                            Badge(containerColor = BrandBlue, contentColor = Color.White) {
                                Text(memberCount.toString(), fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Thanh vien")
                }
            },
            label = { Text("Thành viên", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = BrandBlue,
                indicatorColor = BrandBlue,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            selected = currentTab == AppTab.BILLS,
            onClick = { onTabSelected(AppTab.BILLS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (openBillsCount > 0) {
                            Badge(containerColor = BrandRed, contentColor = Color.White) {
                                Text(openBillsCount.toString(), fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Hóa đơn")
                }
            },
            label = { Text("Hóa đơn", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = BrandBlue,
                indicatorColor = BrandBlue,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            selected = currentTab == AppTab.SUMMARY,
            onClick = { onTabSelected(AppTab.SUMMARY) },
            icon = {
                BadgedBox(
                    badge = {
                        if (openBillsCount > 0) {
                            Badge(containerColor = BrandAmber)
                        }
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Tổng kết")
                }
            },
            label = { Text("Tổng kết", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = BrandBlue,
                indicatorColor = BrandBlue,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            selected = currentTab == AppTab.HISTORY,
            onClick = { onTabSelected(AppTab.HISTORY) },
            icon = {
                Icon(Icons.Default.DateRange, contentDescription = "Lịch sử")
            },
            label = { Text("Lịch sử", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = BrandBlue,
                indicatorColor = BrandBlue,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            selected = currentTab == AppTab.SETTINGS,
            onClick = { onTabSelected(AppTab.SETTINGS) },
            icon = {
                Icon(Icons.Default.Settings, contentDescription = "Cài đặt")
            },
            label = { Text("Cài đặt", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = BrandBlue,
                indicatorColor = BrandBlue,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}
