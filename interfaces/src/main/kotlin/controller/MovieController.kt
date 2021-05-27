package com.github.vitormbgoncalves.starwarsmovies.interfaces.controller

import com.github.vitormbgoncalves.starwarsmovies.core.usecases.service.IMovieService
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.PagingInfo
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ReponseAllMovies
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.RequestMovieDTO
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseMovieDTO
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.toMovie
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.toResponseMovieDTO

/**
 * Controller for movies
 *
 * @author Vitor Goncalves
 * @since 14.05.2021, sex, 17:16
 */

class MovieController(private val movieService: IMovieService) {
  suspend fun getAllMovies(page: Int, size: Int): List<ResponseMovieDTO> {
    return movieService.findAll(page, size).map { it.toResponseMovieDTO() }
  }

  suspend fun getMovie(id: String): ResponseMovieDTO? {
    return movieService.findById(id)?.toResponseMovieDTO()
  }

  suspend fun createMovie(movie: RequestMovieDTO): ResponseMovieDTO {
    return movieService.create(movie.toMovie()).toResponseMovieDTO()
  }

  suspend fun updateMovie(id: String, movie: RequestMovieDTO): ResponseMovieDTO? {
    return movieService.update(id, movie.toMovie())?.toResponseMovieDTO()
  }

  suspend fun deleteMovie(id: String) {
    return movieService.delete(id)
  }

  suspend fun getMoviesPage(page: Int, size: Int): ReponseAllMovies {
    return try {
      val results = movieService.findAll(page - 1, size).map { it.toResponseMovieDTO() }
      val totalMovies = movieService.totalMovies()
      val paginating = (totalMovies / size)
      val totalPages = if (totalMovies == 1L && size == 1 || paginating <= 1) 1 else paginating
      val next = if (page < totalPages.toInt()) page + 1 else null
      val prev = if (page > 1) page - 1 else null
      val info = PagingInfo(totalMovies.toInt(), totalPages.toInt(), next, prev)
      ReponseAllMovies(info, results)
    } catch (e: Exception) {
      throw IllegalArgumentException(e.localizedMessage)
    }
  }
}
