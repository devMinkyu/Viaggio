package com.kotlin.viaggio.aws

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider
import com.amazonaws.regions.Regions

class DeveloperAuthenticationProvider(accountId: String?, identityPoolId: String, region: Regions) :
    AWSAbstractCognitoDeveloperIdentityProvider(accountId, identityPoolId, region) {
    private val developerProvider = "login.viaggio.viaggio-dev"
    override fun getProviderName(): String {
        return developerProvider
    }

    fun setInfo(identityId: String, token: String) {
        setToken(token)
        setIdentityId(identityId)
    }
}