package com.kotlin.viaggio.aws

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider
import com.amazonaws.regions.Regions
import com.kotlin.viaggio.BuildConfig

class DeveloperAuthenticationProvider(accountId: String?, identityPoolId: String, region: Regions) :
    AWSAbstractCognitoDeveloperIdentityProvider(accountId, identityPoolId, region) {
    override fun getProviderName(): String {
        return BuildConfig.AWS_LOGIN_PROVIDER
    }

    fun setInfo(identityId: String, token: String) {
        setToken(token)
        setIdentityId(identityId)
    }
}