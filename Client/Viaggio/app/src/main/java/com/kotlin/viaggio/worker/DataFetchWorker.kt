package com.kotlin.viaggio.worker

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import com.kotlin.viaggio.data.obj.Country
import com.kotlin.viaggio.data.obj.Theme
import com.kotlin.viaggio.model.CountryModel
import com.kotlin.viaggio.model.ThemeModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Function3
import javax.inject.Inject

class DataFetchWorker @Inject constructor(context: Context, params: WorkerParameters) : BaseWorker(context, params) {
    @Inject
    lateinit var themeModel: ThemeModel
    @Inject
    lateinit var countryModel: CountryModel

    @Suppress("UNCHECKED_CAST")
    override fun doWork(): Result {
        super.doWork()
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
        return Result.success()
    }
}