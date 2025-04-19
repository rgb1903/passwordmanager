package com.example.passwordmanager.common.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class EncryptionHelper @Inject constructor(
    private val context: Context
) {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val keyGenerator = KeyGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_AES,
        "AndroidKeyStore"
    )

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("password_key", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "password_key",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            setUserAuthenticationRequired(false)
            setRandomizedEncryptionRequired(true)
        }.build()

        return keyGenerator.apply {
            init(keyGenParameterSpec)
        }.generateKey()
    }

    fun encrypt(text: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val iv = cipher.iv
        val encrypted = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

        return Base64.encodeToString(iv + encrypted, Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String): String {
        val decoded = Base64.decode(encryptedText, Base64.DEFAULT)
        val iv = decoded.copyOfRange(0, 16)
        val encrypted = decoded.copyOfRange(16, decoded.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    companion object {
        private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    }
}