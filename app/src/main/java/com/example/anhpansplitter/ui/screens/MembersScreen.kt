package com.example.anhpansplitter.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anhpansplitter.model.Member
import com.example.anhpansplitter.ui.theme.BrandBlue
import com.example.anhpansplitter.ui.theme.BrandRed



@Composable
fun MembersScreen(
    members: List<Member>,
    onAddMember: (String) -> Boolean,
    onRemoveMember: (Long) -> Boolean,
    modifier: Modifier = Modifier
) {
    var newMemberName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "THÀNH VIÊN (${members.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newMemberName,
                onValueChange = {
                    newMemberName = it
                    errorMessage = null
                },
                placeholder = { Text("Nhập tên thành viên...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(0.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (onAddMember(newMemberName)) {
                        newMemberName = ""
                        errorMessage = null
                    } else {
                        errorMessage = "Tên đã tồn tại hoặc không hợp lệ!"
                    }
                },
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm")
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage ?: "",
                color = BrandRed,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (members.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có thành viên nào. Vui lòng thêm mới!",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(members, key = { it.id }) { m ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, BrandBlue.copy(alpha = 0.5f), RoundedCornerShape(0.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = m.name.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = BrandBlue,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = m.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                onRemoveMember(m.id)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = BrandRed.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
