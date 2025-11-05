package com.example.pawshearts.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen() {
    var currentView by remember { mutableStateOf("menu") }
    var amount by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }


    val moneyHistory = remember { mutableStateListOf<String>() }
    val itemHistory = remember { mutableStateListOf<String>() }

    val buttonColor = Color(0xFFE65100)
    val iconColor = Color(0xFFE65100)

    when (currentView) {
        "menu" -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Quyên Góp Quỹ Tình Nguyện",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "🐾 Paw & Heart 💖",
                    fontSize = 28.sp,
                    color = iconColor,
                    fontWeight = FontWeight.Bold
                )
                CardOption("Quyên góp tài chính", Icons.Default.MonetizationOn, iconColor) {
                    currentView = "moneyMenu"
                }
                CardOption("Quyên góp vật phẩm", Icons.Default.CardGiftcard, iconColor) {
                    currentView = "itemMenu"
                }
                CardOption("Đăng ký tình nguyện viên", Icons.Default.Person, iconColor) {
                    currentView = "volunteer"
                }
            }
        }

        "moneyMenu" -> {
            SubMenuScreen(
                title = "Quyên góp tài chính",
                onDonateClick = { currentView = "moneyDonate" },
                onHistoryClick = { currentView = "moneyHistory" },
                onBack = { currentView = "menu" }
            )
        }

        "moneyDonate" -> {
            DonateMoneyScreen(
                amount = amount,
                onAmountChange = { amount = it },
                onSubmit = {
                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                    moneyHistory.add("Đã quyên góp ${amount} VNĐ vào $date")
                    message = "Cảm ơn bạn đã quyên góp $amount VNĐ 💖"
                    showDialog = true
                    currentView = "moneyHistory"
                },
                onBack = { currentView = "moneyMenu" },
                buttonColor = buttonColor
            )
        }

        "moneyHistory" -> {
            HistoryScreen(
                title = "Lịch sử quyên góp tiền",
                list = moneyHistory,
                onBack = { currentView = "moneyMenu" },
                buttonColor = buttonColor
            )
        }

        "itemMenu" -> {
            SubMenuScreen(
                title = "Quyên góp vật phẩm",
                onDonateClick = { currentView = "itemDonate" },
                onHistoryClick = { currentView = "itemHistory" },
                onBack = { currentView = "menu" }
            )
        }

        "itemDonate" -> {
            val selectedItems = remember { mutableStateListOf<String>() }
            val customItems = remember { mutableStateMapOf<String, String>() }

            DonateItemScreenMulti(
                selectedItems = selectedItems,
                customItems = customItems,
                onSubmit = { finalItems ->
                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                    finalItems.forEach { item ->
                        itemHistory.add("Đã tặng: $item ($date)")
                    }
                    message = "Cảm ơn bạn đã tặng ${finalItems.joinToString(", ")} 🎁"
                    showDialog = true
                    currentView = "itemHistory"
                },
                onBack = { currentView = "itemMenu" },
                buttonColor = buttonColor
            )
        }

        "itemHistory" -> {
            HistoryScreen(
                title = "Lịch sử quyên góp vật phẩm",
                list = itemHistory,
                onBack = { currentView = "itemMenu" },
                buttonColor = buttonColor
            )
        }

        "volunteer" -> {
            VolunteerScreen(
                name = name,
                email = email,
                phone = phone,
                onNameChange = { name = it },
                onEmailChange = { email = it },
                onPhoneChange = { phone = it },
                onSubmit = {
                    message = "Cảm ơn $name đã đăng ký tình nguyện viên 🧡"
                    showDialog = true
                },
                onBack = { currentView = "menu" },
                buttonColor = buttonColor
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Đóng", color = buttonColor)
                }
            },
            title = { Text("Thông báo") },
            text = { Text(message, textAlign = TextAlign.Center) }
        )
    }


}

// ------------------------- DonateItemScreen multi-select -------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateItemScreenMulti(
    selectedItems: MutableList<String>,
    customItems: MutableMap<String, String>,
    onSubmit: (List<String>) -> Unit,
    onBack: () -> Unit,
    buttonColor: Color
) {
    val items = listOf("Thức ăn", "Cát vệ sinh", "Thuốc", "Đồ chơi", "Khác")
    val itemDetails = remember { mutableStateMapOf<String, String>() }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopBar("Quyên góp vật phẩm", onBack)

        items.forEach { item ->
            Column {
                AssistChip(
                    onClick = {
                        if (selectedItems.contains(item)) selectedItems.remove(item)
                        else selectedItems.add(item)
                    },
                    label = { Text(item) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (item) {
                                "Thức ăn" -> Icons.Default.ShoppingBag
                                "Cát vệ sinh" -> Icons.Default.Pets
                                "Thuốc" -> Icons.Default.MedicalServices
                                "Đồ chơi" -> Icons.Default.CardGiftcard
                                else -> Icons.Default.Edit
                            },
                            contentDescription = null
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedItems.contains(item)) buttonColor else Color(0xFFF1F1F1),
                        labelColor = if (selectedItems.contains(item)) Color.White else Color.Black
                    )
                )

                if (selectedItems.contains(item)) {
                    val currentText = if (item == "Khác") customItems[item] ?: "" else itemDetails[item] ?: ""
                    OutlinedTextField(
                        value = currentText,
                        onValueChange = { text ->
                            if (item == "Khác") customItems[item] = text else itemDetails[item] = text
                        },
                        label = {
                            if (item == "Khác") Text("Nhập loại vật phẩm khác")
                            else Text("Chi tiết $item")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // kiểm tra tất cả item bắt nhập đã điền chưa
        val canSubmit = selectedItems.isNotEmpty() &&
                selectedItems.all { item ->
                    if (item == "Khác") customItems[item]?.isNotBlank() == true
                    else itemDetails[item]?.isNotBlank() == true
                }

        Button(
            onClick = {
                val finalItems = selectedItems.map { item ->
                    if (item == "Khác") customItems[item] ?: item
                    else {
                        val detailText = itemDetails[item]?.takeIf { it.isNotBlank() }?.let { " ($it)" } ?: ""
                        item + detailText
                    }
                }
                onSubmit(finalItems)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            enabled = canSubmit
        ) {
            Text("Xác nhận quyên góp", color = Color.White)
        }
    }


}

// ------------------------- Các màn hình và composable khác -------------------------
@Composable
fun HistoryScreen(title: String, list: List<String>, onBack: () -> Unit, buttonColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar(title, onBack)
        Spacer(modifier = Modifier.height(8.dp))


        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (list.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Chưa có lịch sử quyên góp nào", color = Color.Gray)
                    }
                }
            } else {
                items(list) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(entry, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text("❤️ Cảm ơn tấm lòng của bạn!", color = buttonColor)
                        }
                    }
                }
            }
        }
    }


}

@Composable
fun DonateMoneyScreen(
    amount: String,
    onAmountChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    buttonColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TopBar("Quyên góp tài chính", onBack)
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text("Nhập số tiền (VNĐ)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )


        val canSubmit = amount.isNotBlank() && amount.toLongOrNull() != null

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            enabled = canSubmit
        ) {
            Text("Xác nhận quyên góp", color = Color.White)
        }
    }


}

@Composable
fun SubMenuScreen(
    title: String,
    onDonateClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TopBar(title, onBack)
        CardOption("Thực hiện quyên góp", Icons.Default.VolunteerActivism, Color(0xFFE65100), onDonateClick)
        CardOption("Lịch sử quyên góp", Icons.Default.History, Color(0xFFE65100), onHistoryClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardOption(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(36.dp))
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun VolunteerScreen(
    name: String,
    email: String,
    phone: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    buttonColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TopBar("Đăng ký tình nguyện viên", onBack)
        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Họ và tên") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Số điện thoại") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())


        val canSubmit = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank()

        Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = buttonColor), enabled = canSubmit) {
            Text("Đăng ký", color = Color.White)
        }
    }


}

@Composable
fun TopBar(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDonateScreen() {
    DonateScreen()
}
