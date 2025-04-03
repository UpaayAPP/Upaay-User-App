package com.solutions.upaay.globalComponents.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CrackinTextField(
    modifier: Modifier = Modifier,
    value: MutableState<TextFieldValue>,
    label: String,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isPassword: Boolean = false,
    isNumeric: Boolean = false,
    isEmail: Boolean = false,
    isEnabled: Boolean = true,
) {
    val passwordVisible = remember { mutableStateOf(false) }
    val isLabelLarge by remember { derivedStateOf { value.value.text.isEmpty() } }

    val finalKeyboardOptions = when {
        isPassword -> KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = keyboardOptions.imeAction
        )

        isNumeric -> KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = keyboardOptions.imeAction
        )

        isEmail -> KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = keyboardOptions.imeAction
        )

        else -> keyboardOptions
    }

    TextField(
        value = value.value,
        enabled = isEnabled,
        onValueChange = { value.value = it },
        textStyle = MaterialTheme.typography.titleSmall,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
        ),
        label = {
            Text(
                label,
                style = if (isLabelLarge) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodySmall
            )
        },
        placeholder = {
            if (placeholder != null) {
                Text(placeholder, style = MaterialTheme.typography.labelSmall)
            }
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon ?: if (isPassword) {
            {
                val image =
                    if (passwordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(
                        imageVector = image,
                        contentDescription = if (passwordVisible.value) "Hide password" else "Show password",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible.value) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = finalKeyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier.fillMaxWidth(),
    )
}
