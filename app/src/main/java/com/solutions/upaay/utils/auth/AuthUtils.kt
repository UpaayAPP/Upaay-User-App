package com.solutions.upaay.utils.auth

import com.google.firebase.auth.FirebaseAuth

object AuthUtils {
    /**
     * Check if the user is authenticated.
     * @return True if the user is authenticated, False otherwise.
     */
    fun isUserAuthenticated(): Boolean {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser != null
    }
}
