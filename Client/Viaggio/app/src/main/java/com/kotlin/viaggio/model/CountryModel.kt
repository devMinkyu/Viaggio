package com.kotlin.viaggio.model

import com.google.gson.Gson
import com.kotlin.viaggio.data.obj.Country
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.ViaggioApiService
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryModel @Inject constructor() : BaseModel() {
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var api: ViaggioApiService
    @Inject
    lateinit var gson: Gson

    fun createCountries(countries: List<Country>) =
        Completable.fromAction {
            db.get().countryDao().insertAllCountry(*countries.toTypedArray())
        }.subscribeOn(Schedulers.io())

    fun createCountry(country: Country) =
        Completable.fromAction {
            db.get().countryDao().insertCountry(country)
        }.subscribeOn(Schedulers.io())

    fun getCountries(kind: Int): Single<List<Country>> {
        return db.get().countryDao().getCountries(kind).subscribeOn(Schedulers.io())
    }

    fun updateCountry(country: Country) =
        Completable.fromAction {

        }

    fun getDataFetchCountries() =
        api.getCountries().subscribeOn(Schedulers.io())
            .flatMap {
                if (it.isSuccessful) {
                    Single.just(it.body()!!.contries)
                } else {
                    Single.just(listOf())
                }
            }

    fun getDataFetchDomestics() =
        api.getDomestics().subscribeOn(Schedulers.io())
            .flatMap {
                if(it.isSuccessful) {
                    Single.just(it.body()!!.domestics)
                } else {
                    Single.just(listOf())
                }
            }
}