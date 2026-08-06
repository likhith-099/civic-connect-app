package com.civicconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.work.*
import com.civicconnect.data.local.PreferenceManager
import com.civicconnect.data.sync.SyncWorker
import com.civicconnect.domain.usecase.auth.GetSessionUseCase
import com.civicconnect.domain.usecase.auth.GetUserRoleUseCase
import com.civicconnect.presentation.MainScreen
import com.civicconnect.presentation.theme.CivicConnectTheme
import com.civicconnect.utils.ConnectivityObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var getSessionUseCase: GetSessionUseCase

    @Inject
    lateinit var getUserRoleUseCase: GetUserRoleUseCase

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by preferenceManager.isDarkMode.collectAsState(initial = false)
            val networkStatus by connectivityObserver.observe().collectAsState(initial = ConnectivityObserver.Status.Unavailable)
            
            LaunchedEffect(networkStatus) {
                if (networkStatus == ConnectivityObserver.Status.Available) {
                    val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                        .build()
                    WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                        "sync_work",
                        ExistingWorkPolicy.KEEP,
                        syncRequest
                    )
                }
            }

            CivicConnectTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        getSessionUseCase = getSessionUseCase,
                        getUserRoleUseCase = getUserRoleUseCase,
                        connectivityObserver = connectivityObserver
                    )
                }
            }
        }
    }
}
