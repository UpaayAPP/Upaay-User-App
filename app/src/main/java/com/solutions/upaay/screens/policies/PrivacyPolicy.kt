package com.solutions.upaay.screens.policies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Privacy Policy - Upaay Website and Application",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(text = "By using our Services and the Platform or by otherwise giving us your information, you agree to the terms of this Privacy Policy. You also expressly consent to our use and disclosure of your Personal Information (as defined below) in the manner prescribed under this Privacy Policy and further signify your agreement to this Privacy Policy and the Terms of Use. If you do not agree to this Privacy Policy, do not subscribe to the Services, use the Platform or give us any of your information in any manner whatsoever.")

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "1. COLLECTION OF INFORMATION")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "1.1 We may collect and process information from you, through your use of the Platform, or which is provided to one of our partners or through any engagement with us. We may collect and process the personal information provided by you, including but not limited to:")
        Text(text = "• Information that you voluntarily provide, including but not limited to any information that identifies or can be used to identify, contact or locate the user such as name, phone number, gender, photograph, date of birth, time of birth and place of birth.")
        Text(text = "• Any data that is automatically captured by the Platform such as your mobile phone operating system, every computer / mobile device connected to the internet is given a domain name and a set of numbers Registered that serve as that computer's Internet Protocol or \"IP\" address. When you request a page from any page within the Platform, our web servers automatically recognize your domain name and IP address to help us identify your location. The domain name and IP address reveal nothing personal about you other than the IP address from which you have accessed the Platform.")
        Text(text = "• Contacts List. We access the contacts list on your mobile device. We always ask for your consent before accessing your contacts list and you have the option to deny us access to your contacts list.")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Hereinafter, collectively referred to as \"Personal Information\".")

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "1.2 It is clarified that in the event you make any payments through the Platform, we will not store any payment or card-related information which you may provide while making such payments, such as card number, account number, validity date, expiry date or CVV number.")

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "1.3 Using Upaay Website and Application means accepting the Privacy Policy which dictates how personal information of an individual is collected, used, and protected. You give consent to be contacted via Email, SMS, WhatsApp, and other media. I have utilized permissions in manifest file – RECORD_AUDIO, BLUETOOTH_CONNECT, BACKGROUND_SERVICE etc. which require justification to google play store, so add that – for use of webrtc, these permissions were required.")

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "2. USE OF INFORMATION COLLECTED")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "2.1 Use of the Information for Services")
        Text(text = "The primary goal of the Upaay in collecting the information is to provide you a platform for the purpose of providing the Services. Upaay may use the Personal Information provided by you in the following ways:")
        Text(text = "• To help provide you the Services.")
        Text(text = "• To observe, improve and administer the quality of Service.")
        Text(text = "• To analyze how the Platform is used, diagnose technical problems.")
        Text(text = "• Remember the basic information provided by you for effective access.")
        Text(text = "• To confirm your identity in order to determine your eligibility to use the Platform and avail the Services.")
        Text(text = "• To notify you about any changes to the Platform.")
        Text(text = "• To enable Upaay to comply with its legal and regulatory obligations.")
        Text(text = "• To send administrative notices and Service-related alerts and similar communication, as detailed under this Privacy Policy, with a view to optimizing the efficiency of the Platform.")
        Text(text = "• Doing market research, troubleshooting, protection against error, project planning, fraud and other criminal activity; and")
        Text(text = "• To enforce the Upaay’s Terms of Use.")
        Text(text = "• To connect you with other Platform users through various features of the Platform.")
        Text(text = "• In accordance with TRAI regulations, we may reach out to users registered on the National Do Not Call (DND) registry through calls and SMS for essential communications related to our services.")

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "2.3 Cookies")
        Text(text = "Cookies are small portions of information saved by your browser onto your computer / mobile. Cookies are used to record various aspects of your visit and assist Upaay to provide you with uninterrupted service.")
        Text(text = "We may use information collected from our Cookies to identify user behavior and to serve content and offers based on your profile, to personalize your experience and in order to enhance the convenience of the users of our Platform.")
        Text(text = "The user acknowledges and agrees that third party service providers may use Cookies or similar technologies to collect information about the user’s pattern of availing the Services, in order to inform, optimize, and provide advertisements based on the user’s visits on the Platform and general browsing pattern and report how third-party service providers advertisement impressions, other uses of advertisement services, and interactions with these impressions and services are in relation to the user’s visits on such third party’s website.")
        Text(text = "We neither have access to, nor do the Privacy Policy or Terms of Use govern the use of Cookies or other tracking technologies that may be placed by third party service providers on the Platform. These parties may permit the user to opt out of tailored advertisement at any time, however, to the extent advertising technology is integrated into the Services, the user may still receive advertisements and related updates even if they choose to opt-out of tailored advertising. We assume no responsibility or liability whatsoever for the user’s receipt or use of such tailored advertisements.")

        // Add next sections: 3. SHARING OF INFORMATION, 4. SECURITY OF INFORMATION, etc.

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "YOU HAVE READ THIS PRIVACY AND AGREE TO ALL OF THE PROVISIONS CONTAINED ABOVE.",
            fontWeight = FontWeight.Bold
        )
    }
}
