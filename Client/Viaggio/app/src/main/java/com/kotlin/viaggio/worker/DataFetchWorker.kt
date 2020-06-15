package com.kotlin.viaggio.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.kotlin.viaggio.data.obj.*
import com.kotlin.viaggio.model.CountryModel
import com.kotlin.viaggio.model.ThemeModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Function3
import java.io.InputStreamReader
import javax.inject.Inject

class DataFetchWorker @Inject constructor(context: Context, params: WorkerParameters) :
    BaseWorker(context, params) {
    @Inject
    lateinit var themeModel: ThemeModel

    @Inject
    lateinit var countryModel: CountryModel

    @Inject
    lateinit var gson: Gson

    @Suppress("UNCHECKED_CAST")
    override fun doWork(): Result {
        super.doWork()
        loadingLocal()
        return Result.success()
    }


    private fun loadingNetwork() {
        val countrySingle = countryModel.getDataFetchCountries()
        val domesticsSingle = countryModel.getDataFetchDomestics()
        val themeSingle = themeModel.getDataFetchTheme()

        Single.zip(countrySingle, domesticsSingle, themeSingle, Function3
        <List<Country>, List<Country>, List<Theme>, List<Any>>
        { t1, t2, t3 ->
            val domestics = t2.map {
                it.kind = 1
                it.continent = ""
                it.url = ""
                it
            }
            val country = t1.toMutableList()
            country.addAll(domestics)
            listOf(country, t3)
        }).flatMapCompletable {
            val countries = it[0] as List<Country>
            val themes = it[1] as List<Theme>

            val c1 = countryModel.createCountries(countries)
            val c3 = themeModel.createThemes(themes)

            Completable.merge(listOf(c1, c3))
        }.blockingAwait()
    }

    private fun loadingLocal() {
        val countryInputStream = InputStreamReader(applicationContext.assets.open("country.json"))
        val countries: ViaggioApiCountry =
            gson.fromJson(countryInputStream, ViaggioApiCountry::class.java)
        val list: MutableList<Country> = mutableListOf()
        for (datum in countries.countries) {
            list.add(datum)
        }

        val domesticsInputStream = InputStreamReader(applicationContext.assets.open("domestics.json"))
        val domestics: ViaggioApiDomestics =
            gson.fromJson(domesticsInputStream, ViaggioApiDomestics::class.java)
        val domesticsList = domestics.domestics.map {
            it.kind = 1
            it.continent = ""
            it.url = ""
            it
        }
        list.addAll(domesticsList)

        val inputStream1 = InputStreamReader(applicationContext.assets.open("theme.json"))
        val themes: ViaggioApiTheme = gson.fromJson(inputStream1, ViaggioApiTheme::class.java)
        val list1: List<Theme> = themes.themes

        val c1 = countryModel.createCountries(list)
        val c3 = themeModel.createThemes(list1)
        Completable.merge(listOf(c1, c3)).blockingAwait()

    }
}