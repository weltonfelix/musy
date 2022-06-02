package dev.weltonfelix.musy.services

import android.util.Log
import dev.weltonfelix.musy.BuildConfig
import dev.weltonfelix.musy.services.models.RawArtist
import dev.weltonfelix.musy.services.models.RawToken
import dev.weltonfelix.musy.services.models.RawTrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

object Constants {
	const val BASE_URL = BuildConfig.SPOTIFY_BASE_URL
	const val AUTH_URL = BuildConfig.SPOTIFY_AUTH_URL
	private const val CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID
	private const val CLIENT_SECRET = BuildConfig.SPOTIFY_CLIENT_SECRET

	val base64Credentials: String =
		Base64.getEncoder()
			.encodeToString("${CLIENT_ID}:${CLIENT_SECRET}".toByteArray())
}


interface SpotifyApiService {
	@FormUrlEncoded
	@POST("${Constants.AUTH_URL}token")
	fun auth(
		@Header("authorization") credentialsToken: String = "Basic ${Constants.base64Credentials}",
		@Field("grant_type") grantType: String = "client_credentials"
	): Call<RawToken>

	@GET("search")
	fun search(
		@Header("authorization") token: String,
		@Query("q") query: String,
		@Query("type") type: String = "track",
	): Call<RawTrackResponse>

	companion object {
		fun create(): SpotifyApiService {
			val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
				.baseUrl(Constants.BASE_URL).build()

			return retrofit.create(SpotifyApiService::class.java)
		}
	}
}

class SpotifyApi {
	private val service: SpotifyApiService = SpotifyApiService.create()
	private lateinit var token: String

	data class Track(
		val title: String,
		val spotifyURL: String,
		val artist: String,
		val durationSeconds: Int,
		val albumCoverURL: String
	)

	private fun formatArtists(artists: List<RawArtist>): String {
		val artistNames = artists.map { it.name }
		return artistNames.joinToString(", ")
	}

	fun auth(callback: (() -> Unit)? = null) {
		service.auth().enqueue(object : Callback<RawToken> {
			override fun onFailure(call: Call<RawToken>, t: Throwable) {
				Log.e("SpotifyApi", "Error on auth: ${t.message}")

				if (callback != null) {
					callback()
				}
				return
			}

			override fun onResponse(call: Call<RawToken>, response: Response<RawToken>) {
				val rawToken = response.body()?.access_token

				if (rawToken == null) {
					Log.e("SpotifyApi", "Error on auth (Request): ${call.request()}")
					Log.e("SpotifyApi", "Error on auth (Response): $response")
				} else {
					token = "Bearer $rawToken"
				}

				if (callback != null) {
					callback()
				}
				return
			}
		})
	}

	fun search(
		query: String,
		onResponse: (List<Track>) -> Unit,
		onException: ((Throwable) -> Unit)? = null,
		lastTry: Boolean = false
	) {
		if (!::token.isInitialized) {
			if (!lastTry) {
				Log.w("SpotifyApi", "Token not initialized. Retrying...")
				auth {
					search(query, onResponse, onException, lastTry = true)
				}
			} else {
				Log.e("SpotifyApi", "Error on search: No token. Aborting...")
				onException?.invoke(Exception("SpotifyApi: Token not found"))
			}
			return
		}


		service.search(token, query).enqueue(
			object : Callback<RawTrackResponse> {
				override fun onFailure(call: Call<RawTrackResponse>, t: Throwable) {
					Log.e("SpotifyApi", "Error on search: ${t.message}")
					onException?.invoke(t)
				}

				override fun onResponse(
					call: Call<RawTrackResponse>,
					response: Response<RawTrackResponse>
				) {
					val rawTrackResponse = response.body()

					if (rawTrackResponse == null) {
						Log.e("SpotifyApi", "Error on search (Request): ${call.request()}")
						Log.e("SpotifyApi", "Error on search (Response): $response")

						onException?.invoke(Exception("Error on search"))
						return
					}

					val tracks = mutableListOf<Track>()

					rawTrackResponse.tracks.items.forEach {
						tracks.add(
							Track(
								title = it.name,
								spotifyURL = it.external_urls.spotify,
								artist = formatArtists(it.artists),
								durationSeconds = (it.duration_ms / 1000),
								albumCoverURL = it.album.images.first().url
							)
						)
					}

					onResponse(tracks)
				}
			})
	}
}
