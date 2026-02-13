package com.example.qa3at.ui.screens.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qa3at.R
import com.example.qa3at.domain.model.PaymentMethod
import com.example.qa3at.ui.components.PrimaryButton
import com.example.qa3at.ui.components.Qa3atTopBar

data class PaymentOption(
    val method: PaymentMethod,
    val nameRes: Int,
    val descRes: Int,
    val icon: ImageVector
)

@Composable
fun PaymentScreen(
    bookingId: String,
    onPaymentSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val paymentOptions = listOf(
        PaymentOption(
            method = PaymentMethod.CARD,
            nameRes = R.string.payment_card,
            descRes = R.string.payment_card_desc,
            icon = Icons.Filled.CreditCard
        ),
        PaymentOption(
            method = PaymentMethod.MADA,
            nameRes = R.string.payment_mada,
            descRes = R.string.payment_mada_desc,
            icon = Icons.Filled.CreditCard
        ),
        PaymentOption(
            method = PaymentMethod.APPLE_PAY,
            nameRes = R.string.payment_apple_pay,
            descRes = R.string.payment_apple_pay_desc,
            icon = Icons.Filled.PhoneAndroid
        ),
        PaymentOption(
            method = PaymentMethod.BANK_TRANSFER,
            nameRes = R.string.payment_bank,
            descRes = R.string.payment_bank_desc,
            icon = Icons.Filled.AccountBalance
        )
    )

    val totalAmount = 80500.0

    Scaffold(
        topBar = {
            Qa3atTopBar(
                title = stringResource(R.string.payment),
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.total_to_pay),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "BHD ${totalAmount.toInt()}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        PrimaryButton(
                            text = stringResource(R.string.pay_now),
                            onClick = {
                                isProcessing = true
                                onPaymentSuccess()
                            },
                            modifier = Modifier.width(160.dp),
                            enabled = selectedMethod != null,
                            isLoading = isProcessing
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.select_payment_method),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            paymentOptions.forEach { option ->
                PaymentMethodCard(
                    option = option,
                    isSelected = selectedMethod == option.method,
                    onClick = { selectedMethod = option.method }
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.payment_info),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.payment_info_text),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun PaymentMethodCard(
    option: PaymentOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(option.nameRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(option.descRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
