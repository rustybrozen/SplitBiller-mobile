package com.example.anhpansplitter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.anhpansplitter.model.AppMode
import com.example.anhpansplitter.model.AppTab
import com.example.anhpansplitter.ui.components.AppBottomBar
import com.example.anhpansplitter.ui.components.AppTopBar
import com.example.anhpansplitter.ui.screens.BillsScreen
import com.example.anhpansplitter.ui.screens.DebtScreen
import com.example.anhpansplitter.ui.screens.HistoryScreen
import com.example.anhpansplitter.ui.screens.MembersScreen
import com.example.anhpansplitter.ui.screens.SettingsScreen
import com.example.anhpansplitter.ui.screens.SummaryScreen
import com.example.anhpansplitter.ui.theme.AnhpanSplitterTheme
import com.example.anhpansplitter.viewmodel.MainViewModel




class MainActivity : ComponentActivity() {

    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Sync default mode from settings
        if (viewModel.settings.defaultApp == "debt") {
            viewModel.setMode(AppMode.DEBT_BOOK)
        }

        setContent {
            AnhpanSplitterTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}




@Composable
fun MainApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                currentMode = viewModel.currentMode,
                onToggleMode = {
                    val nextMode = if (viewModel.currentMode == AppMode.SPLIT_BILLER) {
                        AppMode.DEBT_BOOK
                    } else {
                        AppMode.SPLIT_BILLER
                    }
                    viewModel.setMode(nextMode)
                }
            )
        },
        bottomBar = {
            if (viewModel.currentMode == AppMode.SPLIT_BILLER) {
                AppBottomBar(
                    currentTab = viewModel.currentTab,
                    onTabSelected = { viewModel.setTab(it) },
                    memberCount = viewModel.members.size,
                    openBillsCount = viewModel.openBillsCount
                )
            }
        }
    ) { innerPadding ->
        if (viewModel.currentMode == AppMode.SPLIT_BILLER) {
            when (viewModel.currentTab) {
                AppTab.MEMBERS -> {
                    MembersScreen(
                        members = viewModel.members,
                        onAddMember = { viewModel.addMember(it) },
                        onRemoveMember = { viewModel.removeMember(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppTab.BILLS -> {
                    BillsScreen(
                        bills = viewModel.bills,
                        members = viewModel.members,
                        onSaveBill = { viewModel.saveBill(it) },
                        onDeleteBill = { viewModel.deleteBill(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppTab.SUMMARY -> {
                    SummaryScreen(
                        groupedTransfers = viewModel.groupedTransfers,
                        totalTransfers = viewModel.calculateTransfers,
                        onSettleAll = { viewModel.settleAllBills() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppTab.HISTORY -> {
                    HistoryScreen(
                        bills = viewModel.bills,
                        members = viewModel.members,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppTab.SETTINGS -> {
                    SettingsScreen(
                        settings = viewModel.settings,
                        members = viewModel.members,
                        onUpdateSettings = { viewModel.updateSettings(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        } else {
            DebtScreen(
                debts = viewModel.debts,
                members = viewModel.members,
                settings = viewModel.settings,
                onUpdateSettings = { viewModel.updateSettings(it) },
                onAddDebt = { amount, name, type, note ->
                    viewModel.addDebt(amount, name, type, note)
                },
                onSettleDebt = { viewModel.settleDebt(it) },
                onDeleteDebt = { viewModel.deleteDebt(it) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}