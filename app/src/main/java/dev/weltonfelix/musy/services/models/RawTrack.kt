package dev.weltonfelix.musy.services.models

data class RawTrackResponse(
	val tracks: RawTracksItems
)

data class RawTracksItems(
	val items: List<RawTrack>
)

data class RawTrack(
	val name: String,
	val duration_ms: Int,
	val artists: List<RawArtist>,
	val album: RawAlbum,
	val external_urls: RawExternalUrls,
)

data class RawArtist(val name: String)

data class RawAlbum(val images: List<RawImage>)

data class RawExternalUrls(val spotify: String)

data class RawImage(val url: String)
