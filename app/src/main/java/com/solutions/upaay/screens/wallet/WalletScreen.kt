package com.solutions.upaay.screens.wallet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.solutions.upaay.MainActivity
import com.solutions.upaay.screens.wallet.phonepe.model.PhonePeRequestModel
import com.solutions.upaay.screens.wallet.phonepe.retrofit.PhonePeApiClient
import com.solutions.upaay.utils.profile.ProfileUtils
import com.solutions.upaay.utils.translate.TranslatedText
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

val rechargePlans = arrayOf(50, 100, 150, 200, 500, 1000, 2000, 10000, 50000)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(navController: NavController) {

    val context = LocalContext.current

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var selectedAmount by remember { mutableStateOf("") }

    // Store the current transaction ID so we can check status later
    var recentTransactionId by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK || result.resultCode == Activity.RESULT_CANCELED) {
            // 🔁 After PhonePe returns, check Firestore for status
            checkTransactionStatus(recentTransactionId) { status ->
                when (status) {
                    "SUCCESS" -> Toast.makeText(context, "Payment Successful!", Toast.LENGTH_SHORT).show()
                    "FAILED" -> Toast.makeText(context, "Payment Failed", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(context, "Payment is in processing, please wait..", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TopAppBar(
                    title = { Text("My Wallet") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                )

//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(5.dp, 10.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Go back",
//                        modifier = Modifier
//                            .padding(end = 8.dp)
//                            .size(22.dp)
//                            .clickable {
//                                navController.popBackStack()
//                            }
//                    )
//                    TranslatedText(
//                        text = "My Wallet",
//                        fontWeight = FontWeight.W400,
//                        style = MaterialTheme.typography.titleSmall,
//                    )
//                }

                TranslatedText(
                    text = "Available balance",
                    fontWeight = FontWeight.W300,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 25.dp)
                )

                TranslatedText(
                    text = "₹ ${ProfileUtils.userProfileState.value.balance.toString()}",
                    fontWeight = FontWeight.W400,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 5.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {

                    Button(
                        onClick = {
                            navController.navigate(MainActivity.RechargeHistoryScreenRoute)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Red
                        ),
                        border = BorderStroke(1.dp, Color.Red),
                        modifier = Modifier
                            .width(LocalConfiguration.current.screenWidthDp.dp / 2.5f)
//                            .height(37.dp)
                    ) {
                        TranslatedText(
                            text = "Recharge History",
                            fontSize = 13.sp,
                            color = Color.Red
                        )
                    }

                    Button(
                        onClick = { showSheet = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF2B9CFF),
                            contentColor = Color.White
                        ),
//                        border = BorderStroke(1.dp, Color.Red),
                        modifier = Modifier
                            .width(LocalConfiguration.current.screenWidthDp.dp / 2.3f)
//                            .height(37.dp)
                            .padding(start = 5.dp)
                    ) {
                        TranslatedText(
                            text = "Recharge",
                            fontSize = 14.sp,
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, start = 5.dp, end = 5.dp)
                        .border(1.dp, Color.Gray.copy(0.2f), RoundedCornerShape(5.dp))
                        .padding(horizontal = 0.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    TranslatedText(
                        text = "Available Plans",
                        color = Color.Black,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 5.dp, bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.Gray.copy(0.2f))
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .padding(top = 15.dp, bottom = 10.dp)
                            .fillParentMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {

                        rechargePlans.forEach {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(end = 10.dp)
                            ) {

                                Button(
                                    onClick = {
                                        selectedAmount = it.toString()
                                        showSheet = true
                                    }
                                    ,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color(0xFF36A023),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
//                                        .height(37.dp)
                                ) {
                                    TranslatedText(
                                        text = "₹${it}",
                                        fontSize = 14.sp,
                                    )
                                }

                                TranslatedText(
                                    text = "Get $it min",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 5.dp)
                                )
                            }
                        }
                    }
                }
                if (showSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showSheet = false },
                        sheetState = bottomSheetState
                    ) {
                        PhonePePaymentBottomSheet(
                            context = context,
                            launcher = launcher,
                            prefillAmount = selectedAmount,
                            onDismiss = { showSheet = false },
                            onPaymentStarted = {
                                showSheet = false
                                Toast.makeText(
                                    context,
                                    "Proceeding Securely...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

fun checkTransactionStatus(transactionId: String, onResult: (String) -> Unit) {
    FirebaseFirestore.getInstance()
        .collection("UserPaymentTransactions")
        .document(transactionId)
        .get()
        .addOnSuccessListener { doc ->
            val status = doc.getString("Callback.status") ?: "PENDING"
            onResult(status)
        }
        .addOnFailureListener {
            onResult("ERROR")
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhonePePaymentBottomSheet(
    context: Context,
    launcher: ActivityResultLauncher<Intent>,
    prefillAmount: String = "",
    onDismiss: () -> Unit,
    onPaymentStarted: () -> Unit
) {
    var amount by remember { mutableStateOf(prefillAmount) }
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter Amount", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { it.isDigit() } },
            placeholder = { Text("Amount") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Button(
            onClick = {
                scope.launch {

                    val firestore = FirebaseFirestore.getInstance()

                    // 🔒 Check if PhonePe is enabled
                    val isPhonePeEnabledSnapshot = firestore
                        .collection("utils")
                        .document("payments")
                        .get()
                        .await()

                    val isPhonePeEnabled = isPhonePeEnabledSnapshot.getBoolean("isPhonepeEnabled") ?: false

                    if (!isPhonePeEnabled) {
                        Toast.makeText(
                            context,
                            "Payment provider not available at this moment. Try again later.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }

                    val user = FirebaseAuth.getInstance().currentUser ?: return@launch
                    val transactionId = "TXN_${System.currentTimeMillis()}"
                    val phoneNumber = user.phoneNumber ?: "+919318345767"

                    val request = PhonePeRequestModel(
                        merchantTransactionId = transactionId,
                        merchantUserId = user.uid,
                        amount = amount.toInt(),
                        mobileNumber = phoneNumber,
                        targetApp = "PHONEPE"
                    )

                    try {
                        val response = PhonePeApiClient.retrofit.initiatePayment(request)
                        if (response.isSuccessful) {
                            val info = response.body()?.paymentInfo ?: return@launch

                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(
                                    "phonepe://pay?data=${info.payloadMain}&checksum=${info.checksum}&merchantId=${info.merchantId}&url=https://upi-callback"
                                )
                            }

                            val txn = mapOf(
                                "uid" to user.uid,
                                "amount" to request.amount,
                                "transactionId" to transactionId,
                                "createdAt" to FieldValue.serverTimestamp()
                            )

                            FirebaseFirestore.getInstance()
                                .collection("UserPaymentTransactions")
                                .document(transactionId)
                                .set(txn)
                                .addOnSuccessListener {
                                    launcher.launch(intent) // ✅ Launch PhonePe using launcher
                                    onPaymentStarted()
                                }
                        } else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("PHONEPE_ERROR", "Error : $e")
                        e.printStackTrace()
                    }
                }
            },
            enabled = amount.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B9CFF)),
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Proceed To Payment", color = Color.White)
        }
    }
}

fun storeInitialTransaction(uid: String, txnId: String, amount: Int) {
    val txnRef = FirebaseFirestore.getInstance()
        .collection("UserPaymentTransactions")
        .document(txnId)

    val txnData = mapOf(
        "uid" to uid,
        "amount" to amount,
        "status" to "INITIATED",
        "createdAt" to FieldValue.serverTimestamp()
    )

    txnRef.set(txnData)
}

suspend fun initiatePhonePePayment(context: Context, amount: Int) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val txnId = "TXN_${System.currentTimeMillis()}"
    val phoneNumber =
        ProfileUtils.userProfileState.value.mobileNumber ?: "+919318345767" // fallback if null

    // 🔹 Store transaction before initiating gateway
    storeInitialTransaction(uid, txnId, amount)

    val body = mapOf(
        "merchantTransactionId" to txnId,
        "merchantUserId" to uid,
        "amount" to amount,
        "mobileNumber" to phoneNumber,
        "targetApp" to "PHONEPE" // optional if using PAY_PAGE instead of UPI_INTENT
    )

    try {
        val client = OkHttpClient()
        val json = JSONObject(body).toString()
        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://asia-south1-upaay-001.cloudfunctions.net/phonepe/paymentinfo")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val resJson = JSONObject(response.body?.string() ?: "")
            val paymentInfo = resJson.getJSONObject("Payment_Info")

            val payloadMain = paymentInfo.getString("payloadMain")
            val checksum = paymentInfo.getString("checksum")
            val merchantId = paymentInfo.getString("merchantId")

            // 🔹 Deep Link intent to PhonePe
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("phonepe://pay?data=$payloadMain&checksum=$checksum&merchantId=$merchantId&url=https://upi-callback")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Failed to initiate payment", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
