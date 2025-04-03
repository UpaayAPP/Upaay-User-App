package com.solutions.upaay.utils.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.solutions.upaay.utils.loading.LoadingStateManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

data class UserProfile(
    var name: String = "",
    var email: String = "",
    var mobileNumber: String = "",
    var balance: Int? = null,
    var dateOfBirth: String? = null,
    var profileImageUrl: String? = null,
    var timeOfBirth: String? = null,
    var placeOfBirth: String? = null,
    var currentAddress: String? = null,
    var horoscope: String? = null,
    var languages: List<String> = emptyList(),
)

enum class ProfileState {
    USER_NOT_FOUND,
    PROFILE_INCOMPLETE,
    VALID_PROFILE
}

object ProfileUtils {

    // Global state for user profile
    val userProfileState = mutableStateOf(UserProfile())

    private var isProfileLoaded = false
    private var balanceListener: ListenerRegistration? = null

    // Check if user profile is incomplete and fetch user data
    suspend fun getUserProfileState(forceCheckAgain: Boolean = false): ProfileState {
        LoadingStateManager.showLoading()

//        val auth = FirebaseAuth.getInstance()
        val user = waitForFirebaseAuthUser() ?: return ProfileState.USER_NOT_FOUND
        val uid = user.uid

        val firestore = FirebaseFirestore.getInstance()
        return try {
            val userDocument = firestore.collection("users").document(uid).get(Source.SERVER).await()

            if (!userDocument.exists()) {
                // User document does not exist
                ProfileState.USER_NOT_FOUND
            } else {
                if (forceCheckAgain || !isProfileLoaded) {
                    val data = userDocument.data ?: return ProfileState.PROFILE_INCOMPLETE

                    // Map fetched data to userProfileState
                    userProfileState.value = UserProfile(
                        name = data["name"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        mobileNumber = data["mobileNumber"] as? String ?: "",
                        balance = (data["balance"] as? Number)?.toInt(),
                        profileImageUrl = data["profileImageUrl"] as? String,
                        dateOfBirth = data["dateOfBirth"] as? String,
                        timeOfBirth = data["timeOfBirth"] as? String,
                        placeOfBirth = data["placeOfBirth"] as? String,
                        currentAddress = data["currentAddress"] as? String,
                        horoscope = data["horoscope"] as? String,
                        languages = data["languages"] as? List<String> ?: emptyList(),
                    )
                    isProfileLoaded = true

                    startRealtimeBalanceUpdates(uid)
                }

                // Check if key fields are incomplete
                if (userProfileState.value.dateOfBirth == null ||
                    userProfileState.value.timeOfBirth == null ||
                    userProfileState.value.placeOfBirth == null ||
                    userProfileState.value.currentAddress == null ||
                    userProfileState.value.horoscope == null ||
                    userProfileState.value.languages.isEmpty()
                ) {
                    ProfileState.PROFILE_INCOMPLETE
                } else {
                    ProfileState.VALID_PROFILE
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileUtils", "Error checking user profile: ${e.message}")
            ProfileState.USER_NOT_FOUND
        } finally {
            LoadingStateManager.hideLoading()
        }
    }

    suspend fun doesUserExist(): Boolean {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return false

        return try {
            val userDocument = firestore.collection("users").document(uid).get().await()
            userDocument.exists() // Return true if the document exists, false otherwise
        } catch (e: Exception) {
            Log.e("ProfileUtils", "Error checking if user exists: ${e.message}")
            false // Assume user does not exist if an error occurs
        }
    }

    private suspend fun waitForFirebaseAuthUser(): FirebaseUser? = suspendCancellableCoroutine { continuation ->
        val auth = FirebaseAuth.getInstance()

        lateinit var listener: FirebaseAuth.AuthStateListener

        listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                continuation.resume(user, null)
                auth.removeAuthStateListener(listener) // ✅ No error now
            }
        }

        auth.addAuthStateListener(listener)

        continuation.invokeOnCancellation {
            auth.removeAuthStateListener(listener)
        }
    }



    private fun startRealtimeBalanceUpdates(uid: String) {
        balanceListener?.remove()

        val userRef = FirebaseFirestore.getInstance().collection("users").document(uid)
        balanceListener = userRef.addSnapshotListener { snapshot, _ ->
            val newBalance = snapshot?.getDouble("balance")?.toInt()
            if (newBalance != null) {
                userProfileState.value = userProfileState.value.copy(balance = newBalance)
            }
        }
    }

    fun resetProfile() {
        userProfileState.value = UserProfile()
        isProfileLoaded = false
    }
}
