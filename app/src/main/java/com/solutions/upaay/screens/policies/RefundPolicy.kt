package com.solutions.upaay.screens.policies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RefundPolicyScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "REFUND AND SHIPPING POLICY - UPAAY",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text("Upaay APP has a stringent refund policy that operates in accordance with the Indian consumer laws and the Consumer Protection Act of 2019 and IT Act of 2000, which states the following:")

        Spacer(modifier = Modifier.height(16.dp))
        Text("REFUND POLICY")
        Text("We don't offer a refund policy.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("SHIPPING POLICY")
        Text("All the products which are purchased Will delivered 2-4 working days.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("RETURN POLICY")
        Text("We offer 7 days of return policy from the date of Purchased.")
        Text("Damaged and defective products replacement/Exchange will be delivered within 5-7 business days")

        Spacer(modifier = Modifier.height(8.dp))
        Text("This Application is Owned by (Rishabh Sood)")

        Spacer(modifier = Modifier.height(16.dp))
        Text("EXPLANATION OF RETURN, REFUND, EXCHANGE AND SHIPPING POLICY.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("1. No Refunds Once an Order Has Been Processed")
        Text("Refunds will not be granted once an order (astrological report) reaches the ‘processing’ stage, where it has already been assigned to an astrologer.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("2. The Cancellation Window")
        Text("Users can initiate a request for cancellation by simply contacting customer care within 1 hour post payment. If a refund has to be issued, it will be at the discretion of the company.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("3. Technical Issues")
        Text("Delays or glitches in service delivery will not be valid grounds for requesting a refund.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("4. Incorrect Information")
        Text("No refunds will be entertained for mistakes made with the data provided by the user. Kindly check all details before proceeding to submission.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("5. Physical Products")
        Text("Damaged products cannot be returned after delivery. Items returned cash on delivery will have shipping and custom fees applied.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("6. Subscription Services")
        Text("Pro-rata refunds will be issued for delayed activation and will be processed within 10 business days.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("7. Consultation Quality")
        Text("Partial/full refunds (to Upaay Wallet) may be given if Network interruptions affect calls or chats. Consultants are unresponsive or display a lack of professionalism. Fluency is not as per the set standards of the profile. No refunds for accuracy-related problems.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("8. Multiple Payments")
        Text("Duplicate payments received (verification required) will be refunded without charges being incurred.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("9. Product Shipping")
        Text("Some physical products for example gemstones Kavach, Suraksha Kavach Evil-Eye band are only deliverable within India.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("Shipment Processing Time")
        Text("All orders are processed within 2-3 business days. Orders are not shipped or delivered on weekends or holidays. If we are experiencing a high volume of orders, shipments may be delayed by a few days. Please allow additional days in transit for delivery. If there is a significant delay in shipment of your order, we will contact you via email or telephone.")
    }
}
