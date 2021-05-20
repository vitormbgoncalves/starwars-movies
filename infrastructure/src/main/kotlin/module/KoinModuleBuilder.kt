package com.github.vitormbgoncalves.starwarsmovies.infrastructure.module

import com.github.vitormbgoncalves.starwarsmovies.core.usecases.repository.IMovieRepository
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.service.IMovieService
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.service.MovieServiceImpl
import com.github.vitormbgoncalves.starwarsmovies.database.MongoDBMovieRepository
import com.github.vitormbgoncalves.starwarsmovies.interfaces.MovieController
import org.koin.core.module.Module
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

/**
 * Koin modules file
 *
 * @author Vitor Goncalves
 * @since 17.05.2021, seg, 18:38
 */
val KoinModuleBuilder: Module = module(createdAtStart = true) {
  single { MovieController(get()) }

  // Services
  single<IMovieService> { MovieServiceImpl(get()) }

  // Repositories
  single<IMovieRepository> { MongoDBMovieRepository(get()) }
  single {
    KMongo.createClient(
      "mongodb+srv://user:minhasenhasecreta@starwars-movies.woimo.mongodb.net/" +
        "movies?retryWrites=true&w=majority"
    ).coroutine
  }
}
