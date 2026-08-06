package com.civicconnect.presentation.screens.report.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civicconnect.presentation.screens.components.CivicConnectTextField

@Composable
fun ReportForm(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    severity: String,
    onSeverityChange: (String) -> Unit,
    isAiGenerating: Boolean,
    onGenerateDescription: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CivicConnectTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Complaint Title"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            CivicConnectTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = "Incident Description"
            )
            TextButton(
                onClick = onGenerateDescription,
                modifier = Modifier.align(Alignment.End),
                enabled = !isAiGenerating && title.isNotBlank()
            ) {
                if (isAiGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Smart Generate with AI",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                DropdownSelector(
                    label = "Category",
                    options = listOf("road", "water", "electricity", "garbage", "sanitation", "other"),
                    selectedOption = category,
                    onOptionSelected = onCategoryChange
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                DropdownSelector(
                    label = "Severity",
                    options = listOf("Low", "Medium", "High", "Critical"),
                    selectedOption = severity,
                    onOptionSelected = onSeverityChange
                )
            }
        }
    }
}
