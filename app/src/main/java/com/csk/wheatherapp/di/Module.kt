package com.csk.wheatherapp.di

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.csk.wheatherapp.data.repo.WeatherRepository
import com.csk.wheatherapp.data.local.AppDatabase
import com.csk.wheatherapp.data.remote.AppApi
import com.csk.wheatherapp.data.remote.NetworkInterceptor
import com.csk.wheatherapp.ui.viewmodel.WeatherViewModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val viewModelModule = module {
    single { WeatherViewModel(get()) }

}
val sharedPrefModule = module {
    single {
        getSharedPrefs(androidApplication())
    }
    single<SharedPreferences.Editor> {
        getSharedPrefs(androidApplication()).edit()
    }
}

fun getSharedPrefs(androidApplication: Application): SharedPreferences {
    return androidApplication.getSharedPreferences(
        "SelfLearn",
        android.content.Context.MODE_PRIVATE
    )
}

val netModule = module {
    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    fun provideInterceptor(
        androidApplication: Application,
        preferences: SharedPreferences
    ): NetworkInterceptor {
        return NetworkInterceptor(androidApplication)
    }

    fun provideLogger(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    fun provideHttpClient(
        cache: Cache,
        interceptor: NetworkInterceptor,
        logger: HttpLoggingInterceptor
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .cache(cache)
            .addInterceptor(logger)
            .addInterceptor(interceptor)
        return okHttpClientBuilder.build()
    }

    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).setLenient().create()
    }

    single { provideCache(androidApplication()) }
    single { provideInterceptor(androidApplication(), get()) }
    single { provideLogger() }
    single { provideHttpClient(get(), get(), get()) }
    single { provideGson() }

}


val apiModule = module {
    fun provideRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create(factory))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
    }
    single { provideRetrofit(get(), get()) }
    fun provideUserApi(retrofit: Retrofit): AppApi {
        return retrofit.create(AppApi::class.java)
    }
    single { provideUserApi(get()) }
}


val databaseModule = module {
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "Weather.database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
    single { provideDatabase(androidApplication()) }


/*
    fun provideExamDao(database: AppDatabase): ExamDao {
        return database.examDao
    }
    single { provideExamDao(get()) }*/
}

val repositoryModule = module {

    fun provideSubjectsRepository(
        api: AppApi,
        dao: AppDatabase,
        preferences: SharedPreferences
    ): WeatherRepository {
        return WeatherRepository(api, dao)
    }
    single { provideSubjectsRepository(get(), get(), get()) }
}