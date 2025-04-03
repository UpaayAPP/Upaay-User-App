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
fun TermsAndConditionsScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Terms and Conditions - Upaay Website and Application",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(text = "These Terms and Conditions of Use will apply to all customers of Upaay App and Website in reference to the content and services provided in the website http://www.upaay.app and the associated applications (hereinafter referred to as Website, Upaay Website and Application, We, Us or Our). Upon accessing or using the Website, you (\"User,\" “You,” “Your”) agree to these Terms of Usage alongside our Privacy Policy and Refund Policy. Furthermore, we reserve the right to put any other terms and conditions as long as they are provided by law, including but not limited to, Indian Contract Act 1872 and Information Technology Act, 2000. If you do not agree with these terms, please refrain from using the Website.")

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Updates and Amendments")
        Text(text = "Terms of Usage shall be subject to amendments which may be done at the unmitigated discretion of Upaay Services Private Limited. The Users assume the responsibility to review these terms periodically to ensure compliance with applicable laws and regulations. Continued use of the Website signifies your acceptance of any changes.")

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "User Consent")
        Text(text = "By accessing and using the services on the Website means that you accept, without reservation, these Terms of Usage. If you do not accept these conditions, please refrain from registering or using the services. Your use of the Website, including any services offered for free or payment, is your acceptance of the legally binding provisions of these terms. (Upaay Website and Application) is not liable for any misuse by minors or unauthorized access to your account.")

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Acceptance Of Terms")
        Text(text = "By downloading and/or by registering or signing up for these Services, or otherwise having access to, receiving, and/or using the Platform, you acknowledge to have read, understood and consented to be governed and bound by these Terms of Use and the Privacy Policy. If you do not accept or agree to any part of these Terms of Use or the Privacy Policy, your usage of the Services will be terminated.")

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "General Description")
        Text(text = "Upaay is a Content Service Provider specializing in the production of astrological articles, reports, and offering consultations over the phone, video, or via email (collectively referred to as “Content”). The Website offers a combination of complimentary and paying features (“Services”). Free Services do not require registration while Paid Services will allow registered members access to those features. By registering, you accept the responsibility for providing accurate, current, and complete information, as well as ensuring its accuracy.")

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Registration and Eligibility")
        Text(text = "To utilize the Upaay service, one should be a minimum of 18 years of age, or of a legal age of consent, and must be able to enter into a legally binding agreement under the Indian Contract Act of 1872. Account security and confidentiality rest solely with the account holder, as do all activities associated with the account. Upaay accepts no responsibility for any abusive actions undertaken by minors or any unauthorized use of your account.")

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Your Responsibilities")
        Text(text = "By using the Platform, you represent and warrant that:")
        Text(text = "• You have fully read and understood the Terms of Use and Privacy Policy and consent to them.\n• You will ensure that your use of the Platform and/or Services will not violate any applicable law or regulation.\n• you have no rights in, or to, the Platform or the technology used or supported by the Platform or any of the Services, other than the right to use each of them in accordance with these Terms of Use.\n• You will not use the Platform or the Services in any manner inconsistent with these Terms of Use or Privacy Policy.\n• you will not resell or make any commercial use of the Services or use the Services in any way that is unlawful, for any unlawful purpose, or in a manner that your use harms us, the Platform, or any other person or entity, as determined in our sole discretion, or act fraudulently or maliciously;\n• you will not decipher, decompile, disassemble, reverse engineer or otherwise attempt to derive any hardware, or source code or underlying ideas or algorithms of any part of the Service (including without limitation any application or widget), except to the limited extent applicable laws specifically prohibit such restriction.\n• you will not transmit or make available any software or other computer files that contain a virus or other harmful component, or otherwise impair or damage the Platform or any connected network, or otherwise damage, disable, overburden, impair or compromise the Platform, our systems or security or interfere with any person or entity's use or enjoyment of the Platform;\n• you will not post, publish or transmit any content or messages that (i) are false, misleading, defamatory, harmful, threatening, abusive or constitute harassment (ii) promote racism, entail hateful slurs or promote hateful behavior, associate with hate groups or any violence towards others including terrorism or self-harm or suicide or harm against any individual or group or religion or caste, (iii) infringe another's rights including any intellectual property rights or copyright or trademark, violate or encourage any conduct that would violate any applicable law or regulation or would give rise to civil liability, or (iv) depict or encourage profanity, nudity, inappropriate clothing, sexual acts, sexually suggestive poses or degrade or objectify people, whether in the nature of a prank, entertainment or otherwise.\n• you will not promote the use of explosives or firearms, the consumption of psychotropic drugs or any other illegal activities.\n• You will not disparage, make false or malicious statements against us or in connection with the Services or the Platform.\n• you will not interfere or attempt to interfere with the proper working of the Platform, or any activities conducted on the Platform.\n• you will not bypass any measures we may use to prevent or restrict access to the Services.\n• you will not run any form of autoresponder or “spam” on the Platform.\n• you will not use manual or automated software, devices, or other processes to “crawl” or “spider” any part of the Services.\n• you will not modify, adapt, appropriate, reproduce, distribute, translate, create derivative works or adaptations of, publicly display, republish, repurpose, sell, trade, or in any way exploit the Service, except as expressly authorized by us.\n• You will not delete or modify any content of the Services, including but not limited to, legal notices, disclaimers, or proprietary notices such as copyright or trademark symbols, logos, that you do not own or have express permission to modify.")

        // Continue adding remaining sections (Payment Terms, Termination, Disclaimer, Contact etc.)

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "YOU HAVE FULLY READ AND UNDERSTOOD THESE TERMS OF USE AND VOLUNTARILY AGREE TO ALL OF THE PROVISIONS CONTAINED ABOVE.",
            fontWeight = FontWeight.Bold
        )
    }
}
