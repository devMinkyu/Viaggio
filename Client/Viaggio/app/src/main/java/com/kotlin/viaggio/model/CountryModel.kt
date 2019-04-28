package com.kotlin.viaggio.model

import com.kotlin.viaggio.data.`object`.Country
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryModel @Inject constructor() : BaseModel() {

    fun createCountries(countries: List<Country>) =
        Completable.fromAction {
            db.get()
        }

    fun createCountry(country: Country) =
        Completable.fromAction {
            db.get()
        }

    fun getCountries() {

    }

    fun updateCountry(country: Country) =
        Completable.fromAction {

        }
}