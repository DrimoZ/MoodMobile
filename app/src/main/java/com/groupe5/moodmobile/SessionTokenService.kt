package com.groupe5.moodmobile

import android.util.Log
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.*

class SessionTokenService(secretKeyString: String) {

    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256).encoded


    fun createSessionToken(userId: String, role: String, isSessionOnly: Boolean): String {
        val validityHours = if (isSessionOnly) 1L else 24L  // 1 heure pour une session, 24 heures sinon
        val message = "token!"
        Log.d("MonTag", message)

        return Jwts.builder()
            .setSubject(userId)
            .claim("role", role)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + validityHours * 60 * 60 * 1000))
            .signWith(SignatureAlgorithm.HS256, Keys.hmacShaKeyFor(secretKey))
            .compact()
    }
}
