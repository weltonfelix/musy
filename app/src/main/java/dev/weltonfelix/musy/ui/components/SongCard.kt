package dev.weltonfelix.musy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.weltonfelix.musy.R

data class SongObject(
	val title: String,
	val spotifyURL: String,
	val artist: String,
	val durationInSeconds: Int,
	val coverImageURL: String,
)

fun formatDuration(duration: Int): String {
	val minutes = (duration / 60).toString()
	val seconds = (duration % 60).toString().padStart(2, '0')
	return "$minutes:$seconds"
}

@ExperimentalMaterial3Api
@Composable
fun SongCard(song: SongObject) {
	val uriHandler = LocalUriHandler.current

	val formattedDuration = formatDuration(song.durationInSeconds)

	val coverImageModel = ImageRequest.Builder(LocalContext.current).data(song.coverImageURL)
		.crossfade(true).build()
	val coverImageContentDescription = "${song.title} - ${song.artist}"

	fun handlePlayOnSpotify() {
		uriHandler.openUri(song.spotifyURL)
	}

	Card(modifier = Modifier.fillMaxWidth(), onClick = { handlePlayOnSpotify() }) {
		Row(
			modifier = Modifier
				.padding(8.dp)
				.height(IntrinsicSize.Min)
		) {
			AsyncImage(
				model = coverImageModel,
				contentDescription = coverImageContentDescription,
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.size(96.dp)
					.clip(RoundedCornerShape(12.dp)),
				placeholder = painterResource(
					id = R.drawable.album_placeholder
				)
			)

			Spacer(modifier = Modifier.width(16.dp))

			Column(
				verticalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxHeight()
			) {
				Column {
					Text(song.title, style = MaterialTheme.typography.titleMedium)
					Text(song.artist, style = MaterialTheme.typography.titleSmall)
				}
				Text(formattedDuration, style = MaterialTheme.typography.bodySmall)
			}
		}
	}
}

@ExperimentalMaterial3Api
@Preview(name = "Song Card")
@Composable()
fun PreviewSongCard() {
	SongCard(
		SongObject(
			title = "Never Gonna Give You Up",
			spotifyURL = "https://open.spotify.com/track/0sNOF9WDwhWunNAHPD3Baj",
			artist = "Rick Astley",
			durationInSeconds = 212,
			coverImageURL = "https://i.scdn.co/image/ab67616d0000b273f9c9c9c9c9c9c9c9c9c9c9c9c9"
		)
	)
}
