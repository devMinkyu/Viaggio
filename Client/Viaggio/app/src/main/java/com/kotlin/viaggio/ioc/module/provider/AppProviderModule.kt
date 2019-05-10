@file:Suppress("unused")

package com.kotlin.viaggio.ioc.module.provider

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import androidx.room.Room
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.AppDatabase
import com.kotlin.viaggio.view.App
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes =
    [
        NetworkProviderModule::class
    ]
)
class AppProviderModule {
    @Provides
    @Singleton
    @Named("Application")
    internal fun provideApplicationContext(app: App): Context {
        return app.baseContext
    }

    @Provides
    @Named("ApiToken")
    internal fun provideApiToken(sp: SharedPreferences): String {
        return sp.getString(AndroidPrefUtilService.Key.TOKEN_ID.name , "")!!
    }

    @Provides
    internal fun providePreferenceManager(@Named("Application") context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    internal fun provideRes(@Named("Application") context: Context): Resources {
        return context.resources
    }

    @Provides
    @Singleton
    internal fun provideAppDataBase(
        @Named("Application") context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "viaggio-android-db"
        )
            .build()
    }

    @Provides
    @Singleton
    internal fun provideFirebaseMlkitDeviceOcr() = FirebaseVision.getInstance().onDeviceTextRecognizer

    @Provides
    @Singleton
    internal fun provideFirebaseMlkitCloudOcr() = FirebaseVisionCloudTextRecognizerOptions.Builder()
        .setLanguageHints(Arrays.asList("en", "ko"))
        .build()

    @Provides
    @Singleton
    internal fun provideCognito(): DeveloperAuthenticationProvider {
        return DeveloperAuthenticationProvider(
            null, // Context
            BuildConfig.S3_POOL_ID,
            Regions.AP_NORTHEAST_2
        )
    }

    @Provides
    @Singleton
    internal fun provideCredential(@Named("Application") context: Context , developerProvider: DeveloperAuthenticationProvider) =
        CognitoCachingCredentialsProvider(
            context,
            developerProvider,
            Regions.AP_NORTHEAST_2
        )

    @Provides
    @Singleton
    internal fun provideAmazonS3(credential: CognitoCachingCredentialsProvider) :AmazonS3Client{
        val s3 = AmazonS3Client(credential)
        s3.setRegion(Region.getRegion(Regions.US_EAST_2))
        s3.endpoint = "s3.us-east-2.amazonaws.com"
        return s3
    }

    @Provides
    @Singleton
    internal fun provideTransferUtility(@Named("Application") context: Context, s3Client: AmazonS3Client) =
        TransferUtility.builder()
            .context(context)
            .defaultBucket(BuildConfig.S3_UPLOAD_BUCKET)
            .s3Client(s3Client)
            .build()
}